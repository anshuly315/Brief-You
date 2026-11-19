package me.ash.reader.brief.ui.style

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CropSquare
import androidx.compose.material.icons.outlined.PhotoSizeSelectLarge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import me.ash.reader.R
import me.ash.reader.brief.ui.BriefSlider
import me.ash.reader.brief.ui.Corner
import me.ash.reader.brief.ui.SettingGroup
import me.ash.reader.ui.component.base.Subtitle
import me.ash.reader.ui.page.settings.SettingItem

/**
 * Brief You: the list-look sections of the Flow page: view mode, density, and two sliders (the
 * picture size and the corner roundness) with a reset. The article list behind it changes as a
 * slider moves.
 */
@Composable
fun ListLookSections() {
    val context = LocalContext.current
    BriefLook.init(context)

    Subtitle(modifier = Modifier.padding(horizontal = 24.dp), text = stringResource(R.string.list_look_view))
    SettingGroup {
        ListMode.entries.forEach { option ->
            SettingItem(
                title = stringResource(option.title),
                desc = stringResource(option.desc),
                onClick = { BriefLook.setMode(context, option) },
            ) {
                if (BriefLook.mode == option) {
                    Icon(Icons.Outlined.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }

    Subtitle(modifier = Modifier.padding(horizontal = 24.dp), text = stringResource(R.string.list_look_density))
    SettingGroup {
        ListDensity.entries.forEach { option ->
            SettingItem(title = stringResource(option.title), onClick = { BriefLook.setDensity(context, option) }) {
                if (BriefLook.density == option) {
                    Icon(Icons.Outlined.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }

    Subtitle(modifier = Modifier.padding(horizontal = 24.dp), text = stringResource(R.string.list_look_pictures))
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        LookSlider(
            icon = Icons.Outlined.PhotoSizeSelectLarge,
            label = stringResource(R.string.list_look_image_size),
            value = BriefLook.imageSize,
            range = 48f..140f,
            steps = 22,
            onChange = { BriefLook.setImageSize(context, it) },
        )
        Spacer(Modifier.size(2.dp))
        LookSlider(
            icon = Icons.Outlined.CropSquare,
            label = stringResource(R.string.list_look_corners),
            value = BriefLook.corner,
            range = 0f..32f,
            steps = 15,
            onChange = { BriefLook.setCorner(context, it) },
        )
        TextButton(
            onClick = {
                BriefLook.setImageSize(context, BriefLook.IMAGE_DEFAULT)
                BriefLook.setCorner(context, BriefLook.CORNER_DEFAULT)
            }
        ) {
            Text(stringResource(R.string.list_look_reset))
        }
    }
}

/** One slider row in the joined-card design: icon, name, the value in a pill, then the slider. */
@Composable
private fun LookSlider(
    icon: ImageVector,
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    steps: Int,
    onChange: (Float) -> Unit,
) {
    var current by remember(value) { mutableStateOf(value) }
    Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(Corner.Card), color = MaterialTheme.colorScheme.surfaceContainerHigh, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(34.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer) {
                    Text(
                        "${current.roundToInt()} dp",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                    )
                }
            }
            BriefSlider(
                value = current.coerceIn(range.start, range.endInclusive),
                onValueChange = {
                    current = it
                    onChange(it)
                },
                valueRange = range,
                steps = steps,
            )
        }
    }
}
