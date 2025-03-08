package com.galacticware.griddle.domain.model.operation.implementation.someargs.switchscreens

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
import com.galacticware.griddle.domain.model.operation.base.ParameterizedOperation
import com.galacticware.griddle.domain.model.operation.base.OperationTag
import com.galacticware.griddle.domain.model.usercontolled.userdefinedgesturemapping.ReassignmentData
import com.galacticware.griddle.domain.model.screen.NestedAppScreen
import com.galacticware.griddle.domain.model.screen.SwitchToScreen
import com.galacticware.griddle.domain.viewmodel.BuildYourOwnKeyboardViewModel
import kotlinx.serialization.json.Json
import kotlin.enums.EnumEntries
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

data class UnsupportedOperation(
    val operationName: String,
    val property: String,
    val message: String
)

fun findUnsupportedOperationsWithMessages(
    operationsTags: EnumEntries<OperationTag>,
    messageProvider: (String) -> String = { "Operation not supported on this platform." }
): List<UnsupportedOperation> {
    val unsupportedOperations = mutableListOf<UnsupportedOperation>()

    for (tag in operationsTags) {
        val operationClass = tag::class
        val supportedOperationProperties = operationClass.declaredMemberProperties.filter {
            it.name.startsWith("isSupportedOn")
        }

        for (property in supportedOperationProperties) {
            property.isAccessible = true // Make the property accessible for reflection
            val supportedOn = property.getter.call(tag) as? Boolean ?: false

            if (!supportedOn) {
                val operationName = tag::class.simpleName ?: "Unknown Operation"
                val propertyName = property.name.removePrefix("isSupportedOn")
                val message = messageProvider(propertyName)
                unsupportedOperations.add(
                    UnsupportedOperation(
                        operationName,
                        propertyName,
                        message
                    )
                )
            }
        }
    }

    return unsupportedOperations
}
object SwitchScreens : ParameterizedOperation<SwitchScreenArgs>({ k -> SwitchScreens.executeOperation(k) }) {
    override val name get() = "SwitchNestedKeyboardScreen"
    override val tag get() = OperationTag.SWITCH_SCREENS
    override val menuItemDescription get() = "Open a user-controllable screen"
    override val userHelpDescription get() = menuItemDescription
    override val requiresUserInput: Boolean get() = false
    override val shouldKeepDuringTurboMode: Boolean
        get() = false
    override val appSymbol: AppSymbol
        get() = AppSymbol.SWITCH_NESTED_SCREEN

    override var isBackspace: Boolean
        get() = false
        set(value) {}
    override fun produceNewGesture(gesturePrototype: Gesture): Gesture =
        produceNewGestureWithAppSymbol(gesturePrototype, this, AppSymbol.SWITCH_NESTED_SCREEN)
    override fun provideArgs(jsonString: String): SwitchScreenArgs =
        Json.decodeFromString<SwitchScreenArgs>(jsonString)

    @Composable
    override fun ShowArgsFinalizationScreen(context: Context, gesture: Gesture) {
        val byokViewModel: BuildYourOwnKeyboardViewModel = viewModel()
        Column {
            Text("Choose which screen this gesture should switch to.")
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                SwitchToScreen.entries.forEach { switchableScreen ->
                    item {
                        Button(
                            onClick = {
                                val args = SwitchScreenArgs(switchableScreen)
                                val request = ReassignmentData(
                                    draftGesture = gesture.apply {
                                        assignment.withArgs(args)
                                            .withOperation(SwitchScreens) // do we even need this line?
                                    },
                                    operation = SwitchScreens,
                                    args = args,
                                )
                                byokViewModel.setReassignmentData(request)
                                byokViewModel.setAskForConfirmation("Are you sure you want to replace this gesture with '$SwitchScreens-$switchableScreen'?")
                            }
                        ) {
                            Text(
                                text = switchableScreen.prettyName,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable override fun ShowReassignmentScreen(context: Context, gesture: Gesture)
        = ShowNoArgsConfirmationScreen(context, gesture, "Are you sure you want to change this gesture to \"Open TextReplacement Editor\"?")

    override fun executeOperation(keyboardContext: KeyboardContext) {
        val (userSwitchableScreen,) = keyboardContext.switchScreenArgs
        val griddleKeyboardScreen = userSwitchableScreen.screenObject
        griddleKeyboardScreen.provideKeyboardContext(keyboardContext)
        NestedAppScreen.stack.let {
            if(it.peek() == griddleKeyboardScreen) {
                it.pop()
            } else {
                if (it.contains(griddleKeyboardScreen)) {
                    it.remove(griddleKeyboardScreen)
                }
                it.push(griddleKeyboardScreen)
            }
        }
    }
}