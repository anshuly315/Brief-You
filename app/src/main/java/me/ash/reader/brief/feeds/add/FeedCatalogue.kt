package me.ash.reader.brief.feeds.add

import android.content.Context
import androidx.annotation.StringRes
import me.ash.reader.R
import org.json.JSONObject

/** The ready-made list the Add Feed page searches and browses (assets/feed_catalogue.json). Brief You. */
object FeedCatalogue {

    /** The categories of the browse grid, in the order shown ("All" is not one of them). */
    enum class Category(val id: String, @StringRes val title: Int) {
        World("world", R.string.cat_world),
        Business("business", R.string.cat_business),
        Politics("politics", R.string.cat_politics),
        Lifestyle("lifestyle", R.string.cat_lifestyle),
        SciTech("scitech", R.string.cat_scitech),
        Sports("sports", R.string.cat_sports),
        Opinion("opinion", R.string.cat_opinion),
        Local("local", R.string.cat_local),
        Other("other", R.string.cat_other);

        companion object {
            fun of(id: String) = entries.firstOrNull { it.id == id } ?: Other
        }
    }

    data class Entry(
        val title: String,
        val url: String,
        val country: String,
        val category: Category,
        val language: String,
    ) {
        /** The address without its scheme, as a short second line. */
        val shortUrl: String get() = url.removePrefix("https://").removePrefix("http://")
    }

    const val ALL = ""
    const val INTERNATIONAL = "International"

    @Volatile private var cache: List<Entry>? = null

    fun all(context: Context): List<Entry> =
        cache
            ?: runCatching {
                    val text = context.applicationContext.assets.open("feed_catalogue.json").bufferedReader().use { it.readText() }
                    val array = JSONObject(text).getJSONArray("feeds")
                    List(array.length()) { i ->
                        val o = array.getJSONObject(i)
                        Entry(
                            title = o.getString("title"),
                            url = o.getString("url"),
                            country = o.optString("country", INTERNATIONAL),
                            category = Category.of(o.optString("category", "other")),
                            language = o.optString("language", "en"),
                        )
                    }
                }
                .getOrDefault(emptyList())
                .also { cache = it }

    fun countries(context: Context): List<String> =
        all(context).map { it.country }.filter { it != INTERNATIONAL }.distinct().sorted()

    fun languages(context: Context): List<String> = all(context).map { it.language }.distinct().sorted()

    /**
     * The entries for what is typed (every word must appear in the name, address, country or
     * category), narrowed to a category, a country (its own plus the international ones) and a
     * language. Empty [query] with no category gives nothing unless [listAll] (the "All" tile): the page shows the browse grid then.
     */
    fun find(
        context: Context,
        query: String,
        category: Category? = null,
        country: String = ALL,
        language: String = ALL,
        listAll: Boolean = false,
    ): List<Entry> {
        val words = query.trim().lowercase().split(' ').filter { it.isNotEmpty() }
        if (words.isEmpty() && category == null && !listAll) return emptyList()
        return all(context)
            .asSequence()
            .filter { category == null || it.category == category }
            .filter { country == ALL || it.country == country || it.country == INTERNATIONAL }
            .filter { language == ALL || it.language == language }
            .filter { e ->
                val hay = "${e.title} ${e.url} ${e.country}".lowercase()
                words.all { it in hay }
            }
            .toList()
    }
}

/** The country and language last chosen on the Add Feed page (kept for next time). */
object AddFeedPrefs {
    private const val FILE = "brief_add_feed"

    fun country(context: Context): String =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getString("country", FeedCatalogue.ALL) ?: FeedCatalogue.ALL

    fun language(context: Context): String =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getString("language", FeedCatalogue.ALL) ?: FeedCatalogue.ALL

    fun setCountry(context: Context, value: String) =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putString("country", value).apply()

    fun setLanguage(context: Context, value: String) =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putString("language", value).apply()

    /** The group picked on the page, applied when the found feed's settings open (see AddFeedDialog). */
    var pendingGroupId: String? = null
}
