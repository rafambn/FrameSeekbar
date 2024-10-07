package com.rafambn.frameprogressbar

import android.graphics.Bitmap
import android.graphics.Color
import androidx.annotation.Dimension
import androidx.annotation.Dimension.Companion.DP

data class Marker(
    @Dimension(unit = DP)
    var width: Int = 5,
    @Dimension(unit = DP)
    var height: Int = 5,
    @Dimension(unit = DP)
    var topOffset: Int = 0,
    var color: Int = Color.GRAY,
    var bitmap: Bitmap? = null
)
