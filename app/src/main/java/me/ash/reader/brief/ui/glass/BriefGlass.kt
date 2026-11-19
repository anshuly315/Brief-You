package me.ash.reader.brief.ui.glass

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import me.ash.reader.brief.ui.style.BriefStyle
import me.ash.reader.brief.ui.style.DesignStyle

/**
 * Brief You: the glass look's own settings (blur, frost, theme tint, saturation, refraction, outline).
 * Own preferences file (`brief_glass`); each value is Compose state, so the bars change as a slider
 * moves. A value of -1 means "automatic": SimpMusic's look in Expressive, a tinted flat look in Classic.
 */
object BriefGlass {
    private const val FILE = "brief_glass"
    private var ready = false

    var blur by mutableFloatStateOf(-1f); private set        // dp
    var frost by mutableFloatStateOf(-1f); private set       // percent of white (or black in dark)
    var tint by mutableFloatStateOf(-1f); private set        // percent of the theme colour
    var saturation by mutableFloatStateOf(-1f); private set  // percent
    var refraction by mutableIntStateOf(-1); private set     // -1 auto, 0 off, 1 on
    var outline by mutableIntStateOf(-1); private set        // -1 auto, 0 off, 1 on

    /** Glass on the article page too. Off by default: it flickers there while scrolling. */
    var readingGlass by androidx.compose.runtime.mutableStateOf(false); private set
    fun setReadingGlass(c: Context, v: Boolean) { init(c); readingGlass = v; prefs(c).edit { putBoolean("readingGlass", v) } }

    private val classic get() = BriefStyle.design == DesignStyle.Classic
    fun blurDp() = if (blur >= 0f) blur else 8f
    fun frostPct() = if (frost >= 0f) frost else if (classic) 12f else 27f
    fun tintPct() = if (tint >= 0f) tint else if (classic) 38f else 0f
    fun saturationPct() = if (saturation >= 0f) saturation else 150f
    fun refractionOn() = if (refraction >= 0) refraction == 1 else true
    fun outlineOn() = if (outline >= 0) outline == 1 else !classic

    fun init(context: Context) {
        if (ready) return
        val p = prefs(context)
        blur = p.getFloat("blur", -1f); frost = p.getFloat("frost", -1f); tint = p.getFloat("tint", -1f)
        saturation = p.getFloat("saturation", -1f); refraction = p.getInt("refraction", -1); outline = p.getInt("outline", -1)
        readingGlass = p.getBoolean("readingGlass", false)
        ready = true
    }

    private fun prefs(context: Context) = context.applicationContext.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun setBlur(c: Context, v: Float) { init(c); blur = v; prefs(c).edit { putFloat("blur", v) } }
    fun setFrost(c: Context, v: Float) { init(c); frost = v; prefs(c).edit { putFloat("frost", v) } }
    fun setTint(c: Context, v: Float) { init(c); tint = v; prefs(c).edit { putFloat("tint", v) } }
    fun setSaturation(c: Context, v: Float) { init(c); saturation = v; prefs(c).edit { putFloat("saturation", v) } }
    fun setRefraction(c: Context, v: Boolean) { init(c); refraction = if (v) 1 else 0; prefs(c).edit { putInt("refraction", refraction) } }
    fun setOutline(c: Context, v: Boolean) { init(c); outline = if (v) 1 else 0; prefs(c).edit { putInt("outline", outline) } }

    fun reset(c: Context) {
        init(c); blur = -1f; frost = -1f; tint = -1f; saturation = -1f; refraction = -1; outline = -1; readingGlass = false
        prefs(c).edit { clear() }
    }
}
