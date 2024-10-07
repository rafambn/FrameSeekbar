package io.github.rafambn.frameseekbar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class Marker(
    width: Dp = 5.dp,
    height: Dp = 5.dp,
    topOffset: Dp = 0.dp,
    color: Color = Color.Gray,
    bitmap: ImageBitmap? = null
) {
    var width by mutableStateOf(width)
    var height by mutableStateOf(height)
    var topOffset by mutableStateOf(topOffset)
    var color by mutableStateOf(color)
    var bitmap by mutableStateOf(bitmap)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Marker) return false

        if (width != other.width) return false
        if (height != other.height) return false
        if (topOffset != other.topOffset) return false
        if (color != other.color) return false
        if (bitmap != other.bitmap) return false

        return true
    }

    override fun hashCode(): Int {
        var result = width.hashCode()
        result = 31 * result + height.hashCode()
        result = 31 * result + topOffset.hashCode()
        result = 31 * result + color.hashCode()
        result = 31 * result + (bitmap?.hashCode() ?: 0)
        return result
    }
}


