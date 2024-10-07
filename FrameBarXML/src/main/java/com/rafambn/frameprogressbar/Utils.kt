package com.rafambn.frameprogressbar

import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.Dimension
import androidx.annotation.Dimension.Companion.PX


fun Canvas.drawRectWithOffset(
    @Dimension(unit = PX)
    width: Float,
    @Dimension(unit = PX)
    heigth: Float,
    @Dimension(unit = PX)
    topOffset: Float,
    @Dimension(unit = PX)
    offset: Float,
    paint: Paint
) {
    this.drawRect(offset, topOffset, offset + width, topOffset + heigth, paint)
}

fun dpToPixel(dpValue: Int, screenScale: Float): Int {
    return (dpValue * screenScale + 0.5f).toInt()
}
