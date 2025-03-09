package com.galacticware.griddle.domain.model.shared

/**
 * A data class for points with higher precision.
 * */
data class Point(val x: Double, val y: Double) {
    companion object {
        private const val FADE_AWAY_DURATION = 1000L
    }
    val birthTime = System.currentTimeMillis()

    var timeSpentDown = null as Long?
    fun reportUp(time: Long) {
        timeSpentDown = time - birthTime
        upTime = time
    }

    private var upTime = null as Long?

    val opacity get() = run {
        upTime?.let { anUpTime -> timeSpentDown?.let{ aTimeSpentDown ->
            val time = System.currentTimeMillis()
            /**
             * This is a fade away effect that will last for [FADE_AWAY_DURATION] milliseconds.
             * Choose one of the following styles:
             * */
            val duration = time - anUpTime + aTimeSpentDown
            1f - duration.coerceAtMost(FADE_AWAY_DURATION) / FADE_AWAY_DURATION
        } }?: 1f
    }

    // Define an operator to subtract two points
    operator fun minus(other: Point) = Point(x - other.x, y - other.y)

    // Define an operator to add two points
    operator fun plus(other: Point) = Point(x + other.x, y + other.y)

    // Define an operator to scale a point by a scalar
    operator fun times(scalar: Double) = Point(x * scalar, y * scalar)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Point

        if (x != other.x) return false
        if (y != other.y) return false
        if (birthTime != other.birthTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + birthTime.hashCode()
        return result
    }

    fun roundToInt(): Pair<Int, Int> = Pair(x.toInt(), y.toInt())
}