package com.galacticware.griddle.domain.model.operation.implementation.someargs.presskey

import android.content.Context
import android.text.InputType
import android.view.KeyEvent
import androidx.compose.runtime.Composable
import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.gesture.KeyboardContext
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import com.galacticware.griddle.domain.model.keyboard.Keyboard
import com.galacticware.griddle.domain.model.modifier.ModifierKeyState
import com.galacticware.griddle.domain.model.operation.base.Operation
import com.galacticware.griddle.domain.model.operation.base.OperationTag

val PressTab = object : Operation({}) {
    override val name: String by lazy { "Press Enter" }
    override var isBackspace: Boolean = false
    override val appSymbol by lazy { AppSymbol.TAB_RIGHT }
    override val menuItemDescription: String
        get() = "Press Tab."
    override val requiresUserInput: Boolean
        get() = false
    override val tag: OperationTag
        get() = OperationTag.PRESS_TAB
    override val userHelpDescription: String
        get() = menuItemDescription
    override val shouldKeepDuringTurboMode: Boolean
        get() = true

    @Composable
    override fun ShowReassignmentScreen(context: Context, gesture: Gesture)
            = ShowNoArgsConfirmationScreen(context, gesture, "Are you sure you want to change this gesture to TAB?")
    override fun produceNewGesture(gesturePrototype: Gesture): Gesture
            = produceNewGestureWithAppSymbol(gesturePrototype, this, appSymbol)
    override fun executeOperation(keyboardContext: KeyboardContext) {
        val inputType = keyboardContext.inputConnection.editText.inputType
        println("type: ${
            when(inputType) {
                InputType.TYPE_CLASS_DATETIME -> "TYPE_CLASS_DATETIME"
                InputType.TYPE_CLASS_NUMBER -> "TYPE_CLASS_NUMBER"
                InputType.TYPE_DATETIME_VARIATION_DATE -> "TYPE_DATETIME_VARIATION_DATE"
                InputType.TYPE_DATETIME_VARIATION_TIME -> "TYPE_DATETIME_VARIATION_TIME"
                InputType.TYPE_NUMBER_FLAG_DECIMAL -> "TYPE_NUMBER_FLAG_DECIMAL"
                InputType.TYPE_TEXT_VARIATION_PASSWORD -> "TYPE_TEXT_VARIATION_PASSWORD"
                InputType.TYPE_TEXT_VARIATION_PERSON_NAME -> "TYPE_TEXT_VARIATION_PERSON_NAME"
                InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS -> "TYPE_TEXT_VARIATION_POSTAL_ADDRESS"
                InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT -> "TYPE_TEXT_VARIATION_EMAIL_SUBJECT"
                InputType.TYPE_TEXT_VARIATION_FILTER -> "TYPE_TEXT_VARIATION_FILTER"
                InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE -> "TYPE_TEXT_VARIATION_SHORT_MESSAGE"
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD -> "TYPE_TEXT_VARIATION_VISIBLE_PASSWORD"
                InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS -> "TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS"
                InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD -> "TYPE_TEXT_VARIATION_WEB_PASSWORD"
                // all the others
                else -> {
                    "unsupported=${
                        when(inputType){
                            InputType.TYPE_TEXT_FLAG_CAP_SENTENCES -> "TYPE_TEXT_FLAG_CAP_SENTENCES"
                            InputType.TYPE_TEXT_FLAG_AUTO_CORRECT -> "TYPE_TEXT_FLAG_AUTO_CORRECT"
                            InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE -> "TYPE_TEXT_FLAG_AUTO_COMPLETE"
                            InputType.TYPE_TEXT_FLAG_MULTI_LINE -> "TYPE_TEXT_FLAG_MULTI_LINE"
                            InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE -> "TYPE_TEXT_FLAG_IME_MULTI_LINE"
                            InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS -> "TYPE_TEXT_FLAG_NO_SUGGESTIONS"
                            else -> "unknown, $inputType"
                        }                  
                    }"
                }
            }
        }")

        val isShiftPressed = Keyboard.shiftState != ModifierKeyState.NONE
        /*when(inputType) {
            0,
            InputType.TYPE_CLASS_DATETIME, InputType.TYPE_CLASS_NUMBER,
            InputType.TYPE_DATETIME_VARIATION_DATE, InputType.TYPE_DATETIME_VARIATION_TIME,
            InputType.TYPE_NUMBER_FLAG_DECIMAL,
            InputType.TYPE_TEXT_VARIATION_PASSWORD, InputType.TYPE_TEXT_VARIATION_PERSON_NAME,
            InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS,
            InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT, InputType.TYPE_TEXT_VARIATION_FILTER,
            InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE, InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD,
            InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS, InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD ->{
                keyboardContext.inputConnection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN,
                    if(isShiftPressed)
                        KeyEvent.KEYCODE_NAVIGATE_PREVIOUS
                    else
                        KeyEvent.KEYCODE_NAVIGATE_NEXT
                ))
                if(isShiftPressed) {
                    Keyboard.cancelNonRepeatingModifiers()
                }
            }
            else -> {*/
        val currentTimeMillis = System.currentTimeMillis()
        keyboardContext.inputConnection.sendKeyEvent(
            KeyEvent(
                /*downTime*/ currentTimeMillis,
                /*eventTime*/ currentTimeMillis,
                /*action*/ KeyEvent.ACTION_DOWN,
                /*keyCode*/ KeyEvent.KEYCODE_TAB,
                /*repeatCount*/ 0,
                /*metaState*/ if(isShiftPressed) KeyEvent.META_SHIFT_ON else 0
            )
        )
        Keyboard.cancelNonRepeatingModifiers()
            /*}
        }*/
    }
}