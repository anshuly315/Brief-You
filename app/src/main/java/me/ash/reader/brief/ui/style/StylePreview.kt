package me.ash.reader.brief.ui.style

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.ash.reader.brief.ui.glass.glass
import com.kyant.backdrop.backdrops.layerBackdrop

/*
 * Brief You: a small picture of the app in the colours you have chosen. It is drawn from the live
 * theme, so it changes the moment you pick a colour, a scheme style, a contrast level or dark mode.
 */
@Composable
fun StylePreview(modifier: Modifier = Modifier) {
    val c = MaterialTheme.colorScheme
    val glassOn = me.ash.reader.brief.ui.glass.glassActive()
    val surface = c.surfaceContainerLow
    val backdrop = com.kyant.backdrop.backdrops.rememberLayerBackdrop {
        drawRect(surface)
        drawContent()
    }
    val classic = BriefStyle.design == DesignStyle.Classic
    val barHeight = (BriefLook.barHeightDp(true) * 0.4f).dp
    val side = (BriefLook.barSideDp() * 0.4f).dp
    val bottom = (BriefLook.barBottom * 0.4f).dp
    val barShape = if (classic) androidx.compose.ui.graphics.RectangleShape else RoundedCornerShape(12.dp)
    Box(modifier = modifier.fillMaxSize()) {
        // The page behind the bar. With glass on it gets colourful stripes, so the glass has something to show.
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .layerBackdrop(backdrop)
                    .then(
                        if (glassOn) Modifier.drawBehind {
                            val stripe = 26.dp.toPx()
                            val colors = listOf(c.primary, c.tertiary, c.secondary)
                            var x = -size.height
                            var i = 0
                            while (x < size.width) {
                                drawLine(colors[i % 3].copy(alpha = 0.55f), Offset(x, size.height), Offset(x + size.height, 0f), strokeWidth = stripe)
                                x += stripe * 1.6f
                                i++
                            }
                        } else Modifier
                    ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // A top bar: back circle, title, accent circle.
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Dot(18.dp, c.onSurface)
                Line(80.dp, c.onSurface)
                Spacer(Modifier.weight(1f))
                Dot(18.dp, c.primary)
            }
            // An article card in the primary container.
            Column(
                modifier =
                    Modifier.fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(c.primaryContainer)
                        .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Line(120.dp, c.onPrimaryContainer)
                Line(170.dp, c.onPrimaryContainer.copy(alpha = 0.6f))
                Line(90.dp, c.onPrimaryContainer.copy(alpha = 0.6f))
            }
            // Chips in the other accent roles.
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Chip(c.secondaryContainer)
                Chip(c.tertiaryContainer)
                Chip(c.surfaceContainerHigh)
            }
            Spacer(Modifier.height(barHeight))
        }
        // The bottom bar, drawn from the real bar settings: thickness, distance from the sides and from the bottom.
        Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(horizontal = side).padding(bottom = bottom).height(barHeight)) {
            val barContent: @Composable () -> Unit = {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Dot(10.dp, c.onSecondaryContainer)
                    Box(Modifier.width(30.dp).height(16.dp).clip(CircleShape).background(c.primary))
                    Dot(10.dp, c.onSecondaryContainer)
                }
            }
            if (glassOn) {
                Box(Modifier.fillMaxSize().then(Modifier.glass(backdrop, barShape))) { barContent() }
            } else {
                Box(Modifier.fillMaxSize().clip(barShape).background(c.secondaryContainer)) { barContent() }
            }
        }
    }
}

@Composable private fun Dot(size: Dp, color: Color) = Box(Modifier.size(size).clip(CircleShape).background(color))

@Composable
private fun Line(width: Dp, color: Color) =
    Box(Modifier.width(width).height(8.dp).clip(RoundedCornerShape(4.dp)).background(color))

@Composable
private fun Chip(color: Color) =
    Box(Modifier.width(48.dp).height(22.dp).clip(RoundedCornerShape(8.dp)).background(color))
