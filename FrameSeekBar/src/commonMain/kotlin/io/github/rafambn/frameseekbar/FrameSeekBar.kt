package io.github.rafambn.frameseekbar

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import io.github.rafambn.frameseekbar.enums.CoercePointer
import io.github.rafambn.frameseekbar.enums.ComponentType
import io.github.rafambn.frameseekbar.enums.Movement
import io.github.rafambn.frameseekbar.enums.PointerSelection
import kotlin.math.floor
import kotlin.math.max

@Composable
fun FrameSeekBar(
    modifier: Modifier = Modifier,
    pointerSelection: PointerSelection = PointerSelection.CENTER,
    coercedPointer: CoercePointer = CoercePointer.NOT_COERCED,
    pointer: Marker,
    markers: List<Marker>,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>? = null,
    onValueChange: (Float) -> Unit,
    onValueChangeStarted: (() -> Unit)? = null,
    onValueChangeFinished: (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null
) {
    FrameSeekBarBase(
        modifier = modifier,
        movement = Movement.CONTINUOUS,
        pointerSelection = pointerSelection,
        coercedPointer = coercedPointer,
        pointer = pointer,
        markers = markers,
        value = value,
        onValueChange = onValueChange,
        onValueChangeStarted = onValueChangeStarted,
        onValueChangeFinished = onValueChangeFinished,
        valueRange = valueRange,
        enabled = enabled,
        interactionSource = interactionSource,
    )
}

@Composable
fun FrameSeekBar(
    modifier: Modifier = Modifier,
    pointerSelection: PointerSelection = PointerSelection.CENTER,
    pointer: Marker,
    markers: List<Marker>,
    index: Int,
    onIndexChange: (Int) -> Unit,
    onIndexChangeStarted: (() -> Unit)? = null,
    onIndexChangeFinished: (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null
) {
    val onIndexChangeState = rememberUpdatedState<(Float) -> Unit> {
        if (it != index.toFloat()) {
            onIndexChange(it.toInt())
        }
    }

    FrameSeekBarBase(
        modifier = modifier,
        movement = Movement.DISCRETE,
        pointerSelection = pointerSelection,
        coercedPointer = CoercePointer.NOT_COERCED,
        pointer = pointer,
        markers = markers,
        value = index.toFloat(),
        onValueChange = onIndexChangeState.value,
        onValueChangeStarted = onIndexChangeStarted,
        onValueChangeFinished = onIndexChangeFinished,
        enabled = enabled,
        interactionSource = interactionSource,
    )
}

@Composable
private fun FrameSeekBarBase(
    modifier: Modifier = Modifier,
    movement: Movement = Movement.CONTINUOUS,
    pointerSelection: PointerSelection = PointerSelection.CENTER,
    coercedPointer: CoercePointer = CoercePointer.NOT_COERCED,
    pointer: Marker,
    markers: List<Marker>,
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeStarted: (() -> Unit)? = null,
    onValueChangeFinished: (() -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Float>? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null
) {
    val density = LocalDensity.current
    val mOffsets = remember(markers.toList()) {
        mutableListOf<Float>().apply {
            clear()
            var tempOffset = 0F
            markers.forEach {
                add(tempOffset)
                tempOffset += with(density) { it.width.toPx() }
            }
        }
    }

    val pointerWidthPx = remember(pointer.hashCode()) { with(density) { pointer.width.toPx() } }
    val trackWidthPx = remember(markers.toList()) {
        with(density) { markers.sumOf { it.width.value.toInt() }.dp.toPx() } -
                if (coercedPointer == CoercePointer.COERCED)
                    pointerWidthPx
                else
                    0F
    }

    val rawOffset = remember {
        mutableFloatStateOf(with(density) {
            if (movement == Movement.CONTINUOUS)
                if (valueRange == null)
                    value.coerceIn(0F, trackWidthPx)
                else
                    convertRange(value.coerceIn(valueRange.start, valueRange.endInclusive), valueRange, 0F..trackWidthPx)
            else
                findOffsetTroughIndex(value, markers).dp.toPx()
        })
    }
    val onValueChangeState = rememberUpdatedState<(Float) -> Unit> {
        if (it != value) {
            if (valueRange == null)
                onValueChange(it)
            else
                onValueChange(convertRange(it, 0F..trackWidthPx, valueRange))
        }
    }

    val draggableState = remember(markers.toList()) {
        DraggableState { delta ->
            val coercedValue = (rawOffset.floatValue - delta).coerceIn(0f, trackWidthPx)
            val newValue = if (movement == Movement.CONTINUOUS)
                coercedValue
            else
                findIndexTroughOffset(coercedValue, mOffsets)

            onValueChangeState.value.invoke(newValue)
            rawOffset.floatValue = coercedValue
        }
    }

    Layout(
        {
            Box(modifier = Modifier.layoutId(ComponentType.POINTER)) { Pointer(pointer = pointer) }
            Box(modifier = Modifier.layoutId(ComponentType.TRACK)) { Markers(markersList = markers) }
        },
        modifier = modifier
            .wrapContentSize()
            .requiredSizeIn(
                minWidth = markers.sumOf { it.width.value.toInt() }.dp,
                minHeight = maxOf(
                    markers.maxOf { it.height + it.topOffset },
                    pointer.height + pointer.topOffset
                )
            )
            .clipToBounds()
            .let { modifier1 ->
                if (enabled) modifier1.draggable(
                    interactionSource = interactionSource,
                    orientation = Orientation.Horizontal,
                    state = draggableState,
                    onDragStarted = {
                        onValueChangeStarted?.invoke()
                    },
                    onDragStopped = {
                        onValueChangeFinished?.invoke()
                    })
                else modifier1
            }
            .focusable(enabled)
    ) { measures, constraints ->

        val pointerPlaceable = measures.first {
            it.layoutId == ComponentType.POINTER
        }.measure(constraints)

        val markersPlaceable = measures.first {
            it.layoutId == ComponentType.TRACK
        }.measure(constraints)

        //Some variable to improve undestanding
        val progressBarWidth = markersPlaceable.width
        val progressBarHeight = max(markersPlaceable.height, pointerPlaceable.height)
        val halfPointerWidth = floor(pointerWidthPx / 2).toInt()
        val halfProgressBarWidth = progressBarWidth / 2

        //Variable to define the placement of the pointer with its center align with the center of the layout
        val pointerOffsetX = halfProgressBarWidth - halfPointerWidth
        val pointerOffsetY = 0

        //This variable determines the placement of the markers. It aligns the left edge of the markers with the left edge of the pointer. Depending on the selection type
        // of the pointer and if the pointer is coerced, it then shifts the markers to the right.
        val markersOffsetX = pointerOffsetX + if (coercedPointer == CoercePointer.NOT_COERCED)
            pointerSelectionShift(pointerSelection, halfPointerWidth, pointerWidthPx.toInt())
        else
            0
        val markersOffsetY = 0

        layout(
            progressBarWidth,
            progressBarHeight
        ) {
            //It ensures that the movement is limited to the maximum width of the markers. If the pointer is in a coerced state, the width of the pointer is subtracted
            // from the total movement.
            val coercedValue = if (movement == Movement.CONTINUOUS) {
                if (valueRange == null)
                    value.coerceIn(0F, trackWidthPx).toInt()
                else
                    convertRange(value.coerceIn(valueRange.start, valueRange.endInclusive), valueRange, 0F..trackWidthPx).toInt()
            } else
                findOffsetTroughIndex(value, markers).dp.toPx().toInt()

            markersPlaceable.placeRelative(
                markersOffsetX - coercedValue,
                markersOffsetY
            )
            pointerPlaceable.placeRelative(
                pointerOffsetX,
                pointerOffsetY
            )
        }
    }
}

fun findIndexTroughOffset(offset: Float, listOffset: List<Float>): Float {
    val index = listOffset.indexOfLast { offset >= it }
    return if (index != -1) index.toFloat() else 0F
}

fun findOffsetTroughIndex(selectedIndex: Float, markers: List<Marker>): Float {
    var starOffset = 0F
    markers.forEachIndexed { index, marker ->
        if (selectedIndex == index.toFloat()) {
            starOffset += marker.width.value / 2
            return starOffset
        } else starOffset += marker.width.value
    }
    return starOffset
}

fun pointerSelectionShift(pointerSelection: PointerSelection, halfPointerWidth: Int, pointerWidth: Int): Int {
    return when (pointerSelection) {
        PointerSelection.LEFT -> 0
        PointerSelection.CENTER -> halfPointerWidth
        PointerSelection.RIGHT -> pointerWidth
    }
}

fun convertRange(
    value: Float,
    originalRange: ClosedFloatingPointRange<Float>,
    targetRange: ClosedFloatingPointRange<Float>
): Float {
    return (value - originalRange.start) / (originalRange.endInclusive - originalRange.start) * (targetRange.endInclusive - targetRange.start) + targetRange.start
}