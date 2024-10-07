package com.rafambn.frameprogressbar.api

import com.rafambn.frameprogressbar.enums.CoercePointer
import com.rafambn.frameprogressbar.enums.Movement
import com.rafambn.frameprogressbar.enums.PointerSelection

interface FrameProgressBarAPI {

    fun setNumberFrames(numberFrames: Int)
    fun getNumberFrames(): Int

    fun setIndex(index: Int)
    fun getIndex(): Int

    fun setMovement(movement: Movement)
    fun getMovement(): Movement

    fun setPointerSelection(pointerSelection: PointerSelection)
    fun getPointerSelection(): PointerSelection

    fun setCoercePointer(coercePointer: CoercePointer)
    fun getCoercePointer(): CoercePointer

    fun setOffset(offset: Float, ignoreMovementType: Boolean = false)
    fun getOffset(): Float

    fun findIndexTroughOffset(offset: Float): Int
}
