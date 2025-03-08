package com.galacticware.griddle.domain.model.gesture

import com.galacticware.griddle.domain.model.error.Errors
import com.galacticware.griddle.domain.model.geometry.GridArea
import com.galacticware.griddle.domain.model.geometry.RectangleLocation
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import com.galacticware.griddle.domain.model.modifier.ModifierKeyKind
import com.galacticware.griddle.domain.model.modifier.ModifierThemeSet
import com.galacticware.griddle.domain.model.operation.implementation.noargs.noop.NoOp
import com.galacticware.griddle.domain.model.operation.base.Operation

enum class GestureType(
    val rectangleLocation: RectangleLocation? = null,
    val optionsLabel: String? = null,
) {
    BOOMERANG_DOWN(optionsLabel = "Boomerang Down"),
    BOOMERANG_DOWN_LEFT(optionsLabel = "Boomerang Down-Left"),
    BOOMERANG_DOWN_RIGHT(optionsLabel = "Boomerang Down-Right"),
    BOOMERANG_LEFT(optionsLabel = "Boomerang Left"),
    BOOMERANG_RIGHT(optionsLabel = "Boomerang Right"),
    BOOMERANG_UP(optionsLabel = "Boomerang Up"),
    BOOMERANG_UP_LEFT(optionsLabel = "Boomerang Up-Left"),
    BOOMERANG_UP_RIGHT(optionsLabel = "Boomerang Up-Right"),
    CIRCLE_ANTI_CLOCKWISE(optionsLabel = "Circle Counter-Clockwise"),
    CIRCLE_CLOCKWISE(optionsLabel = "Circle Clockwise"),
    HOLD(optionsLabel = "Hold (long-press"),
    CLICK(RectangleLocation.center, optionsLabel = "Tap"),
    SWIPE_DOWN(RectangleLocation.bottomCenter, optionsLabel = "Swipe Down"),
    SWIPE_DOWN_LEFT(RectangleLocation.bottomLeft, optionsLabel = "Swipe Down-Left"),
    SWIPE_DOWN_RIGHT(RectangleLocation.bottomRight, optionsLabel = "Swipe Down-Right"),
    SWIPE_LEFT(RectangleLocation.left, optionsLabel = "Swipe Left"),
    SWIPE_RIGHT(RectangleLocation.right, optionsLabel = "Swipe Right"),
    SWIPE_UP(RectangleLocation.topCenter, optionsLabel = "Swipe Up"),
    SWIPE_UP_LEFT(RectangleLocation.topLeft, optionsLabel = "Swipe Up-Left"),
    SWIPE_UP_RIGHT(RectangleLocation.topRight, optionsLabel = "Swipe Up-Right"),
    ;

    val rowStart: Int = rectangleLocation?.rowStart ?: 1
    val colStart: Int = rectangleLocation?.colStart ?: 1

    fun definition(): GridArea? = when (this) {
        SWIPE_UP_LEFT -> GridArea.oneUnit
        BOOMERANG_UP_LEFT -> null
        SWIPE_UP -> GridArea.oneUnit
        BOOMERANG_UP -> null
        SWIPE_UP_RIGHT -> GridArea.oneUnit
        BOOMERANG_UP_RIGHT -> null
        SWIPE_RIGHT -> GridArea.oneUnit
        BOOMERANG_RIGHT -> null
        SWIPE_DOWN_RIGHT -> GridArea.oneUnit
        BOOMERANG_DOWN_RIGHT -> null
        SWIPE_DOWN -> GridArea.oneUnit
        BOOMERANG_DOWN -> null
        SWIPE_DOWN_LEFT -> GridArea.oneUnit
        BOOMERANG_DOWN_LEFT -> null
        SWIPE_LEFT -> GridArea.oneUnit
        BOOMERANG_LEFT -> null
        CIRCLE_ANTI_CLOCKWISE -> null
        CIRCLE_CLOCKWISE -> null
        CLICK -> GridArea.oneUnit
        HOLD -> null
    }

    fun gestureClass() = when (this) {
        SWIPE_UP_LEFT -> SwipeNorthWest::class.java
        BOOMERANG_UP_LEFT -> BoomerangNorthWest::class.java
        SWIPE_UP -> SwipeNorth::class.java
        BOOMERANG_UP -> BoomerangNorth::class.java
        SWIPE_UP_RIGHT -> SwipeNorthEast::class.java
        BOOMERANG_UP_RIGHT -> BoomerangNorthEast::class.java
        SWIPE_RIGHT -> SwipeEast::class.java
        BOOMERANG_RIGHT -> BoomerangEast::class.java
        SWIPE_DOWN_RIGHT -> SwipeSouthEast::class.java
        BOOMERANG_DOWN_RIGHT -> BoomerangSouthEast::class.java
        SWIPE_DOWN -> SwipeSouth::class.java
        BOOMERANG_DOWN -> BoomerangSouth::class.java
        SWIPE_DOWN_LEFT -> SwipeSouthWest::class.java
        BOOMERANG_DOWN_LEFT -> BoomerangSouthWest::class.java
        BOOMERANG_LEFT -> BoomerangWest::class.java
        SWIPE_LEFT -> SwipeWest::class.java
        CIRCLE_ANTI_CLOCKWISE -> CircleCounterClockwise::class.java
        CIRCLE_CLOCKWISE -> CircleClockwise::class.java
        CLICK -> Click::class.java
        HOLD -> Hold::class.java
    }

    fun newInstance(
        operation: Operation,
        modifiers: Set<ModifierKeyKind> = setOf(),
        modifierThemeSet: ModifierThemeSet,
        symbol: AppSymbol?,
        isIndicator: Boolean = false,
        overrideMetaState: Boolean,
        respectShift: Boolean,
        isPeripheral: Boolean,
        keycode: Int?,
        argsJson: String?,
    ) = run {
        val gestureAssignment = GestureAssignment(
            operation,
            modifiers,
            true,
            definition() ?: GridArea.oneUnit,
            modifierThemeSet,
            symbol,
            isIndicator,
            overrideMetaState,
            respectShift,
            isPeripheral,
            keycode,
            argsJson,
        )
        when (this) {
            SWIPE_UP_LEFT -> SwipeNorthWest(gestureAssignment)
            BOOMERANG_UP_LEFT -> BoomerangNorthWest(gestureAssignment)
            SWIPE_UP -> SwipeNorth(gestureAssignment)
            BOOMERANG_UP -> BoomerangNorth(gestureAssignment)
            SWIPE_UP_RIGHT -> SwipeNorthEast(gestureAssignment)
            BOOMERANG_UP_RIGHT -> BoomerangNorthEast(gestureAssignment)
            SWIPE_RIGHT -> SwipeEast(gestureAssignment)
            BOOMERANG_RIGHT -> BoomerangEast(gestureAssignment)
            SWIPE_DOWN_RIGHT -> SwipeSouthEast(gestureAssignment)
            BOOMERANG_DOWN_RIGHT -> BoomerangSouthEast(gestureAssignment)
            SWIPE_DOWN -> SwipeSouth(gestureAssignment)
            BOOMERANG_DOWN -> BoomerangSouth(gestureAssignment)
            SWIPE_DOWN_LEFT -> SwipeSouthWest(gestureAssignment)
            BOOMERANG_DOWN_LEFT -> BoomerangSouthWest(gestureAssignment)
            BOOMERANG_LEFT -> BoomerangWest(gestureAssignment)
            SWIPE_LEFT -> SwipeWest(gestureAssignment)
            CIRCLE_ANTI_CLOCKWISE -> CircleCounterClockwise(gestureAssignment)
            CIRCLE_CLOCKWISE -> CircleClockwise(gestureAssignment)
            CLICK -> Click(gestureAssignment)
            HOLD -> Hold(gestureAssignment)
        }
    }

    fun withAssignment(gestureAssignment: GestureAssignment, swapAssignment: GestureAssignment?): Gesture {
        val gesture = newInstance(
            NoOp,
            modifierThemeSet = ModifierThemeSet.BLANK,
            symbol = null,
            overrideMetaState = false,
            respectShift = true,
            isPeripheral = false,
            keycode = null,
            argsJson = null
        )
        gesture.assignment = gestureAssignment
        gesture.swapAssignment = swapAssignment
        return gesture
    }

    companion object {
        val circularTypes = setOf(
            CIRCLE_ANTI_CLOCKWISE,
            CIRCLE_CLOCKWISE,
        )

        val visibleTypes
            get() = setOf(
                SWIPE_UP_LEFT,
                SWIPE_UP,
                SWIPE_UP_RIGHT,
                SWIPE_RIGHT,
                SWIPE_DOWN_RIGHT,
                SWIPE_DOWN,
                SWIPE_DOWN_LEFT,
                SWIPE_LEFT,
                CLICK,
            )

        fun fromInstance(gesture: Gesture): GestureType =
            entries.firstOrNull { it.gestureClass() == gesture::class.java }
                ?: throw Errors.UNKNOWN_GESTURE_TYPE.send("No gesture type for class $gesture")
    }
}