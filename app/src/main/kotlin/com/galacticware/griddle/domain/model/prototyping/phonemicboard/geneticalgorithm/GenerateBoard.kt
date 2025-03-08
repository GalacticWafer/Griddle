package com.galacticware.griddle.domain.model.prototyping.phonemicboard.geneticalgorithm

import com.galacticware.griddle.domain.model.keyboard.Keyboard

fun generateBoard() : Keyboard? {
    /*   val occurrences = OccurrenceRatios.report(SampleText.s)
  Log.d(TAG, "Occurrence Ratios: ${occurrences.nGramFrequencies()}")

  var population = createPopulation(occurrences) + CrossReferencingMap.from(
      DefaultBoard.instance
  )
  var doReturn = 1 as Int?

  val maxGenerations = 10000
  var maxGenerationsWithoutImprovement = 5
  val mutationRate = 0.0005
  var generationsWithoutImprovement = 0
  var currentBestScore = [AbstractKeyboardLayer].score(population.first(), SampleText.s)
  for (generationCount in 0 until maxGenerations) {
      if(doReturn == null) break
      population.let { candidates ->
          val bestCandidate = candidates.first()
          val goodCandidates =
              candidates.slice(1 until population.size / 2 - population.size / 10)
          val luckyCandidates =
              candidates.slice(population.size / 2 until population.size).shuffled()
                  .take(population.size / 10)
          val nonFirsts = (goodCandidates + luckyCandidates).shuffled()
          val parents = (0 until 5).map { Pair(bestCandidate, nonFirsts.random()) } +
                  (5 until population.size / 4).map {
                      nonFirsts.shuffled().take(2).let { it[0] to it[1] }
                  }
          val nextGeneration =
              mutableListOf<CrossReferencingMap>()
          val bestOffspring = breed(
              (0 until 5).map { Pair(bestCandidate, nonFirsts.random()) }
          ).filter { it.size == bestCandidate.size }

          while (nextGeneration.size < population.size) {
              nextGeneration.addAll(bestOffspring.plus(breed(parents).filter { it.size ==
                      bestCandidate.size }))
          }
          val slice = population.slice(0 until 5)
          population = (slice + (slice.let {sl ->
              val x = mutableListOf<CrossReferencingMap>()
              while (x.size < population.size / 2) {
                  x.addAll(breed(sl.shuffled().windowed(2).map{it[0] to it[1]}))
              }
              x
          }) + nextGeneration.let { pop ->
              pop.map { layer ->
                  if (rand.nextFloat() < mutationRate) {
                      (0..3).map {
                          val boxInfo1 = layer.entries.shuffled().random()
                          val gesture1 = boxInfo1.value.entries.shuffled().random()
                          val boxInfo2 = layer.entries.shuffled().random()
                          val gesture2 = boxInfo2.value.entries.shuffled().random()
                          val temp = gesture1.value
                          gesture1.setValue(gesture2.value)
                          gesture2.setValue(temp)
                      }
                  } else {
                      val (a, b) = layer.entries.shuffled().take(2).let {it[0] to it[1]}
                      layer.remove(a)
                      layer.remove(b)
                      layer.offer(a.key, b.value)
                      layer.offer(b.key, a.value)
                  }
                  layer
              }
              pop
          }).sortedBy { [AbstractKeyboardLayer].score(it, SampleText.s) }
          .slice(0 until 100)
          Log.d(TAG, "Generation $generationCount: best: ${[AbstractKeyboardLayer].score(population.first(),
              SampleText.s)}")
          if ([AbstractKeyboardLayer].score(population.first(), SampleText.s) == currentBestScore) {
              generationsWithoutImprovement++
          } else {
              generationsWithoutImprovement = 0
              currentBestScore = [AbstractKeyboardLayer].score(population.first(), SampleText.s)
          }
          if (generationsWithoutImprovement >= maxGenerationsWithoutImprovement) {
              Log.d(TAG, "Apocolypse!!")
              // population = createPopulation(occurrences).let { newPop ->
              //     (breed(newPop.zip(population)) + population + newPop).toSet()
              // }.sortedBy { [AbstractKeyboardLayer].score(it, SampleText.s) }
              // maxGenerationsWithoutImprovement = 0
              // currentBestScore = [AbstractKeyboardLayer].score(population.first(), SampleText.s)
              doReturn = null
          }
          doReturn?: return@let population
      }
  }

  val positionToButtonMap = population.first().let { crossReferencingMap ->
      crossReferencingMap.entries.map { (it, ma) ->
          val position = it.rowStart to it.colStart
          val gestures = ma.entries.map { (k, v) -> bind(v, simpleInput, withSymbol(k)) }
          val textThemePairs = gestures
              .map { g -> DefaultBoard.defaultColorTheme to g }
          makeClassicGestureButton(
              position.first,
              position.second,
              it.rowSpan,
              it.colSpan,
              gestures.toMutableSet(),
          ) to (position to textThemePairs)
      }.associate {
          val box = it.first()
          it.second.first to (box to it.second.second)
      }
  } */
    return null
}