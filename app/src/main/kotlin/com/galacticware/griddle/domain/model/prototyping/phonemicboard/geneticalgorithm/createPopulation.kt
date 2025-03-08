package com.galacticware.griddle.domain.model.prototyping.phonemicboard.geneticalgorithm

import androidx.compose.runtime.Composable
import com.galacticware.griddle.domain.model.keyboard.system.layerkind.AbstractKeyboardLayer
import com.galacticware.griddle.domain.model.prototyping.phonemicboard.OccurrenceRatios
import com.galacticware.griddle.domain.model.prototyping.phonemicboard.SampleText

@Composable
private fun createPopulation(occurrences: OccurrenceRatios): List<CrossReferencingMap> {
    var population = (0 until 100).map {
        occurrences.generateGridKeymapTemplate(3, 3)
    }.withIndex().sortedBy {
        AbstractKeyboardLayer.score(it.value, SampleText.s)
    }.map { it.value }
    return population
}

/**
 * Create 4 children per couple
 */
fun breed(
    parents: List<Pair<
            CrossReferencingMap,
            CrossReferencingMap,
            >>
): List<CrossReferencingMap> = run {
    val symbols = parents.first().first.entries.flatMap { x ->
        x.value.entries.map { it.key }
    }.toMutableSet()
    parents.map { (a, b) ->
        val chromosomesCount = a.size
        val children = (0 until 4).map {
            val child = CrossReferencingMap()

            val unconsumedStrings = symbols.toMutableSet()
            val uniquePositonsToGestureTypes = a.genes().plus(b.genes())
                .map {
                    it.second
                }.toMutableSet()

            val zip = unconsumedStrings.zip(uniquePositonsToGestureTypes)
            if (zip.size != chromosomesCount) {
                throw IllegalStateException("Size mismatch")
            }
            zip.forEach { (symbol, gestureType) ->
                val (position, gesture) = gestureType
                child.offer(Triple(position, symbol, gesture))
            }
            child
        }
        children
    }.flatten()
}
