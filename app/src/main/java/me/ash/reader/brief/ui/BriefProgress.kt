package me.ash.reader.brief.ui

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.unit.dp
import me.ash.reader.brief.ui.style.BriefStyle
import me.ash.reader.brief.ui.style.DesignStyle

/** A linear progress bar: wavy in Material Expressive, straight in Classic Material 3. */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BriefLinearProgress(progress: () -> Float, modifier: Modifier = Modifier) {
    if (BriefStyle.design == DesignStyle.Classic) LinearProgressIndicator(progress = progress, modifier = modifier)
    else LinearWavyProgressIndicator(progress = progress, modifier = modifier)
}

/** The same without a value (it keeps moving). */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BriefLinearProgress(modifier: Modifier = Modifier) {
    if (BriefStyle.design == DesignStyle.Classic) LinearProgressIndicator(modifier = modifier)
    else LinearWavyProgressIndicator(modifier = modifier)
}

/** A loading spinner: Expressive's turning shape, or a plain turning circle in Classic Material 3. */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BriefLoading(modifier: Modifier = Modifier, color: Color = Color.Unspecified) {
    if (BriefStyle.design == DesignStyle.Classic) {
        CircularProgressIndicator(
            modifier = modifier,
            color = if (color.isSpecified) color else MaterialTheme.colorScheme.primary,
            strokeWidth = 3.dp,
        )
    } else if (color.isSpecified) LoadingIndicator(modifier = modifier, color = color)
    else LoadingIndicator(modifier = modifier)
}
