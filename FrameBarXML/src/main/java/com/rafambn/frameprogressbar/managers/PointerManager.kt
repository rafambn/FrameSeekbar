package com.rafambn.frameprogressbar.managers

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import com.rafambn.frameprogressbar.Marker
import com.rafambn.frameprogressbar.api.PointerAPI
import com.rafambn.frameprogressbar.dpToPixel
import com.rafambn.frameprogressbar.drawRectWithOffset

class PointerManager(private var screenScale: Float) : PointerAPI {
    private lateinit var mPointer: Marker
    val pointerWidth
        get() = dpToPixel(mPointer.width, screenScale)

    val pointerTotalHeight
        get() = dpToPixel(mPointer.height + mPointer.topOffset, screenScale)

    fun createPointer(width: Int, height: Int, topOffset: Int, color: Int, drawable: Drawable?) {
        mPointer = Marker(
            width = width,
            height = height,
            topOffset = topOffset,
            color = color,
            bitmap = drawable?.toBitmap(dpToPixel(width, screenScale), dpToPixel(height, screenScale), null)
        )
    }

    fun drawPointer(mViewCenter: Float, canvas: Canvas, paint: Paint) {
        mPointer.bitmap?.let { bitmap ->
            paint.alpha = 255
            canvas.drawBitmap(
                bitmap,
                mViewCenter - dpToPixel(mPointer.width, screenScale) / 2,
                dpToPixel(mPointer.topOffset, screenScale).toFloat(),
                paint
            )
        } ?: run {
            paint.color = mPointer.color
            canvas.drawRectWithOffset(
                dpToPixel(mPointer.width, screenScale).toFloat(),
                dpToPixel(mPointer.height, screenScale).toFloat(),
                dpToPixel(mPointer.topOffset, screenScale).toFloat(),
                mViewCenter - dpToPixel(mPointer.width, screenScale) / 2,
                paint
            )
        }
    }

    override fun setPointerWidth(width: Int) {
        mPointer.width = width
    }

    override fun setPointerHeight(height: Int) {
        mPointer.height = height
    }

    override fun setPointerTopOffset(topOffset: Int) {
        mPointer.topOffset = topOffset
    }

    override fun setPointerColor(color: Int) {
        mPointer.color = color
    }

    override fun setPointerDrawable(drawable: Drawable?) {
        if (drawable == null)
            mPointer.bitmap = drawable
        else
            mPointer.bitmap = drawable.toBitmap(dpToPixel(mPointer.width, screenScale), dpToPixel(mPointer.height, screenScale), null)
    }

    override fun setPointerBitmap(bitmap: Bitmap?) {
        mPointer.bitmap = bitmap
    }
}