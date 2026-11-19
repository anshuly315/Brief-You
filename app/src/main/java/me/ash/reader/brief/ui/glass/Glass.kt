package me.ash.reader.brief.ui.glass

import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.colorControls
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.highlight.Highlight
import me.ash.reader.brief.ui.style.BriefStyle

/*
 * Brief You: the liquid-glass surface, the on/off switch version.
 *
 * Ported from SimpMusic (https://github.com/maxrave-dev/SimpMusic, GPL-3.0), whose
 * LiquidGlass.kt / LiquidGlassContainer.kt draw the same effect with Kyant's `backdrop` library.
 * The effect settings below are SimpMusic's. This is the static version: no press animation yet.
 *
 * How it works: one part of a page is the SOURCE ([glassSource], e.g. the article), and glass
 * surfaces drawn next to it (never inside it) refract what is behind them. With the switch off, or
 * when there is no source on screen, [GlassOrSurface] draws the ordinary Material surface instead.
 */

/** The backdrop of the glass source currently on screen (one reading page at a time). */
object GlassHost {
    var backdrop by mutableStateOf<LayerBackdrop?>(null)
}

/** Whether the glass look is on: the setting, and a phone that can draw it (Android 13+). */
@Composable
fun glassActive(): Boolean {
    BriefStyle.init(LocalContext.current)
    return BriefStyle.glass && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
}

/** Marks this part of the page as what glass surfaces next to it refract. */
@Composable
fun rememberGlassSource(): Modifier = Modifier.glassSource()

@Composable
fun Modifier.glassSource(): Modifier {
    if (!glassActive()) return this
    val background = MaterialTheme.colorScheme.surface
    val backdrop = rememberLayerBackdrop {
        drawRect(background)
        drawContent()
    }
    DisposableEffect(backdrop) {
        GlassHost.backdrop = backdrop
        onDispose { if (GlassHost.backdrop === backdrop) GlassHost.backdrop = null }
    }
    return this.layerBackdrop(backdrop)
}

/** The glass effect for any shape, drawn over [backdrop]. */
@Composable
fun Modifier.glass(backdrop: LayerBackdrop, shape: Shape): Modifier {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val scrim = if (isDark) Color.Black else Color.White
    return this.drawBackdrop(
        backdrop = backdrop,
        shape = { shape },
        highlight = { Highlight.Default },
        effects = {
            vibrancy()
            colorControls(brightness = 0.05f, contrast = 1f, saturation = 1.5f)
            blur(8f.dp.toPx())
            lens(size.minDimension / 4f, size.minDimension / 2f, false)
        },
        onDrawBackdrop = { drawBackdrop -> drawBackdrop() },
        onDrawSurface = { drawRect(scrim.copy(alpha = 0.27f)) },
    )
}

/** Glass when the switch is on and a source is on screen; otherwise the normal Material surface. */
@Composable
fun GlassOrSurface(
    shape: Shape,
    color: Color,
    contentColor: Color,
    tonalElevation: Dp = 0.dp,
    shadowElevation: Dp = 0.dp,
    content: @Composable () -> Unit,
) {
    val backdrop = GlassHost.backdrop
    if (glassActive() && backdrop != null) {
        Box(modifier = Modifier.glass(backdrop, shape)) {
            CompositionLocalProvider(LocalContentColor provides contentColor, content = content)
        }
    } else {
        Surface(
            shape = shape,
            color = color,
            contentColor = contentColor,
            tonalElevation = tonalElevation,
            shadowElevation = shadowElevation,
            content = content,
        )
    }
}

/** The icon colour for the reading bar: readable on glass, or the normal pill colour. */
@Composable
fun barContentColor(): Color =
    if (glassActive() && GlassHost.backdrop != null) MaterialTheme.colorScheme.onSurface
    else MaterialTheme.colorScheme.onPrimaryContainer
