package com.rafambn.frameprogressbar.api

import com.rafambn.frameprogressbar.FrameProgressBar

interface OnFrameProgressBarChangeListener {
    /**
     * Notification that the progress level has changed. Clients can use the fromUser parameter
     * to distinguish user-initiated changes from those that occurred programmatically.
     *
     * @param frameProgressBar The FrameProgressBar whose progress has changed
     * @param index The current progress level.
     */
    fun onIndexChanged(frameProgressBar: FrameProgressBar, index: Int, fromTouch: Boolean)

    /**
     * Notification that the user has started a touch gesture.
     * @param frameProgressBar The FrameProgressBar in which the touch gesture began
     */
    fun onStartTrackingTouch(frameProgressBar: FrameProgressBar)

    /**
     * Notification that the user has finished a touch gesture.
     * @param frameProgressBar The FrameProgressBar in which the touch gesture began
     */
    fun onStopTrackingTouch(frameProgressBar: FrameProgressBar)
}