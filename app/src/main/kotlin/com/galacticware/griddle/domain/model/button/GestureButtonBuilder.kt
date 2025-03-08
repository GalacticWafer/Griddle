package com.galacticware.griddle.domain.model.button

import androidx.compose.ui.unit.IntSize
import com.galacticware.griddle.domain.model.geometry.AxialParams
import com.galacticware.griddle.domain.model.geometry.CartesianAxis
import com.galacticware.griddle.domain.model.geometry.GridPosition
import com.galacticware.griddle.domain.model.geometry.StartAndSpan
import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.gesture.GestureType
import com.galacticware.griddle.domain.model.appsymbol.SettingsValue
import com.galacticware.griddle.domain.model.keyboard.definition.theme.DEFAULT_PRIMARY_THEME
import com.galacticware.griddle.domain.model.keyboard.definition.theme.DEFAULT_SIZE
import com.galacticware.griddle.domain.model.keyboard.system.layerkind.AbstractKeyboardLayer
import com.galacticware.griddle.domain.model.modifier.ModifierTheme

class GestureButtonBuilder(
    val gridPosition: GridPosition,
    val gestureSet: MutableSet<Gesture>,
    val size: IntSize,
    var modifierTheme: ModifierTheme,
    val settingsValueProvider: SettingsValueProvider?,
    val isPeripheral: Boolean = false,
) : () -> GestureButton {
    val model: GestureButtonModel get() = GestureButtonModel(
        gridPosition,
        gestureSet.associate {
            GestureType.fromInstance(it) to it.model
        }.toMutableMap(),
        size,
        modifierTheme,
        SettingsValue.entries.firstOrNull { it.provider == settingsValueProvider},
        isPeripheral,
    )

    val colStart get() = gridPosition.colStart
    val colSpan get() = gridPosition.colSpan
    val rowStart get() = gridPosition.rowStart
    val rowSpan get() = gridPosition.rowSpan

    /**
     * Call this object as a function to return the corresponding GestureButton instance.
     */
    override fun invoke(): GestureButton = run {
        GestureButton(
            gridPosition = gridPosition,
            widthRuler = size.width,
            heightRuler = size.height,
            modifierTheme = modifierTheme,
            gestureSet = gestureSet,
            settingsValueProvider = settingsValueProvider,
            isPeripheral = isPeripheral,
            builder = this,
        )
    }

    /**
     * Return a deep copy of this GestureButtonBuilder with a new position according to the parameters.
     */
    fun reposition(
        colStart: Int = gridPosition.colStart,
        rowStart: Int = gridPosition.rowStart,
        colSpan: Int = gridPosition.colSpan,
        rowSpan: Int = gridPosition.rowSpan,
    ): GestureButtonBuilder {
        return GestureButtonBuilder(
            gridPosition = GridPosition(
                rowParams = AxialParams(CartesianAxis.Y, StartAndSpan(rowStart, rowSpan)),
                colParams = AxialParams(CartesianAxis.X, StartAndSpan(colStart, colSpan)),
            ),
            gestureSet = gestureSet.map { it }.toMutableSet(),
            size = IntSize(size.width, size.height),
            modifierTheme = modifierTheme.copy(),
            settingsValueProvider = settingsValueProvider,
            isPeripheral = isPeripheral,
        )
    }

    /**
     * Return a deep copy of this GestureButtonBuilder with a new gesture according to the parameters.
     */
    fun withGesture(gesture: Gesture) = run {
        val copy = copy()
        copy.gestureSet.add(gesture)
        copy
    }

    private fun copy() = GestureButtonBuilder(
        gridPosition = gridPosition,
        gestureSet = gestureSet.toMutableSet(),
        size = IntSize(size.width, size.height),
        modifierTheme = modifierTheme.copy(),
        settingsValueProvider = settingsValueProvider,
        isPeripheral = isPeripheral,
    )

    /**
     * Removes the first gesture that matches the given function.
     */
    fun withoutGesture(function: (Gesture) -> Boolean): GestureButtonBuilder {
        val copy = copy()
        val gesture = copy.gestureSet.firstOrNull { function(it) }
        copy.gestureSet.remove(gesture)
        return copy
    }

    /**
     * Removes all gestures that input letters.
     */
    fun withoutLetters(): GestureButtonBuilder {
        val copy = copy()
        copy.gestureSet.filter { g: Gesture ->
            g.currentText.let {
                it.length == 1 && it[0].isLetter()
            }
        }
            .let { copy.gestureSet.removeAll(it.toSet()) }
        return copy
    }

    fun replaceGesturesWith(vararg gestures: Gesture): GestureButtonBuilder {
        val copy = copy()
        copy.gestureSet.clear()
        copy.gestureSet.addAll(gestures)
        return copy
    }


    companion object {
        /**
         * Specify information to create a classic (20-gesture) GestureButton.
         */
        fun gestureButton(
            rowStart: Int,
            colStart: Int,
            rowSpan: Int,
            colSpan: Int,
            gestureSet: MutableSet<Gesture> = mutableSetOf(),
            size: IntSize = DEFAULT_SIZE,
            modifierTheme: ModifierTheme = DEFAULT_PRIMARY_THEME,
            settingsValueProvider: SettingsValueProvider? = null,
            isPeripheral: Boolean = false,
        ): GestureButtonBuilder = AbstractKeyboardLayer.buttonBuilder(
            rowStart = rowStart,
            colStart = colStart,
            rowSpan = rowSpan,
            colSpan = colSpan,
            gestureSet = gestureSet,
            widthRuler = size.width,
            heightRuler = size.height,
            theme = modifierTheme,
            settingsValueProvider = settingsValueProvider,
            isPeripheral = isPeripheral,
        )
    }

    override fun toString(): String {
        return "(${gridPosition.colStart},${gridPosition.rowStart},$size.width,$size.height)[${
            gestureSet.joinToString(",") {
                "{${it::class.simpleName},${it.editorOperation::class.simpleName},${it.currentText}}"
            }
        }]"
    }

    /**
     * @return the compliment of the passed filterFunction
     */
    fun without(filterFunction: (Gesture) -> Boolean): GestureButtonBuilder {
        val copy = copy()
        copy.gestureSet
            .filter { !filterFunction(it) }
            .let { copy.gestureSet.removeAll(it.toSet()) }
        return copy
    }

    fun withoutSymbols() = without { gesture: Gesture ->
        gesture.currentText.let { t ->
            !(t.length == 1 && !t[0].isDigit() && !t[0].isLetter())
        }
    }

    fun update(newGesture: Gesture) {
        gestureSet.removeIf { it::class == newGesture::class }
        gestureSet.add(newGesture)
    }
}