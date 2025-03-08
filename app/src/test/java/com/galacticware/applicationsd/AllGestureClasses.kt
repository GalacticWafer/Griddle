package com.galacticware.applicationsd

import com.galacticware.griddle.domain.model.gesture.BoomerangEast
import com.galacticware.griddle.domain.model.gesture.BoomerangNorth
import com.galacticware.griddle.domain.model.gesture.BoomerangNorthEast
import com.galacticware.griddle.domain.model.gesture.BoomerangNorthWest
import com.galacticware.griddle.domain.model.gesture.BoomerangSouth
import com.galacticware.griddle.domain.model.gesture.BoomerangSouthEast
import com.galacticware.griddle.domain.model.gesture.BoomerangSouthWest
import com.galacticware.griddle.domain.model.gesture.BoomerangWest
import com.galacticware.griddle.domain.model.gesture.CircleCounterClockwise
import com.galacticware.griddle.domain.model.gesture.CircleClockwise
import com.galacticware.griddle.domain.model.gesture.Click
import com.galacticware.griddle.domain.model.gesture.Hold
import com.galacticware.griddle.domain.model.gesture.SwipeEast
import com.galacticware.griddle.domain.model.gesture.SwipeNorth
import com.galacticware.griddle.domain.model.gesture.SwipeNorthEast
import com.galacticware.griddle.domain.model.gesture.SwipeNorthWest
import com.galacticware.griddle.domain.model.gesture.SwipeSouth
import com.galacticware.griddle.domain.model.gesture.SwipeSouthEast
import com.galacticware.griddle.domain.model.gesture.SwipeSouthWest
import com.galacticware.griddle.domain.model.gesture.SwipeWest

val AllGestureClasses = listOf(
            Click::class,
            Hold::class,
            SwipeEast::class,
            SwipeWest::class,
            SwipeNorth::class,
            SwipeSouth::class,
            SwipeNorthEast::class,
            SwipeNorthWest::class,
            SwipeSouthEast::class,
            SwipeSouthWest::class,
            BoomerangEast::class,
            BoomerangWest::class,
            BoomerangNorth::class,
            BoomerangSouth::class,
            BoomerangNorthEast::class,
            BoomerangNorthWest::class,
            BoomerangSouthEast::class,
            BoomerangSouthWest::class,
            CircleClockwise::class,
            CircleCounterClockwise::class,
        )