package me.ash.reader.brief.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Brief You's connected lists, one design everywhere: rows rounded big on the group's outside and
 * small between them, a small gap apart. Lists on pages and sheets use the big corners; lists
 * nested inside something smaller (a dialog, a card) use the nested ones.
 */
object Group {
    // Brief You: Classic Material 3 (Color & Style > Design style) uses smaller corners everywhere.
    private val classic get() = me.ash.reader.brief.ui.style.BriefStyle.design == me.ash.reader.brief.ui.style.DesignStyle.Classic
    val Outer get() = if (classic) 12.dp else 24.dp
    val Inner get() = if (classic) 4.dp else 6.dp
    /** The space between two rows (draw each row with half of it above and below). */
    val Gap = 3.dp
    val NestedOuter get() = if (classic) 10.dp else 16.dp
    val NestedInner get() = if (classic) 3.dp else 4.dp
}

/**
 * The app's corner sizes, one scale for everything: inner joins of a group, pictures and small
 * cards, cards and groups, tiles and heroes, banners. Buttons, chips and pills are fully round.
 */
object Corner {
    // Brief You: Classic Material 3 uses smaller corners than Material Expressive (Color & Style > Design style).
    private val classic get() = me.ash.reader.brief.ui.style.BriefStyle.design == me.ash.reader.brief.ui.style.DesignStyle.Classic
    val Inner get() = if (classic) 4.dp else 6.dp
    val Small get() = if (classic) 10.dp else 16.dp
    val Card get() = if (classic) 14.dp else 24.dp
    val Tile get() = if (classic) 16.dp else 28.dp
    val Banner get() = if (classic) 20.dp else 32.dp
}

/** The shape of row [index] of [count] in a connected list. */
fun groupShape(index: Int, count: Int, outer: Dp = Group.Outer, inner: Dp = Group.Inner): RoundedCornerShape {
    val top = if (index == 0) outer else inner
    val bottom = if (index == count - 1) outer else inner
    return RoundedCornerShape(topStart = top, topEnd = top, bottomStart = bottom, bottomEnd = bottom)
}

/** The same for a list nested in something smaller (a dialog, a card). */
fun nestedGroupShape(index: Int, count: Int): RoundedCornerShape =
    groupShape(index, count, Group.NestedOuter, Group.NestedInner)
