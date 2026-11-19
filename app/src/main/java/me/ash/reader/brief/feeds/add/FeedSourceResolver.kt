package me.ash.reader.brief.feeds.add

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Turns what is typed or pasted into the address of a feed: a subreddit (`r/psychiatry`), a Reddit
 * user, a YouTube channel (`@handle`, a channel, user or video link) or a YouTube playlist. Anything
 * else is handed on as it is (the feed finder in Read You's RssHelper already finds the feed of an
 * ordinary website). No account or key is needed: Reddit and YouTube both publish plain feeds.
 *
 * Not tested against the live sites yet (nothing here could reach them): the address shapes are the
 * ones the two sites have published for years; YouTube's channel page is read for the channel number,
 * which could change one day, so every lookup has a readable error. Brief You.
 */
object FeedSourceResolver {

    enum class Kind { Reddit, YouTube, YouTubePlaylist }

    /** What the text looks like, found without the network: [feedUrl] is null when a lookup is needed. */
    data class Detected(val kind: Kind, val title: String, val feedUrl: String?)

    private val REDDIT =
        Regex(
            "^(?:(?:https?://)?(?:www\\.|old\\.|new\\.|m\\.)?reddit\\.com)?/?(r|u|user)/([A-Za-z0-9_\\-]{2,24})(?:[/?#].*)?$",
            RegexOption.IGNORE_CASE,
        )
    private val YT_HOST = Regex("^(?:https?://)?(?:www\\.|m\\.|music\\.)?(?:youtube\\.com|youtu\\.be)(?:[/?#].*)?$", RegexOption.IGNORE_CASE)
    private val YT_FEED = Regex("youtube\\.com/feeds/videos\\.xml", RegexOption.IGNORE_CASE)
    private val YT_CHANNEL_ID = Regex("youtube\\.com/channel/(UC[\\w-]{22})", RegexOption.IGNORE_CASE)
    private val YT_PLAYLIST = Regex("[?&]list=((?:PL|UU|OL|LL|FL)[\\w-]{10,})", RegexOption.IGNORE_CASE)
    private val YT_HANDLE_BARE = Regex("^@([\\w.\\-]{3,30})$")
    private val YT_HANDLE_URL = Regex("youtube\\.com/(@[\\w.\\-]{3,30})", RegexOption.IGNORE_CASE)
    private val YT_NAMED = Regex("youtube\\.com/(?:c|user)/([\\w.\\-]+)", RegexOption.IGNORE_CASE)
    private val YT_VIDEO = Regex("(?:youtube\\.com/(?:watch|shorts/|live/)|youtu\\.be/)", RegexOption.IGNORE_CASE)

    private fun channelFeed(id: String) = "https://www.youtube.com/feeds/videos.xml?channel_id=$id"

    fun detect(input: String): Detected? {
        val text = input.trim()
        if (text.isEmpty() || text.contains(' ')) return null

        REDDIT.matchEntire(text)?.let { m ->
            val kind = m.groupValues[1].lowercase()
            val name = m.groupValues[2]
            return if (kind == "r") {
                Detected(Kind.Reddit, "r/$name", "https://www.reddit.com/r/$name/.rss")
            } else {
                Detected(Kind.Reddit, "u/$name", "https://www.reddit.com/user/$name/.rss")
            }
        }

        if (YT_FEED.containsMatchIn(text)) return Detected(Kind.YouTube, "YouTube", text.withScheme())
        if (YT_HOST.matches(text) || YT_HANDLE_BARE.matches(text)) {
            YT_CHANNEL_ID.find(text)?.let { return Detected(Kind.YouTube, "YouTube channel", channelFeed(it.groupValues[1])) }
            YT_PLAYLIST.find(text)?.let {
                if (!YT_VIDEO.containsMatchIn(text) || text.contains("playlist?", true)) {
                    return Detected(
                        Kind.YouTubePlaylist,
                        "YouTube playlist",
                        "https://www.youtube.com/feeds/videos.xml?playlist_id=${it.groupValues[1]}",
                    )
                }
            }
            YT_HANDLE_BARE.matchEntire(text)?.let { return Detected(Kind.YouTube, "@${it.groupValues[1]}", null) }
            YT_HANDLE_URL.find(text)?.let { return Detected(Kind.YouTube, it.groupValues[1], null) }
            YT_NAMED.find(text)?.let { return Detected(Kind.YouTube, it.groupValues[1], null) }
            if (YT_VIDEO.containsMatchIn(text)) return Detected(Kind.YouTube, "YouTube channel of this video", null)
        }
        return null
    }

    /**
     * The address to hand to the feed finder. For a channel handle, a channel's old name or a video,
     * the channel's page is read once for its channel number. Throws a readable [IOException] when
     * that fails.
     */
    suspend fun resolve(input: String): String {
        val text = input.trim()
        val found = detect(text) ?: return text
        found.feedUrl?.let { return it }
        return withContext(Dispatchers.IO) { channelFeedFromPage(pageUrl(text)) }
    }

    private fun pageUrl(text: String): String =
        when {
            YT_HANDLE_BARE.matches(text) -> "https://www.youtube.com/$text"
            else -> text.withScheme()
        }

    private fun String.withScheme() = if (startsWith("http", ignoreCase = true)) this else "https://$this"

    private val RSS_LINK =
        Regex("<link[^>]+type=\"application/rss\\+xml\"[^>]+href=\"([^\"]+)\"", RegexOption.IGNORE_CASE)
    private val EXTERNAL_ID = Regex("\"externalId\":\"(UC[\\w-]{22})\"")
    private val CHANNEL_ID = Regex("\"channelId\":\"(UC[\\w-]{22})\"")
    private val CHANNEL_PATH = Regex("/channel/(UC[\\w-]{22})")

    private fun channelFeedFromPage(url: String): String {
        val html = fetch(url)
        RSS_LINK.find(html)?.let { m ->
            val link = m.groupValues[1].replace("&amp;", "&")
            if (YT_FEED.containsMatchIn(link)) return link
        }
        for (re in listOf(EXTERNAL_ID, CHANNEL_ID, CHANNEL_PATH)) {
            re.find(html)?.let { return channelFeed(it.groupValues[1]) }
        }
        throw IOException("Couldn't find this channel's feed. Open the channel on YouTube, copy its link and paste it here.")
    }

    private fun fetch(url: String): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            connection.connectTimeout = 10_000
            connection.readTimeout = 15_000
            connection.instanceFollowRedirects = true
            connection.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120 Mobile Safari/537.36",
            )
            connection.setRequestProperty("Accept-Language", "en")
            if (connection.responseCode !in 200..299) throw IOException("YouTube answered ${connection.responseCode}")
            // The channel number is near the top of the page: no need to read all of it.
            val out = StringBuilder()
            connection.inputStream.bufferedReader().use { reader ->
                val buf = CharArray(8192)
                while (out.length < 1_500_000) {
                    val n = reader.read(buf)
                    if (n < 0) break
                    out.append(buf, 0, n)
                }
            }
            return out.toString()
        } finally {
            connection.disconnect()
        }
    }
}
