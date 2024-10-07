package com.rafambn.frameprogressbar.api

import android.graphics.Bitmap
import android.graphics.drawable.Drawable

interface MarkersAPI {

    fun setMarkersWidth(width: Int)
    fun setMarkersWidth(listWidth: List<Pair<Int, Int>>)

    fun setMarkersHeight(height: Int)
    fun setMarkersHeight(listHeight: List<Pair<Int, Int>>)

    fun setMarkersTopOffset(topOffset: Int)
    fun setMarkersTopOffset(listTopOffset: List<Pair<Int, Int>>)

    fun setMarkersColor(color: Int)
    fun setMarkersColor(listColor: List<Pair<Int, Int>>)

    fun setMarkersDrawable(drawable: Drawable?)
    fun setMarkersBitmap(bitmap: Bitmap?)
    fun setMarkersDrawable(listDrawable: List<Pair<Int, Drawable?>>)
    fun setMarkersBitmap(listBitmap: List<Pair<Int, Bitmap?>>)
}
