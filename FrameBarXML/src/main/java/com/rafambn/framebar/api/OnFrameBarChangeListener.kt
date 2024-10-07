package com.rafambn.framebar.api

import com.rafambn.framebar.FrameBar

interface OnFrameBarChangeListener {
    /**
     * Notification that the progress level has changed. Clients can use the fromUser parameter
     * to distinguish user-initiated changes from those that occurred programmatically.
     *
     * @param frameBar The FrameProgressBar whose progress has changed
     * @param index The current progress level.
     */
    fun onIndexChanged(frameBar: FrameBar, index: Int, fromTouch: Boolean)

    /**
     * Notification that the user has started a touch gesture.
     * @param frameBar The FrameProgressBar in which the touch gesture began
     */
    fun onStartTrackingTouch(frameBar: FrameBar)

    /**
     * Notification that the user has finished a touch gesture.
     * @param frameBar The FrameProgressBar in which the touch gesture began
     */
    fun onStopTrackingTouch(frameBar: FrameBar)
}