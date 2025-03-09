package com.galacticware.applicationsd

import com.galacticware.griddle.domain.model.shared.GenericGestureType
import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.gesture.GestureType
import com.galacticware.griddle.domain.model.button.GestureButton
import com.galacticware.griddle.domain.model.keyboard.Keyboard
import com.galacticware.griddle.domain.model.keyboard.KeyboardHandedness
import com.galacticware.griddle.domain.model.keyboard.KeyboardOffsetAndSize
import com.galacticware.griddle.domain.model.language.LanguageTag
import com.galacticware.griddle.domain.model.layer.LayerDefinable
import com.galacticware.griddle.domain.model.layer.LayerKind
import com.galacticware.griddle.domain.model.modifier.ModifierKeyKind
import com.galacticware.griddle.domain.model.modifier.ModifierKeyState
import com.galacticware.griddle.domain.model.modifier.ModifierTheme
import com.galacticware.griddle.domain.model.modifier.ModifierThemeSet


class MockKeyboardLayer : LayerDefinable {
    override val name get() = "testLayer"
    override val languageTag = LanguageTag.ENGLISH
    override fun loadButtons(
        replacementButtons: Map<Pair<Int, Int>, Pair<GestureButton, List<Pair<ModifierTheme, Gesture>>>>?
    ) = run {
        val gestureButtons = (replacementButtons?:mapOf()).values.associate {
            it.first to it.second.associate { (_, gesture) ->
                GestureType.fromInstance(gesture) to gesture
            }.toMutableMap()
        }
        generateThemeMap(gestureButtons)

    }

    private fun generateThemeMap(
        keyboardButtons1: Map<GestureButton, Map<GestureType, Gesture>>,
    ) = keyboardButtons1.entries.associate { (gestureButton, gestures) ->
        (gestureButton.rowStart to gestureButton.colStart) to (
                gestureButton to gestures
                    .filter { (_, gesture) -> gesture.isDisplayable }
                    .map { (_, gesture) ->
                        val themes = run {
                            val operation = gesture.currentAssignment.operation
                            if (operation is MultiOperation) {
                                operation.childOps[operation.i].second.let {
                                    ModifierThemeSet(
                                        gesture.currentAssignment.modifierKeyKind,
                                        gesture.currentAssignment.noneTheme.withText(it.first),
                                        gesture.currentAssignment.onceTheme.withText(it.second),
                                        gesture.currentAssignment.repeatTheme.withText(it.third),
                                    )
                                }
                            } else {
                                gesture.currentAssignment.modifierThemeSet
                            }
                        }
                        val modifierState = when (themes.modifierKeyKind) {
                            ModifierKeyKind.SHIFT -> Keyboard.shiftState
                            ModifierKeyKind.CONTROL -> Keyboard.ctrlState
                            ModifierKeyKind.ALT -> Keyboard.altState
                        }
                        when (modifierState) {
                            ModifierKeyState.OFF -> themes.none
                            ModifierKeyState.ONE_SHOT -> themes.once
                            ModifierKeyState.ON -> themes.repeat
                        }.let {
                            it.withText((if (it.text.let { t -> t?.length == 1 && !t[0].isLetter() } &&
                                Keyboard.shiftState != ModifierKeyState.OFF)
                                it.withText(gesture.currentAssignment.noneTheme.text ?: "")
                            else
                                it).text ?: "") to gesture
                        }
                    }
                )
    }

    override val isPrimary: Boolean = true
    override var defaultModifierTheme: ModifierTheme = MockedModifierThemeSet.none
    override val keyboardHandedness: KeyboardHandedness = KeyboardHandedness(true, 4)
    override var rowHeight: Int = 100
    override var colWidth: Int = 100
    override val colSpan: Int = 1
    override val rowSpan: Int = 1
    override val originalRowHeight: Int = 100
    override val originalColWidth: Int = 100
    override var offsetX: Float = 0f
    override fun resizeToFitScreen() {
        TODO("Not yet implemented")
    }

    override fun saveBoardPositionAndSize(value: KeyboardOffsetAndSize) {
        TODO("Not yet implemented")
    }

    override fun updateGesture(gesture: Gesture, genericGestureType: GenericGestureType, buttonContainingFirstPoint: GestureButton) {
        gestureButtonBuilders = gestureButtonBuilders.map { builder ->
            if(builder.gridPosition == buttonContainingFirstPoint.position) {
                val withGesture = builder.withGesture(gesture)
                withGesture
            } else builder
        }.toMutableSet()
    }

    override var gestureButtonBuilders = mutableSetOf(MockedButtonBuilder)
    override val layerKind: LayerKind = LayerKind.USER_DEFINED
}