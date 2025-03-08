package com.galacticware.griddle.domain.model.prototyping.phonemicboard

import com.galacticware.griddle.domain.model.geometry.GridPosition
import com.galacticware.griddle.domain.model.gesture.GestureType
import com.galacticware.griddle.domain.model.prototyping.phonemicboard.geneticalgorithm.CrossReferencingMap
import java.util.Stack
import kotlin.math.min

 

/**
 * Counts the occurrences of n-grams, words, and spaces for use in creation of a report which can be used to test the
 * keyboard with, and generate a CrossReferencingMap with.
 */
data class OccurrenceRatios(
    val words: Map<String, Int>,
    val characters: List<Map.Entry<Char, Int>>,
    val bigrams: List<Map.Entry<String, Int>>,
    val trigrams: List<Map.Entry<String, Int>>,
    val spacesCount: Int,
){
    companion object {
        fun report(
            inputText: String,
            omitWords: Set<String> = setOf(),
        ) = run {
            val spacesCount = inputText.count { it == ' ' }
            val words: Map<String, Int> = Regex("\\w+")
                .findAll(inputText.lowercase().let {
                    var s1 = it
                    omitWords.forEach { omission ->
                        s1 = it.replace(omission, "")
                    }
                    s1
                })
                .map { it.value }
                .groupingBy { it }
                .eachCount()
                .let { stringToIntMap ->
                    val stringToIntMap1 = stringToIntMap.toMutableMap()
                    omitWords.forEach { stringToIntMap1.remove(it) }
                    stringToIntMap1
                }

            val characters = run {
                val dict = mutableMapOf<Char, Int>()
                words.forEach { (word, count) ->
                    word.forEach { char ->
                        dict[char] = (dict[char] ?: 0) + count
                    }
                }
                dict.entries.sortedBy { it.value * -1 }
            }
            val bigrams = run {
                val dict = mutableMapOf<String, Int>()
                words.keys.forEach { word ->
                    word.windowed(2).forEach { bigram ->
                        dict[bigram] = (dict[bigram] ?: 0) + words[word]!!
                    }
                }
                dict.entries.sortedBy { it.value * -1 }
            }
            val trigrams = run {
                val dict = mutableMapOf<String, Int>()
                words.keys.forEach { word ->
                    word.windowed(3).forEach { trigram ->
                        dict[trigram] = (dict[trigram] ?: 0) + words[word]!!
                    }
                }
                dict.entries.sortedBy { it.value * -1 }
            }
            OccurrenceRatios(words, characters, bigrams, trigrams, spacesCount)
        }
    }

    override fun toString(): String =
        "Words: ${words.entries.sortedBy { it.value * -1}}\n" +
                "chars: $characters\n" +
                "bigrams: $bigrams\n" +
                "trigrams: $trigrams"

    fun nGramFrequencies() = characters.asSequence()
        .map { mapOf("${it.key}" to it.value).entries.first() }
        .plus(listOf(mapOf(" " to spacesCount).entries.first()))
        // .plus(bigrams)
        // .plus(trigrams)
        .shuffled()
    // .sortedBy { it.value * -1}
    // .map { it.key to it.value }
    val m = mapOf(
        "ə" to 11.49,
        "n" to 7.11,
        "r" to 6.94,
        "t" to 6.91,
        "ɪ" to 6.32,
        "s" to 4.75,
        "d" to 4.21,
        "l" to 3.96,
        "i" to 3.61,
        "k" to 3.18,
        "ð" to 2.95,
        "ɛ" to 2.86,
        "m" to 2.76,
        "z" to 2.76,
        "p" to 2.15,
        "æ" to 2.10,
        "v" to 2.01,
        "w" to 1.95,
        "u" to 1.93,
        "b" to 1.80,
        "e" to 1.79,
        "ʌ" to 1.74,
        "f" to 1.71,
        "aɪ" to 1.50,
        "ɑ" to 1.45,
        "h" to 1.40,
        "o" to 1.25,
        "ɒ" to 1.18,
        "ŋ" to 0.99,
        "ʃ" to 0.97,
        "j" to 0.81,
        "g" to 0.80,
        "dʒ" to 0.59,
        "tʃ" to 0.56,
        "aʊ" to 0.50,
        "ʊ" to 0.43,
        "θ" to 0.41,
        "ɔɪ" to 0.10,
        "ʒ" to 0.07,
    )

    private val visibleGestureTypes = object {
        private val _shuffledBatch
            get() = GestureType.visibleTypes.shuffled().let { list ->
                val stack = Stack<GestureType>()
                list.forEach { stack.push(it) }
                stack
            }
        private var shuffledGestureTypesIterator = _shuffledBatch

        fun next() : GestureType {
            if (shuffledGestureTypesIterator.isEmpty()) {
                shuffledGestureTypesIterator = _shuffledBatch
            }
            return shuffledGestureTypesIterator.pop()
        }
    }
    fun generateGridKeymapTemplate(rowCount: Int, colCount: Int)
    : CrossReferencingMap = run {
        val frequencies = nGramFrequencies().toList()
        val associate = (0 until rowCount).map { i ->
            (0 until colCount).map { j ->
                GridPosition.originUnit.withPosition(i, j)
            }
        }
            .flatten()
            .let { l ->
                val nGrams = frequencies.map { it.key }.slice(
                    0 until min(20 * l.size, frequencies.size)
                )
                val chunks = nGrams.chunked(min(20, frequencies.size / l.size))
                l.zip(chunks)
            }.associate { (point, nGrams) ->
                point to nGrams.associateWith { visibleGestureTypes.next() }.toMutableMap()
            }
        CrossReferencingMap(associate.toMutableMap())
    }
}
