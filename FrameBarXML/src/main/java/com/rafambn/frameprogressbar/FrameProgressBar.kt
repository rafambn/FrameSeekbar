package com.rafambn.frameprogressbar

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.Dimension
import androidx.core.content.ContextCompat
import com.rafambn.frameprogressbar.api.FrameProgressBarAPI
import com.rafambn.frameprogressbar.api.MarkersAPI
import com.rafambn.frameprogressbar.api.OnFrameProgressBarChangeListener
import com.rafambn.frameprogressbar.api.PointerAPI
import com.rafambn.frameprogressbar.enums.CoercePointer
import com.rafambn.frameprogressbar.enums.Movement
import com.rafambn.frameprogressbar.enums.PointerSelection
import com.rafambn.frameprogressbar.managers.MarkerManager
import com.rafambn.frameprogressbar.managers.PointerManager

/**
 * A custom progress bar that displays a customizable set of frames.
 *
 * This class has three key features:
 *
 * * Movement: This feature determines how the progress bar moves when the user interacts with it. The three possible values are
 * CONTINUOUS, DISCRETE.
 *
 * * Pointer Selection: This feature determines witch part of the pointer indicates the current selection. The three possible values are
 * LEFT, CENTER, and RIGHT..
 *
 * * Coerce Pointer: This feature determines whether or not the pointer can be dragged beyond the start or end of the progress
 * bar. The two possible values are COERCED and NOT_COERCED.
 */
