package com.rafambn.framebar.api

import com.rafambn.framebar.enums.CoercePointer
import com.rafambn.framebar.enums.Movement
import com.rafambn.framebar.enums.PointerSelection

interface FrameBarAPI {

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
