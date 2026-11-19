package me.ash.reader.brief.ui.style

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import com.materialkolor.PaletteStyle
import me.ash.reader.R

/** The colour-scheme styles Smart RSS offers; each is a Material Color Utilities variant. */
enum class SchemeStyle(val style: PaletteStyle, val title: Int, val desc: Int) {
    TonalSpot(PaletteStyle.TonalSpot, R.string.scheme_tonal_spot, R.string.scheme_tonal_spot_desc),
    Fidelity(PaletteStyle.Fidelity, R.string.scheme_fidelity, R.string.scheme_fidelity_desc),
    Neutral(PaletteStyle.Neutral, R.string.scheme_neutral, R.string.scheme_neutral_desc),
    Vibrant(PaletteStyle.Vibrant, R.string.scheme_vibrant, R.string.scheme_vibrant_desc),
    Expressive(PaletteStyle.Expressive, R.string.scheme_expressive, R.string.scheme_expressive_desc),
    Content(PaletteStyle.Content, R.string.scheme_content, R.string.scheme_content_desc),
    Rainbow(PaletteStyle.Rainbow, R.string.scheme_rainbow, R.string.scheme_rainbow_desc),
    FruitSalad(PaletteStyle.FruitSalad, R.string.scheme_fruit_salad, R.string.scheme_fruit_salad_desc),
    Monochrome(PaletteStyle.Monochrome, R.string.scheme_monochrome, R.string.scheme_monochrome_desc),
}

/** The overall shape language: Classic Material 3 (flat, small corners) or Material Expressive (the app's look so far). */
enum class DesignStyle(val title: Int, val desc: Int) {
    Classic(R.string.design_classic, R.string.design_classic_desc),
    Expressive(R.string.design_expressive, R.string.design_expressive_desc),
}

/** Contrast levels: standard, medium, high (Material's 0, 0.5 and 1). */
enum class ContrastLevel(val value: Double, val title: Int) {
    Standard(0.0, R.string.contrast_standard),
    Medium(0.5, R.string.contrast_medium),
    High(1.0, R.string.contrast_high),
}

/**
 * Brief You: the extra Color & Style choices (colour-scheme style, contrast, glass). Kept in their
 * own preferences file (`brief_style`) so Read You's settings are untouched. The values are
 * Compose state, so changing one redraws the app at once.
 */
object BriefStyle {
    private const val FILE = "brief_style"
    private var ready = false

    var scheme by mutableStateOf(SchemeStyle.TonalSpot)
        private set
    var contrast by mutableStateOf(ContrastLevel.Standard)
        private set
    var glass by mutableStateOf(false)
        private set
    var design by mutableStateOf(DesignStyle.Expressive)
        private set

    /** Reads the saved choices, once. Safe to call from anywhere, any number of times. */
    fun init(context: Context) {
        if (ready) return
        val prefs = context.applicationContext.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        scheme = SchemeStyle.entries.firstOrNull { it.name == prefs.getString("scheme", null) } ?: SchemeStyle.TonalSpot
        contrast = ContrastLevel.entries.firstOrNull { it.name == prefs.getString("contrast", null) } ?: ContrastLevel.Standard
        glass = prefs.getBoolean("glass", false)
        design = DesignStyle.entries.firstOrNull { it.name == prefs.getString("design", null) } ?: DesignStyle.Expressive
        ready = true
    }

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun setScheme(context: Context, value: SchemeStyle) {
        init(context); scheme = value
        prefs(context).edit { putString("scheme", value.name) }
    }

    fun setContrast(context: Context, value: ContrastLevel) {
        init(context); contrast = value
        prefs(context).edit { putString("contrast", value.name) }
    }

    fun setGlass(context: Context, value: Boolean) {
        init(context); glass = value
        prefs(context).edit { putBoolean("glass", value) }
    }

    fun setDesign(context: Context, value: DesignStyle) {
        init(context); design = value
        prefs(context).edit { putString("design", value.name) }
    }
}
