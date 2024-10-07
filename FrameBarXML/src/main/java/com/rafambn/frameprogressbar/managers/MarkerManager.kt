package com.rafambn.frameprogressbar.managers

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import com.rafambn.frameprogressbar.Marker
import com.rafambn.frameprogressbar.api.MarkersAPI
import com.rafambn.frameprogressbar.dpToPixel
import com.rafambn.frameprogressbar.drawRectWithOffset

internal class MarkerManager(private var screenScale: Float) : MarkersAPI {
    private var mMarkersList = mutableListOf<Marker>()
    private var mOffsets = mutableListOf<Float>()
    val markerWidth
        get() = dpToPixel(mMarkersList.sumOf { it.width }, screenScale)
    val markerTotalHeight
        get() = dpToPixel(mMarkersList.maxOf { it.height + it.topOffset }, screenScale)
    val numberOfMarkers
        get() = mMarkersList.size

    internal fun drawMarkers(currentOffset: Float, canvas: Canvas, paint: Paint) {
        mMarkersList.forEachIndexed { index, marker ->
            marker.bitmap?.let { bitmap ->
                paint.alpha = 255
                canvas.drawBitmap(
                    bitmap,
                    currentOffset + mOffsets[index],
                    dpToPixel(marker.topOffset, screenScale).toFloat(),
                    paint
                )
            } ?: run {
                paint.color = marker.color
                canvas.drawRectWithOffset(
                    dpToPixel(marker.width, screenScale).toFloat(),
                    dpToPixel(marker.height, screenScale).toFloat(),
                    dpToPixel(marker.topOffset, screenScale).toFloat(),
                    currentOffset + mOffsets[index],
                    paint
                )
            }
        }
    }

    fun createMarkers(numberOfMarkers: Int, width: Int, height: Int, topOffset: Int, color: Int, drawable: Drawable?) {
        mMarkersList.clear()
        for (i in 0 until numberOfMarkers) {
            mMarkersList.add(
                Marker(
                    width = width,
                    height = height,
                    topOffset = topOffset,
                    color = color,
                    bitmap = drawable?.toBitmap(dpToPixel(width, screenScale), dpToPixel(height, screenScale), null)
                )
            )
        }

        if (color == Color.GRAY && drawable == null)
            mMarkersList.forEachIndexed { index, marker ->
                if (index % 2 == 0)
                    marker.color = Color.GRAY
                else
                    marker.color = Color.TRANSPARENT
            }

        updateMarkers()
    }

    private fun updateMarkers() {
        mOffsets.clear()
        var tempOffset = 0F
        mMarkersList.forEach {
            mOffsets.add(tempOffset)
            tempOffset += dpToPixel(it.width, screenScale)
        }
    }

    fun findIndexTroughOffset(offset: Float): Int {
        val index = mOffsets.indexOfLast { offset >= it }
        return if (index != -1) index else 0
    }

    fun findOffsetTroughIndex(mSelectedIndex: Int): Float {
        var starOffset = 0F
        mMarkersList.forEachIndexed { index, marker ->
            if (mSelectedIndex == index) {
                starOffset += dpToPixel(marker.width, screenScale) / 2
                return starOffset
            } else starOffset += dpToPixel(marker.width, screenScale)
        }
        return starOffset
    }

    override fun setMarkersWidth(width: Int) {
        mMarkersList.forEach {
            it.width = width
        }
        updateMarkers()
    }

    override fun setMarkersWidth(listWidth: List<Pair<Int, Int>>) {
        listWidth.forEach {
            mMarkersList[it.first].width = it.second
        }
        updateMarkers()
    }

    override fun setMarkersHeight(height: Int) {
        mMarkersList.forEach {
            it.height = height
        }
    }

    override fun setMarkersHeight(listHeight: List<Pair<Int, Int>>) {
        listHeight.forEach {
            mMarkersList[it.first].height = it.second
        }
    }

    override fun setMarkersTopOffset(topOffset: Int) {
        mMarkersList.forEach {
            it.topOffset = topOffset
        }
    }

    override fun setMarkersTopOffset(listTopOffset: List<Pair<Int, Int>>) {
        listTopOffset.forEach {
            mMarkersList[it.first].topOffset = it.second
        }
    }

    override fun setMarkersColor(color: Int) {
        mMarkersList.forEach {
            it.color = color
        }
    }

    override fun setMarkersColor(listColor: List<Pair<Int, Int>>) {
        listColor.forEach {
            mMarkersList[it.first].color = it.second
        }
    }

    override fun setMarkersDrawable(drawable: Drawable?) {
        mMarkersList.forEach {
            if (drawable == null)
                it.bitmap = null
            else
                it.bitmap = drawable.toBitmap(dpToPixel(it.width, screenScale), dpToPixel(it.height, screenScale), null)
        }
    }

    override fun setMarkersDrawable(listDrawable: List<Pair<Int, Drawable?>>) {
        listDrawable.forEach {
            if (it.second == null)
                mMarkersList[it.first].bitmap = null
            else
                mMarkersList[it.first].bitmap =
                    it.second!!.toBitmap(
                        dpToPixel(mMarkersList[it.first].width, screenScale),
                        dpToPixel(mMarkersList[it.first].height, screenScale),
                        Bitmap.Config.ARGB_8888
                    )
        }
    }

    override fun setMarkersBitmap(bitmap: Bitmap?) {
        mMarkersList.forEach {
            it.bitmap = bitmap
        }
    }

    override fun setMarkersBitmap(listBitmap: List<Pair<Int, Bitmap?>>) {
        listBitmap.forEach {
            mMarkersList[it.first].bitmap = it.second
        }
    }
}