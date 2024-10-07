package com.rafambn.frameprogressbar.api

import android.graphics.Bitmap
import android.graphics.drawable.Drawable

interface PointerAPI {

    fun setPointerWidth(width: Int)

    fun setPointerHeight(height: Int)

    fun setPointerTopOffset(topOffset: Int)

    fun setPointerColor(color: Int)

    fun setPointerDrawable(drawable: Drawable?)

    fun setPointerBitmap(bitmap: Bitmap?)
}