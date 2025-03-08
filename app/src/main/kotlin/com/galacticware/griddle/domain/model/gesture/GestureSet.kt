package com.galacticware.griddle.domain.model.gesture

/**
 * A helper class tho facilitate passing some gestures to a function
 */
class GestureSet {
    fun toSet(): MutableSet<Gesture> {
        val set = mutableSetOf<Gesture>()
        click?.let {set.add(it)}
        hold?.let {set.add(it)}
        swipeNorth?.let {set.add(it)}
        swipeNorthEast?.let {set.add(it)}
        swipeEast?.let {set.add(it)}
        swipeSouthEast?.let {set.add(it)}
        swipeSouth?.let {set.add(it)}
        swipeSouthWest?.let {set.add(it)}
        swipeWest?.let {set.add(it)}
        swipeNorthWest?.let {set.add(it)}
        boomerangNorth?.let {set.add(it)}
        boomerangNorthEast?.let {set.add(it)}
        boomerangEast?.let {set.add(it)}
        boomerangSouthEast?.let {set.add(it)}
        boomerangSouth?.let {set.add(it)}
        boomerangSouthWest?.let {set.add(it)}
        boomerangWest?.let {set.add(it)}
        boomerangNorthWest?.let {set.add(it)}
        circleClockwise?.let {set.add(it)}
        circleCounterClockwise?.let {set.add(it)}
        return set
    }

    var click: Click? = null
    var hold: Hold? = null
    var swipeNorth: SwipeNorth? = null
    var swipeNorthEast: SwipeNorthEast? = null
    var swipeEast: SwipeEast? = null
    var swipeSouthEast: SwipeSouthEast? = null
    var swipeSouth: SwipeSouth? = null
    var swipeSouthWest: SwipeSouthWest? = null
    var swipeWest: SwipeWest? = null
    var swipeNorthWest: SwipeNorthWest? = null

    var boomerangNorth: BoomerangNorth? = null
    var boomerangNorthEast: BoomerangNorthEast? = null
    var boomerangEast: BoomerangEast? = null
    var boomerangSouthEast: BoomerangSouthEast? = null
    var boomerangSouth: BoomerangSouth? = null
    var boomerangSouthWest: BoomerangSouthWest? = null
    var boomerangWest: BoomerangWest? = null
    var boomerangNorthWest: BoomerangNorthWest? = null

    var circleClockwise: CircleClockwise? = null
    var circleCounterClockwise: CircleCounterClockwise? = null
}