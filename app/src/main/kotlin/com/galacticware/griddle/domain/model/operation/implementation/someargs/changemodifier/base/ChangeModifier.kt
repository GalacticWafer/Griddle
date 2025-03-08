package com.galacticware.griddle.domain.model.operation.implementation.someargs.changemodifier.base

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.gesture.KeyboardContext
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import com.galacticware.griddle.domain.model.keyboard.Keyboard
import com.galacticware.griddle.domain.model.modifier.ModifierAction
import com.galacticware.griddle.domain.model.modifier.ModifierCycleDirection
import com.galacticware.griddle.domain.model.modifier.ModifierCycleDirection.*
import com.galacticware.griddle.domain.model.modifier.ModifierKeyKind
import com.galacticware.griddle.domain.model.modifier.ModifierKeyState
import com.galacticware.griddle.domain.model.operation.base.OperationTag
import com.galacticware.griddle.domain.model.operation.base.ParameterizedOperation
import com.galacticware.griddle.domain.model.usercontolled.userdefinedgesturemapping.ReassignmentData
import com.galacticware.griddle.domain.viewmodel.BuildYourOwnKeyboardViewModel
import kotlinx.serialization.json.Json

object ChangeModifier : ParameterizedOperation<ChangeModifierArgs>({}) {
    override val menuItemDescription: String
        get() = "Change the state of a modifier key"
    override val shouldKeepDuringTurboMode: Boolean
        get() = false
    override val tag: OperationTag
        get() = OperationTag.CHANGE_MODIFIER
    override val appSymbol: AppSymbol?
        get() = null
    override val userHelpDescription: String
        get() = "Choose a modifier and an action to apply to it."
    override var isBackspace: Boolean
        get() = false
        set(value) {}
    override val requiresUserInput: Boolean
        get() = false

    override fun provideArgs(jsonString: String): ChangeModifierArgs =
        Json.decodeFromString<ChangeModifierArgs>(jsonString)

    @Composable
    override fun ShowArgsFinalizationScreen(context: Context, gesture: Gesture) {
        val byokViewModel: BuildYourOwnKeyboardViewModel = viewModel()
        Column {
            Text("Choose which screen this gesture should switch to.")
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                ChangeModifierArgs.instances.forEach { changeModifierArgs ->
                    item {
                        Button(
                            onClick = {
                                val request = ReassignmentData(
                                    draftGesture = gesture.apply {
                                        assignment.withArgs(changeModifierArgs)
                                            .withOperation(ChangeModifier) // do we even need this line?
                                    },
                                    operation = ChangeModifier,
                                    args = changeModifierArgs,
                                )
                                byokViewModel.setReassignmentData(request)
                                byokViewModel.setAskForConfirmation("Are you sure you want to replace this gesture with '${ChangeModifier}-$changeModifierArgs'?")
                            }
                        ) {
                            Text(
                                text = changeModifierArgs.description(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                            )
                        }
                    }
                }
            }
        }
    }

    override fun executeOperation(keyboardContext: KeyboardContext) = run {
        val args = keyboardContext.modifierKeyHandlerArgs
        val kind = args.modifierKeyKind
        val state = Keyboard.modifierKeyStateFor(kind)
        when (args.modifierAction) {
            ModifierAction.ONE_SHOT -> applyOneShotModifier(kind, state)
            ModifierAction.RELEASE -> releaseModifier(kind, state)
            ModifierAction.CYCLE -> cycleModifier(args.cycleDirection!!, kind)
            ModifierAction.TOGGLE -> toggleModifier(kind)
        }
    }

    private fun cycleModifier(cycleDirection: ModifierCycleDirection, kind: ModifierKeyKind) {
        Keyboard.cycleToNextModifierKeyStateFor(kind, cycleDirection)
    }

    private fun applyOneShotModifier(kind: ModifierKeyKind, currentState: ModifierKeyState) {
        when (currentState) {
            ModifierKeyState.NONE -> cycleModifier(FORWARD, kind)
            ModifierKeyState.ONCE -> {}
            ModifierKeyState.REPEAT -> cycleModifier(REVERSE, kind)
        }
    }

    private fun releaseModifier(kind: ModifierKeyKind, currentState: ModifierKeyState) {
        when (currentState) {
            ModifierKeyState.NONE -> {}
            ModifierKeyState.ONCE -> cycleModifier(REVERSE, kind)
            ModifierKeyState.REPEAT -> cycleModifier(FORWARD, kind)
        }
    }

    private fun toggleModifier(modifierKeyKind: ModifierKeyKind) {
        val state = Keyboard.modifierKeyStateFor(modifierKeyKind)
        when (state){
            ModifierKeyState.NONE -> cycleModifier(REVERSE, modifierKeyKind)
            ModifierKeyState.ONCE -> cycleModifier(FORWARD, modifierKeyKind)
            ModifierKeyState.REPEAT -> cycleModifier(FORWARD, modifierKeyKind)
        }
    }
}