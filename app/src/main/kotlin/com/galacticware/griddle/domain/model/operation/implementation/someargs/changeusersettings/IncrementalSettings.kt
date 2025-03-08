package com.galacticware.griddle.domain.model.operation.implementation.someargs.changeusersettings

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
import com.galacticware.griddle.domain.model.operation.base.ParameterizedOperation
import com.galacticware.griddle.domain.model.operation.base.OperationTag
import com.galacticware.griddle.domain.model.usercontolled.UserDefinedValues
import com.galacticware.griddle.domain.model.usercontolled.VibrationChoice
import com.galacticware.griddle.domain.model.usercontolled.userdefinedgesturemapping.ReassignmentData
import com.galacticware.griddle.domain.model.util.vibrate
import com.galacticware.griddle.domain.viewmodel.BuildYourOwnKeyboardViewModel
import kotlinx.serialization.json.Json

object ChangeUserSetting : ParameterizedOperation<ChangeUserSettingArgs>({
        keyboardContext: KeyboardContext ->
    ChangeUserSetting.executeOperation(keyboardContext)
}) {
    override val userHelpDescription: String get() = "Increase, decrease, or toggle a user setting"
    override var isBackspace: Boolean = false
    override val name: String
        get() = "ChangeUserSetting"
    override val menuItemDescription: String
        get() = userHelpDescription
    override val requiresUserInput: Boolean
        get() = true

    override fun provideArgs(jsonString: String): ChangeUserSettingArgs =
        Json.decodeFromString<ChangeUserSettingArgs>(jsonString)

    @Composable
    override fun ShowArgsFinalizationScreen(context: Context, gesture: Gesture) {
        val byokViewModel: BuildYourOwnKeyboardViewModel = viewModel()
        Column {
            Text("Choose which screen this gesture should switch to.")
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                ChangeUserSettingArgs.instances.forEach { changeUserSettingArgs ->
                    item {
                        Button(
                            onClick = {
                                val request = ReassignmentData(
                                    draftGesture = gesture.apply {
                                        assignment.withArgs(changeUserSettingArgs)
                                            .withOperation(ChangeUserSetting) // do we even need this line?
                                    },
                                    operation = ChangeUserSetting,
                                    args = changeUserSettingArgs,
                                )
                                byokViewModel.setReassignmentData(request)
                                byokViewModel.setAskForConfirmation("Are you sure you want to replace this gesture with '${ChangeUserSetting}-$changeUserSettingArgs'?")
                            }
                        ) {
                            Text(
                                text = changeUserSettingArgs.description(),
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

    override val shouldKeepDuringTurboMode: Boolean
        get() = false
    override val tag: OperationTag
        get() = OperationTag.CHANGE_USER_SETTING
    private var currentArgs: ChangeUserSettingArgs? = null
    override fun executeOperation(keyboardContext: KeyboardContext) {
        val appContext = keyboardContext.context

        val keyboard = keyboardContext.keyboard
        val (setting, incrementalAdjustmentType) = keyboardContext.changeUserSettingArgs
        keyboard.changeIncrementalSetting(
            appContext,
            setting,
            incrementalAdjustmentType
        )

        if(UserDefinedValues.current.userVibration.toggledChoice == VibrationChoice.ON) {
            vibrate(appContext)
        }
    }

    fun load(args: ChangeUserSettingArgs) {
        currentArgs = args
    }
}