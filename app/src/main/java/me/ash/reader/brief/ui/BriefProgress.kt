package me.ash.reader.brief.ui

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
