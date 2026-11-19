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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/*
 * Brief You: a small picture of the app in the colours you have chosen. It is drawn from the live
 * theme, so it changes the moment you pick a colour, a scheme style, a contrast level or dark mode.
 */
@Composable
fun StylePreview(modifier: Modifier = Modifier) {
    val c = MaterialTheme.colorScheme
    Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
        // The floating bar and its main button.
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier =
                    Modifier.weight(1f)
                        .height(28.dp)
                        .clip(CircleShape)
                        .background(c.secondaryContainer)
                        .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Dot(10.dp, c.onSecondaryContainer)
                Dot(10.dp, c.onSecondaryContainer)
                Dot(10.dp, c.onSecondaryContainer)
            }
            Box(Modifier.size(28.dp).clip(CircleShape).background(c.primary))
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
