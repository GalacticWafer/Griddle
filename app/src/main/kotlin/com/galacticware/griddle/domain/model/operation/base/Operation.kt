package com.galacticware.griddle.domain.model.operation.base

import android.content.Context
import android.view.View
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.galacticware.griddle.domain.model.error.Errors
import com.galacticware.griddle.domain.model.geometry.GridPosition
import com.galacticware.griddle.domain.model.shared.Point
import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.gesture.KeyboardContext
import com.galacticware.griddle.domain.model.input.GriddleInputConnection
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import com.galacticware.griddle.domain.model.keyboard.Keyboard
import com.galacticware.griddle.domain.viewmodel.BuildYourOwnKeyboardViewModel
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*

/**
 * Serializer for the [Operation] class.
 */
object OperationSerializer : KSerializer<Operation> {

    // Define the descriptor for the Operation class (we're storing the enum name)
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Operation", PrimitiveKind.STRING)

    // Serialize: Save the enum name as a string
    override fun serialize(encoder: Encoder, value: Operation) {
        // Find the corresponding UserConfigurableOperationTag for the operation
        val tag = OperationTag.values().find { it.objectInstance == value }
        if (tag != null) {
            encoder.encodeString(tag.name)
        } else {
            throw SerializationException("Unknown operation: ${value::class.simpleName}")
        }
    }

    // Deserialize: Look up the enum from the serialized string, and then get the associated operation
    override fun deserialize(decoder: Decoder): Operation {
        val enumName = decoder.decodeString()  // Get the serialized enum name
        val tag = try {
            OperationTag.valueOf(enumName)  // Find the corresponding enum
        } catch (e: IllegalArgumentException) {
            throw SerializationException("Invalid operation tag: $enumName")
        }
        return tag.objectInstance
    }
}


/**
 * The base class for all gesture actions, which can control the [inputConnection] and have access
 * to all the data they need in any case by using the [KeyboardContext] class.
 */
@Serializable(with = OperationSerializer::class)
abstract class Operation(
    /**
     * The [executeFn] method is the main function to be called when the [Operation]
     * is invoked. Its [KeyboardContext] parameter contains all data
     * needed by the  [Operation] to perform its function, and modify the keyboard state.
     */
    val executeFn: (keyboardContext: KeyboardContext) -> Unit,
) : (Keyboard, Context, Gesture, List<Point>, View?, (() -> Unit)?, GridPosition) -> Unit,

    EditorOperationArgsContainer,
    /**
     * All [Operation]s must implement ways to present
     */
    OperationDescriptionProvider
{

    /**
     * If true, the operation will cause the textReplacement screen to be redacted, when a textReplacement was expanded
     * due to the previous operation.
     */
    open var isBackspace: Boolean = false
    open val  appSymbol: AppSymbol? = null
    open val shouldKeepDuringTurboMode: Boolean = false
    private lateinit var handler: KeyboardContext

    private var areTheArgsLoaded = false

    /**
     * Load the context into the operation so that thd Operation has any and everything that it may
     * need to accomplish whatever it does.
     */
    override fun loadKeyboardContext(
        keyboardContext: KeyboardContext
    ) {
        handler = keyboardContext
        areTheArgsLoaded = true
    }

    override fun toString(): String = this::class.simpleName?: super.toString()
    protected fun throwUnsupported(missingItem: String = "implementation"): Nothing = throw Errors.UNSUPPORTED_OPERATION_REMAPPING.send(
        "No $missingItem found for ${this::class.simpleName}")
    override val userHelpDescription: String get() { throwUnsupported("user help description") }
    override val menuItemDescription: String get() { throwUnsupported("menu item description") }
    override val name: String get() = this::class.simpleName?:""
    override val tag: OperationTag get() { throwUnsupported("operation tag") }
    @Composable override fun ShowReassignmentScreen(context: Context, gesture: Gesture) { throwUnsupported("reassignment screen") }
    override fun produceNewGesture(gesturePrototype: Gesture): Gesture { throwUnsupported("new gesture producer") }
    protected fun produceNewGestureWithAppSymbol(gesturePrototype: Gesture, operation: Operation, appSymbol: AppSymbol): Gesture = gesturePrototype
        .also {
            it.assignment = it.currentAssignment
                .withOperation(operation)
                .withText(appSymbol.value)
                .withSymbol(appSymbol)
        }
    override val requiresUserInput: Boolean get() { throwUnsupported("flag 'requiresUserInput'") }


    /**
     * This is a yes-no dialog sequence that shows a confirmation screen for reassignment,
     *
     * @param gesture to be used to gather any needed data about this gesture if more details are desired in the confirmation screen.
     * @param context to be used to gather any needed data about this gesture if more details are desired in the confirmation screen.
     * @param confirmationMessage is a Sting message to make sure the user okay with making a
     * permanent change to their keyboard. The message is usually a question in the form of, "Are
     * you sure you want to reassign this gesture to <this-operation>?". A message in this format is considered
     * the default behavior. For this default behavior, please pass an empty String as the confirmationMessage.
     */
    @Composable protected fun ShowNoArgsConfirmationScreen(
        context: Context,
        gesture: Gesture,
        confirmationMessage: String,
    ) {
        val message = confirmationMessage.ifBlank {
            "Are you sure you want to reassign this gesture to \"$menuItemDescription\"?"
        }
        val byokViewModel: BuildYourOwnKeyboardViewModel = viewModel()
        byokViewModel.reassignmentDataStateFlow.collectAsState().value?: run {
            byokViewModel.setReassignmentData(null)
            return
        }

        Button(
            onClick = {
                byokViewModel.setAskForConfirmation(message)
            }
        ) {
            Text("Done")
        }
    }

    override fun invoke() {
        executeOperation(handler)
        cleanup(handler.inputConnection)
    }

    private fun cleanup(inputConnection: GriddleInputConnection) {
        inputConnection.editText.invalidate()
        areTheArgsLoaded = false
    }

    override fun invoke(
        keyboard: Keyboard,
        context: Context,
        gesture: Gesture,
        touchPoints: List<Point>,
        view: View?,
        previousOperation: (() -> Unit)?,
        gestureBUttonPosition: GridPosition
    ) = run {
        /**
         * Vibration is a one-shot deal, so any callers of child classes will need to call
         * `EditorHandler.withVibration(vibrationChoice)` repeatedly if vibration is desired more than once.
         */
        executeOperation(
            KeyboardContext(
                keyboard,
                context,
                gesture,
                touchPoints,
                view,
                previousOperation,
                gestureBUttonPosition,
            )
        )
    }
}