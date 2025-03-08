package com.galacticware.griddle.domain.model.prototyping.phonemicboard.geneticalgorithm

import com.galacticware.griddle.domain.model.geometry.CartesianAxis
import com.galacticware.griddle.domain.model.keyboard.Keyboard
import com.galacticware.griddle.domain.model.geometry.RectangleLocation
import com.galacticware.griddle.domain.model.geometry.GridPosition
import com.galacticware.griddle.domain.model.geometry.AxialParams
import com.galacticware.griddle.domain.model.geometry.StartAndSpan
import com.galacticware.griddle.domain.model.gesture.GestureType
import com.galacticware.griddle.domain.model.error.Errors
import java.util.concurrent.ConcurrentHashMap

/**
 * A CrossReferencingMap holds all the data to create a KeyboardLayer. Its data comes from a keyboard generating
 * algorithm.
 */
class CrossReferencingMap(
    map: MutableMap<GridPosition, MutableMap<String, GestureType>> = ConcurrentHashMap(
        (0 until 3).map {
            (0 until 3).map { j ->
                GridPosition(
                    rowParams = AxialParams(CartesianAxis.Y, StartAndSpan(it, 1)),
                    colParams = AxialParams(CartesianAxis.X, StartAndSpan(j, 1)),
                ) to ConcurrentHashMap<String, GestureType>()
            }
        }.flatten().toMap().toMutableMap()
    )
) {
    private val boxPositionToButtonArgs = map.toMutableMap()

    operator fun get(key: GridPosition) = boxPositionToButtonArgs[key]
    val entries get() = boxPositionToButtonArgs.entries
    var iterator = boxPositionToButtonArgs.iterator()
    fun hasNext() = iterator.hasNext()
    fun next(): Map.Entry<RectangleLocation, MutableMap<String, GestureType>> {
        val next = iterator.next()
        return mapOf(next.key.rectangleLocation to next.value).entries.first()
    }
    fun clone() = CrossReferencingMap(boxPositionToButtonArgs.toMutableMap())
    fun genes() = run {
        boxPositionToButtonArgs.entries.flatMap { m ->
                m.value.entries.map { it.key to (m.key to it.value)}
            }
        }
    fun offer(triple: Triple<GridPosition, String, GestureType>,
              untriedButtons: MutableSet<GridPosition> = boxPositionToButtonArgs.keys.toMutableSet()) {
        val (gridPosition, symbol, gestureType) = triple

        val r = boxPositionToButtonArgs.keys.firstOrNull { pos ->
            val x1 = pos.colStart
            val y1 = pos.rowStart
            val x2 = gridPosition.colStart
            val y2 = gridPosition.rowStart
            x1 == x2 && y1 == y2
        } ?: {
            /* Log.e(TAG , "How the fuck is it not in here?[${
                boxPositionToButtonArgs.keys.joinToString(",") { it.toString() }
            }]") */
        }
        val mutableMap = boxPositionToButtonArgs[r]!!
        if(symbol in mutableMap) {
            var found = false
            GestureType.visibleTypes.filter { t ->
                t !in mutableMap.values
            }
                .shuffled()
                .firstOrNull()
                ?.let { t ->
                    mutableMap[symbol] = t
                    found = true
                }
            if(!found) {
                if(untriedButtons.isEmpty()) {
                    throw Errors.BUTTON_WITH_NOT_FOUND.send(
                        "No more boxes to try: ${boxPositionToButtonArgs.values}"
                    )
                }
                var next = untriedButtons.first()
                untriedButtons.remove(next)
                if(next == gridPosition){
                    if(untriedButtons.isEmpty()) {
                        throw IllegalStateException("No more boxes to try")
                    }
                    next = untriedButtons.first()
                    untriedButtons.remove(next)
                }
                offer(Triple(next, symbol, gestureType), untriedButtons)
            }
        } else {
            if(mutableMap.containsKey(symbol)) {
                throw IllegalStateException("Already has a symbol")
            }
            mutableMap[symbol] = gestureType
        }
    }

    fun stringToPosition(symbol: String) = boxPositionToButtonArgs.entries.firstOrNull { it.value.containsKey(symbol) }?.key
    fun remove(a: MutableMap.MutableEntry<GridPosition, MutableMap<String, GestureType>>) {
        boxPositionToButtonArgs.remove(a.key)
    }

    fun offer(triple: GridPosition, untriedButtons: MutableMap<String, GestureType>) {
        boxPositionToButtonArgs[triple] = untriedButtons
    }

    val size get() = boxPositionToButtonArgs.entries.sumOf { it.value.size }

    companion object {
        fun from(keyboard: Keyboard): CrossReferencingMap {
            val map = CrossReferencingMap()
            /*for ((pos, boxAndMappings) in keyboard.buttons) {
                if(pos.first >= 3 || pos.second >= 3) continue
                val (box, mappings) = boxAndMappings
                mappings.forEach { (theme, gesture) ->
                    map.offer(Triple(box.position, theme.text!!, GestureType.fromInstance(gesture)))
                }
            }*/
            return map
        }
    }
}