class FrameProgressBar(context: Context, attrs: AttributeSet) : View(context, attrs),
    FrameProgressBarAPI, MarkersAPI, PointerAPI {
    private val mScreenScale = context.resources.displayMetrics.density
    private val mMarkerManager = MarkerManager(mScreenScale)
    private val mPointerManager = PointerManager(mScreenScale)
    private val mPaint = Paint()

    @Dimension(unit = Dimension.PX)
    private var mCurrentOffset = 0F

    @Dimension(unit = Dimension.PX)
    private var mStartOffset = 0F

    @Dimension(unit = Dimension.PX)
    private var mCoercedtOffset = 0

    @Dimension(unit = Dimension.PX)
    private var mViewCenter = 0F
    private var mSelectedIndex: Int

    private var mMovement: Movement
    private var mPointerSelection: PointerSelection
    private var mCoercedPointer: CoercePointer

    private var mInitialTouchX = 0F
    private var mStartTouchOffset = 0F
    private var mMovableDistance = 0F

    private var mOnFrameProgressBarChangeListener: OnFrameProgressBarChangeListener? = null

    init {
        val myAttrs = context.obtainStyledAttributes(attrs, R.styleable.FrameProgressBar)
        mMarkerManager.createMarkers(
            myAttrs.getInt(R.styleable.FrameProgressBar_size, 10),
            myAttrs.getInt(R.styleable.FrameProgressBar_markersWidth, 5),
            myAttrs.getInt(R.styleable.FrameProgressBar_markersHeight, 20),
            myAttrs.getInt(R.styleable.FrameProgressBar_markersTopOffset, 0),
            myAttrs.getInt(R.styleable.FrameProgressBar_markersColor, Color.GRAY),
            try {
                ContextCompat.getDrawable(
                    context,
                    myAttrs.getResourceId(R.styleable.FrameProgressBar_markersDrawable, -1)
                )
            } catch (ex: Resources.NotFoundException) {
                null
            }
        )
        mPointerManager.createPointer(
            myAttrs.getInt(R.styleable.FrameProgressBar_pointerWidth, 5),
            myAttrs.getInt(R.styleable.FrameProgressBar_pointerHeight, 40),
            myAttrs.getInt(R.styleable.FrameProgressBar_pointerTopOffset, 0),
            myAttrs.getInt(R.styleable.FrameProgressBar_pointerColor, Color.YELLOW),
            try {
                ContextCompat.getDrawable(
                    context,
                    myAttrs.getResourceId(R.styleable.FrameProgressBar_markersDrawable, -1)
                )
            } catch (ex: Resources.NotFoundException) {
                null
            }
        )
        mSelectedIndex = myAttrs.getInt(R.styleable.FrameProgressBar_startIndex, 0)
        mMovement = when (myAttrs.getInt(R.styleable.FrameProgressBar_movement, 1)) {
            0 -> Movement.DISCRETE
            1 -> Movement.CONTINUOUS
            else -> {
                Movement.CONTINUOUS
            }
        }
        mPointerSelection =
            when (myAttrs.getInt(R.styleable.FrameProgressBar_pointerSelection, 1)) {
                0 -> PointerSelection.LEFT
                1 -> PointerSelection.CENTER
                2 -> PointerSelection.RIGHT
                else -> {
                    PointerSelection.CENTER
                }
            }
        mCoercedPointer = when (myAttrs.getInt(R.styleable.FrameProgressBar_coercePointer, 1)) {
            0 -> CoercePointer.COERCED
            1 -> CoercePointer.NOT_COERCED
            else -> {
                CoercePointer.NOT_COERCED
            }
        }
        myAttrs.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mMarkerManager.drawMarkers(mCurrentOffset, canvas, mPaint)
        mPointerManager.drawPointer(mViewCenter, canvas, mPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidthInPixels = mMarkerManager.markerWidth.toFloat()
        val desiredHeightInPixels = maxOf(
            mMarkerManager.markerTotalHeight.toFloat(),
            mPointerManager.pointerTotalHeight.toFloat()
        )

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width: Int = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> desiredWidthInPixels.coerceAtMost(widthSize.toFloat()).toInt()
            else -> desiredWidthInPixels.toInt()
        }

        mViewCenter = (width / 2).toFloat()

        mStartOffset = when (mPointerSelection) {
            PointerSelection.LEFT -> mViewCenter - mPointerManager.pointerWidth / 2
            PointerSelection.CENTER -> mViewCenter
            PointerSelection.RIGHT -> mViewCenter + mPointerManager.pointerWidth / 2
        }

        mCurrentOffset = mStartOffset - mMarkerManager.findOffsetTroughIndex(mSelectedIndex)

        val height: Int = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> desiredHeightInPixels.coerceAtMost(widthSize.toFloat()).toInt()
            else -> desiredHeightInPixels.toInt()
        }

        setMeasuredDimension(width, height)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mInitialTouchX = event.x
                mStartTouchOffset = mCurrentOffset
                mCoercedtOffset =
                    if (mCoercedPointer == CoercePointer.COERCED) //TODO improve coerce
                        when (mPointerSelection) {
                            PointerSelection.LEFT -> 0
                            PointerSelection.CENTER -> mPointerManager.pointerWidth / 2
                            PointerSelection.RIGHT -> mPointerManager.pointerWidth
                        }
                    else
                        0
                mMovableDistance = if (mCoercedPointer == CoercePointer.NOT_COERCED)
                    mMarkerManager.markerWidth.toFloat()
                else
                    mMarkerManager.markerWidth - mPointerManager.pointerWidth.toFloat()
                mOnFrameProgressBarChangeListener?.onStartTrackingTouch(this)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                var distanceMoved = mStartTouchOffset + event.x - mInitialTouchX
                distanceMoved = distanceMoved.coerceIn(
                    mStartOffset - mMovableDistance - mCoercedtOffset,
                    mStartOffset - mCoercedtOffset
                )

                mSelectedIndex = mMarkerManager.findIndexTroughOffset(
                    mViewCenter - distanceMoved - when (mPointerSelection) {
                        PointerSelection.LEFT -> mPointerManager.pointerWidth / 2
                        PointerSelection.CENTER -> 0
                        PointerSelection.RIGHT -> -mPointerManager.pointerWidth / 2
                    }
                )
                mOnFrameProgressBarChangeListener?.onIndexChanged(this, mSelectedIndex, true)
                mCurrentOffset = if (mMovement == Movement.DISCRETE)
                    mStartOffset - mMarkerManager.findOffsetTroughIndex(mSelectedIndex)
                else
                    distanceMoved

                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                mOnFrameProgressBarChangeListener?.onStopTrackingTouch(this)
            }
        }
        return super.onTouchEvent(event)
    }

    /**
     * Sets a listener to receive notifications of changes to the FrameProgressBar's index. Also
     * provides notifications of when the user starts and stops a touch gesture within the FrameProgressBar.
     *
     * @param listener The FrameProgressBar notification listener
     *
     * @see OnFrameProgressBarChangeListener
     */
    fun setOnFrameProgressBarChangeListener(listener: OnFrameProgressBarChangeListener) {
        mOnFrameProgressBarChangeListener = listener
    }

    /**
     * Sets the number of frames to be displayed.
     *
     * Warning: This method will reset all current markers.
     *
     * @param numberFrames The number of frames to be displayed.
     */
    override fun setNumberFrames(numberFrames: Int) {
        mMarkerManager.createMarkers(
            numberFrames,
            5,
            20,
            0,
            Color.GRAY,
            null
        )
        invalidate()
        requestLayout()
    }

    /**
     * Gets the current number of frames.
     *
     * @return The number of frames.
     */
    override fun getNumberFrames(): Int {
        return mMarkerManager.numberOfMarkers
    }

    /**
     * Sets the selected index.
     *
     * Index is bound between 0 and the number of Markers.
     *
     * @param index The selected index.
     */
    override fun setIndex(index: Int) {
        mSelectedIndex = index.coerceIn(0, mMarkerManager.numberOfMarkers - 1)
        mCurrentOffset = mStartOffset - mMarkerManager.findOffsetTroughIndex(mSelectedIndex)
        mOnFrameProgressBarChangeListener?.onIndexChanged(this, mSelectedIndex, false)
        invalidate()
    }

    /**
     * Returns the index of the currently selected item.
     *
     * @return The index of the currently selected item.
     */
    override fun getIndex(): Int {
        return mSelectedIndex
    }

    /**
     * Sets the movement of the view.
     *
     * @param movement The movement to set.
     *
     * @see Movement
     */
    override fun setMovement(movement: Movement) {
        mMovement = movement
        invalidate()
    }

    /**
     * Returns the movement of the FrameProgressBar.
     *
     * @return The movement type.
     */
    override fun getMovement(): Movement {
        return mMovement
    }

    /**
     * Sets the pointer selection.
     *
     * @param pointerSelection The pointer selection to set.
     *
     * @see PointerSelection
     */
    override fun setPointerSelection(pointerSelection: PointerSelection) {
        mPointerSelection = pointerSelection
        invalidate()
        requestLayout()
    }


    /**
     * Returns the pointer selection.
     *
     * @return The pointer selection.
     */
    override fun getPointerSelection(): PointerSelection {
        return mPointerSelection
    }

    /**
     * Sets the CoercePointer to use when drawing the view.
     *
     * @param coercePointer The CoercePointer to use.
     *
     * @see CoercePointer
     */
    override fun setCoercePointer(coercePointer: CoercePointer) {
        mCoercedPointer = coercePointer
        invalidate()
    }

    /**
     * Gets the coerced pointer.
     *
     * @return The coerced pointer.
     */
    override fun getCoercePointer(): CoercePointer {
        return mCoercedPointer
    }

    /**
     * Sets the offset of the view.
     *
     * Offset is bound between 0 and the total size of the Markers.
     *
     * @param offset The offset in pixels.
     * @param ignoreMovementType Weather you and to the offset to change acording to the MovementType.
     */
    override fun setOffset(offset: Float, ignoreMovementType: Boolean) {
        mSelectedIndex = mMarkerManager.findIndexTroughOffset(offset)
        mCurrentOffset = if (mMovement == Movement.DISCRETE && !ignoreMovementType)
            mStartOffset - mMarkerManager.findOffsetTroughIndex(mSelectedIndex)
        else
            mStartOffset - offset.coerceIn(0F, mMarkerManager.markerWidth.toFloat())
        mOnFrameProgressBarChangeListener?.onIndexChanged(this, mSelectedIndex, false)
        invalidate()
    }

    /**
     * Gets the offset of the view.
     *
     * @return The offset of the view.
     */
    override fun getOffset(): Float {
        return -(mCurrentOffset - mStartOffset)
    }

    /**
     * Sets the width of all markers.
     *
     * @param width The width in pixels.
     */
    override fun setMarkersWidth(width: Int) {
        mMarkerManager.setMarkersWidth(width)
        invalidate()
        requestLayout()
    }

    /**
     * Sets the width of specific markers.
     *
     * @param listWidth A list of pairs where the first element is the index of the marker and the second element is the width in pixels.
     */
    override fun setMarkersWidth(listWidth: List<Pair<Int, Int>>) {
        mMarkerManager.setMarkersWidth(listWidth)
        invalidate()
        requestLayout()
    }

    /**
     * Sets the height of all markers.
     *
     * @param height The height in pixels.
     */
    override fun setMarkersHeight(height: Int) {
        mMarkerManager.setMarkersHeight(height)
        invalidate()
        requestLayout()
    }


    /**
     * Sets the height of specific markers.
     *
     * @param listHeight A list of pairs where the first element is the index of the marker and the second element is the height in pixels.
     */
    override fun setMarkersHeight(listHeight: List<Pair<Int, Int>>) {
        mMarkerManager.setMarkersHeight(listHeight)
        invalidate()
        requestLayout()
    }

    /**
     * Sets the top offset of all markers.
     *
     * @param topOffset The top offset in pixels.
     */
    override fun setMarkersTopOffset(topOffset: Int) {
        mMarkerManager.setMarkersTopOffset(topOffset)
        invalidate()
        requestLayout()
    }

    /**
     * Sets the top offset of specific markers.
     *
     * @param listTopOffset A list of pairs where the first element is the index of the marker and the second element is the top offset in pixels.
     */
    override fun setMarkersTopOffset(listTopOffset: List<Pair<Int, Int>>) {
        mMarkerManager.setMarkersTopOffset(listTopOffset)
        invalidate()
        requestLayout()
    }

    /**
     * Sets the color of all markers.
     *
     * @param color The color in ARGB format.
     */
    override fun setMarkersColor(color: Int) {
        mMarkerManager.setMarkersColor(color)
        invalidate()
    }

    /**
     * Sets the color of specific markers.
     *
     * @param listColor A list of pairs where the first element is the index of the marker and the second element is the color in ARGB format.
     */
    override fun setMarkersColor(listColor: List<Pair<Int, Int>>) {
        mMarkerManager.setMarkersColor(listColor)
        invalidate()
    }

    /**
     * Sets the drawable of all markers.
     *
     * @param drawable The drawable to use. If set to null the color of marker will be used.
     */
    override fun setMarkersDrawable(drawable: Drawable?) {
        mMarkerManager.setMarkersDrawable(drawable)
        invalidate()
    }

    /**
     * Sets the drawable of specific markers.
     *
     * @param listDrawable A list of pairs where the first element is the index of the marker and the second element is the drawable to use.
     * If set to null the color of marker will be used.
     */
    override fun setMarkersDrawable(listDrawable: List<Pair<Int, Drawable?>>) {
        mMarkerManager.setMarkersDrawable(listDrawable)
        invalidate()
    }

    /**
     * Sets the bitmap of all markers.
     *
     * @param bitmap The bitmap to use. If set to null the color of marker will be used.
     */
    override fun setMarkersBitmap(bitmap: Bitmap?) {
        mMarkerManager.setMarkersBitmap(bitmap)
        invalidate()
    }

    /**
     * Sets the bitmap of specific markers.
     *
     * @param listBitmap A list of pairs where the first element is the index of the marker and the second element is the bitmap to use. If
     * set to null the color of marker will be used.
     */
    override fun setMarkersBitmap(listBitmap: List<Pair<Int, Bitmap?>>) {
        mMarkerManager.setMarkersBitmap(listBitmap)
        invalidate()
    }

    /**
     * Sets the width of the pointer.
     *
     * @param width The width in pixels.
     */
    override fun setPointerWidth(width: Int) {
        mPointerManager.setPointerWidth(width)
        invalidate()
        requestLayout()
    }

    /**
     * Sets the height of the pointer.
     *
     * @param height The height in pixels.
     */
    override fun setPointerHeight(height: Int) {
        mPointerManager.setPointerHeight(height)
        invalidate()
        requestLayout()
    }

    /**
     * Sets the top offset of the pointer.
     *
     * @param topOffset The top offset in pixels.
     */
    override fun setPointerTopOffset(topOffset: Int) {
        mPointerManager.setPointerTopOffset(topOffset)
        invalidate()
        requestLayout()
    }

    /**
     * Sets the color of the pointer.
     *
     * @param color The color in ARGB format.
     */
    override fun setPointerColor(color: Int) {
        mPointerManager.setPointerColor(color)
        invalidate()
    }

    /**
     * Sets the drawable of the pointer.
     *
     * @param drawable The drawable to use. If set to null the color of marker will be used.
     */
    override fun setPointerDrawable(drawable: Drawable?) {
        mPointerManager.setPointerDrawable(drawable)
        invalidate()
    }

    /**
     * Sets the bitmap of the pointer.
     *
     * @param bitmap The bitmap to use. If set to null the color of marker will be used.
     */
    override fun setPointerBitmap(bitmap: Bitmap?) {
        mPointerManager.setPointerBitmap(bitmap)
        invalidate()
    }

    /**
     * Find the Index of the FrameProgressBar given the Offset.
     *
     * @param offset The offset to use. Offset is bound between 0 and the total size of the Markers.
     */
    override fun findIndexTroughOffset(offset: Float): Int {
        return mMarkerManager.findIndexTroughOffset((offset.coerceIn(0F, mMarkerManager.markerWidth.toFloat())))
    }
}