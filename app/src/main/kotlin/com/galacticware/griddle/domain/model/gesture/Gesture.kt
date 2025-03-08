package com.galacticware.griddle.domain.model.gesture

import android.content.Context
import android.view.KeyEvent
import android.view.View
import androidx.compose.ui.graphics.Color
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import com.galacticware.griddle.domain.model.shared.Direction
import com.galacticware.griddle.domain.model.geometry.GridPosition
import com.galacticware.griddle.domain.model.shared.Point
import com.galacticware.griddle.domain.model.shared.RotationDirection
import com.galacticware.griddle.domain.model.keyboard.Keyboard
import com.galacticware.griddle.domain.model.keyboard.definition.theme.DEFAULT_PRIMARY_THEME
import com.galacticware.griddle.domain.model.layer.LayerKind
import com.galacticware.griddle.domain.model.modifier.ModifierAction
import com.galacticware.griddle.domain.model.modifier.ModifierKeyKind
import com.galacticware.griddle.domain.model.modifier.ModifierKeyState
import com.galacticware.griddle.domain.model.modifier.ModifierTheme
import com.galacticware.griddle.domain.model.modifier.ModifierThemeSet
import com.galacticware.griddle.domain.model.operation.base.Operation
import com.galacticware.griddle.domain.model.operation.base.OperationArgs
import com.galacticware.griddle.domain.model.operation.base.ParameterizedOperation
import com.galacticware.griddle.domain.model.operation.base.SavedExecution
import com.galacticware.griddle.domain.model.operation.implementation.noargs.repeat.Repeat
import com.galacticware.griddle.domain.model.operation.implementation.noargs.simpleinput.SimpleInput
import com.galacticware.griddle.domain.model.operation.implementation.someargs.changemodifier.base.ChangeModifier
import com.galacticware.griddle.domain.model.operation.implementation.someargs.changemodifier.base.ChangeModifierArgs
import com.galacticware.griddle.domain.model.operation.implementation.someargs.changeusersettings.ChangeUserSetting
import com.galacticware.griddle.domain.model.operation.implementation.someargs.changeusersettings.ChangeUserSettingArgs
import com.galacticware.griddle.domain.model.operation.implementation.someargs.presskey.PressKey
import com.galacticware.griddle.domain.model.operation.implementation.someargs.presskey.PressKeyArgs
import com.galacticware.griddle.domain.model.operation.implementation.someargs.remappedsymbollookup.RemappedSymbolLookup
import com.galacticware.griddle.domain.model.operation.implementation.someargs.remappedsymbollookup.RemappedSymbolLookupArgs
import com.galacticware.griddle.domain.model.operation.implementation.someargs.switchlayer.SwitchLayer
import com.galacticware.griddle.domain.model.operation.implementation.someargs.switchlayer.SwitchLayerArgs
import com.galacticware.griddle.domain.model.operation.implementation.someargs.switchscreens.SwitchScreenArgs
import com.galacticware.griddle.domain.model.operation.implementation.someargs.switchscreens.SwitchScreens
import com.galacticware.griddle.domain.model.usercontolled.GriddleSetting
import com.galacticware.griddle.domain.model.usercontolled.IncrementalAdjustmentType
import com.galacticware.griddle.domain.model.util.caseSensitive
import com.galacticware.griddle.domain.model.util.triple
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.util.Locale

abstract class Gesture(
     private val primaryAssignment: GestureAssignment,
     swapGesture: Gesture? = null,
) {
    fun copy(
        primaryAssignment: GestureAssignment? = null,
        swapGesture: Gesture? = null,
    ) = run {
        primaryAssignment?.let {
            GestureType.fromInstance(this).newInstance(
                it.operation,
                modifierThemeSet = it.modifierThemeSet,
                symbol = it.appSymbol,
                isIndicator = it.isIndicator,
                overrideMetaState = it.overrideMetaState,
                respectShift = it.respectShift,
                isPeripheral = it.isPeripheral,
                keycode = it.keycode,
                argsJson = it.argsJson,
            )
        }?: this
    }
    var assignment = primaryAssignment
        get() = if(Keyboard.isHotSwapped)
            swapAssignment?: primaryAssignment
        else
            primaryAssignment
    var swapAssignment: GestureAssignment? = swapGesture?.assignment
    
    val currentAssignment get() = assignment
    val prettyPrinted: String get() = "${this::class
        .simpleName!!
        .let { s ->
            "([A-Z][a-z]+)"
                .toRegex().find(s)?.groups
                ?.mapNotNull { str -> str?.value?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() } }
                ?.joinToString(" ") { it }
                ?: s
            s
        }
    }:" +
    "\n${assignment.operation.menuItemDescription}" +
    "\n${assignment.operation.userHelpDescription}"

    var wasValueProvided: Boolean = false
    private val _isDisplayable: Boolean = assignment.isDisplayable

    /**
     * The function to run when this gesture is performed.
     */
    val editorOperation
        get() = assignment.operation

    /**
     * Definition of the amount of area this gesture visually consumes within the key.
     */
    val gridRectangleDefinition
        get() = assignment.gridArea

    /**
     * A [Gesture] is displayable only if it is a swipe or click
     */
    val isDisplayable
        get() = _isDisplayable
                && (assignment.noneTheme.text?.isNotBlank() == true
                || ( assignment.operation is ChangeModifier && (assignment.operation as ChangeModifier)
            .provideArgs(assignment.argsJson!!).modifierAction == ModifierAction.RELEASE))
                && (this is Swipe || this is Click)

    /**
     * Retrieves the text to be displayed based on modifier state.
     */
    val currentText: String
        get() = assignment.currentText

    /**
     * Retrieves the keycode to be sent in a [KeyEvent].
     */
    val currentKeycode get() =  currentTheme().keyCode

    /**
     * Retrieves the mask state to ge mask to be sent in a [KeyEvent].
     */
    val oneShotMetaState: Int
        get() {
            var metaState = 0
            if (assignment.shiftState != null) {
                metaState = metaState or KeyEvent.META_SHIFT_ON
            }
            if (assignment.ctrlState != null) {
                metaState = metaState or KeyEvent.META_CTRL_ON
            }
            if (assignment.altState != null) {
                metaState = metaState or KeyEvent.META_ALT_ON
            }
            return metaState
        }

    val backgroundColor get() = currentTheme().primaryBackgroundColor
    val borderColor get() = currentTheme().primaryBorderColor
    val textColor get() = currentTheme().primaryTextColor

    fun currentTheme(): ModifierTheme {
        val modifierKeyState = when (assignment.modifierKeyKind) {
            ModifierKeyKind.SHIFT -> Keyboard.shiftState
            ModifierKeyKind.CONTROL -> Keyboard.ctrlState
            ModifierKeyKind.ALT -> Keyboard.altState
        }
        return when (modifierKeyState) {
            ModifierKeyState.NONE -> assignment.noneTheme
            ModifierKeyState.ONCE -> assignment.onceTheme
            ModifierKeyState.REPEAT -> assignment.repeatTheme
        }
    }

    fun perform(
        keyboardContext: KeyboardContext,
        context: Context,
        savedExecution: SavedExecution
    ) = perform(
        keyboardContext.keyboard,
        context,
        keyboardContext.touchPoints,
        keyboardContext.view,
        savedExecution,
        keyboardContext.gestureButtonPosition,
    )

    fun perform(
        keyboard: Keyboard,
        context: Context,
        touchPoints: List<Point>,
        view: View?,
        previousOperation: SavedExecution,
        gestureButtonPosition: GridPosition,
    ): SavedExecution = (
        if(assignment.operation is Repeat)
            previousOperation
        else
            SavedExecution(assignment.operation) {
                assignment.operation.also {
                    it.loadKeyboardContext(
                        KeyboardContext(
                            keyboard,
                            context,
                            this,
                            touchPoints,
                            view,
                            previousOperation,
                            gestureButtonPosition,
                        )
                    )
                    it.invoke()
                }
            }
        ).apply {
            invoke()
        }

    override fun toString(): String {
        return "${this::class.simpleName}," +
                "${editorOperation.name}," +
                "${gridRectangleDefinition}," +
                if (currentText.isEmpty()) "text is empty" else "text=\"${currentText}\""
    }

    val icon get() = assignment.appSymbol?.icon

    val model: GestureModel get() = GestureModel(
        assignment.model,
        swapAssignment?.model?: null,
    )

    override fun hashCode(): Int = GestureType.fromInstance(this).hashCode()
    fun withArgsJson(args: OperationArgs): Gesture = GestureType.fromInstance(this)
        .newInstance(
            operation = args.opInstance(),
            modifiers = assignment.modifiers,
            modifierThemeSet = assignment.modifierThemeSet,
            symbol = assignment.appSymbol,
            isIndicator = assignment.isIndicator,
            overrideMetaState = assignment.overrideMetaState,
            respectShift = assignment.respectShift,
            isPeripheral = assignment.isPeripheral,
            keycode = assignment.keycode,
            argsJson = args.toJson()
        )

    companion object {
        val DUMMY_CLICK = Click(GestureAssignment.EMPTY_ASSIGNMENT)
        /**
         * Create a gesture with the provided arguments.
         */
        fun create(
            gestureType: GestureType,
            operation: Operation,
            modifiers: Set<ModifierKeyKind> = setOf(),
            modifierThemeSet: ModifierThemeSet,
            appSymbol: AppSymbol?,
            isIndicator: Boolean = false,
            overrideMetaState: Boolean = false,
            respectShift: Boolean = true,
            isPeripheral: Boolean = false,
            keycode: Int? = null,
            args: String?,
        ) = gestureType.newInstance(operation, modifiers, modifierThemeSet, appSymbol,  isIndicator,
            overrideMetaState, respectShift, isPeripheral, keycode, args)

        fun switchLayer(
            gestureType: GestureType,
            layerKind: LayerKind
        ) = create(
            gestureType,
            SwitchLayer,
            appSymbol = layerKind.symbol,
            layerKind = layerKind
        )

        fun switchScreens(
            gestureType: GestureType,
            switchScreenArgs: SwitchScreenArgs
        ) = create(gestureType, SwitchScreens,
            switchScreenArgs = switchScreenArgs,
            appSymbol = switchScreenArgs.userSwitchToScreen.appSymbol
        )

        fun remappedSymbolLookup(
            gestureType: GestureType,
            appSymbol: AppSymbol,
        ) = create(gestureType, RemappedSymbolLookup,
            appSymbol = appSymbol,
            remappedSymbolLookupArgs = RemappedSymbolLookupArgs(appSymbol),
        )

        fun changeUserSetting(
            gestureType: GestureType,
            changeUserSettingArgs: ChangeUserSettingArgs,
        ) = create(gestureType, ChangeUserSetting,
            griddleSetting = changeUserSettingArgs.griddleSetting,
            adjustmentType = changeUserSettingArgs.incrementalAdjustmentType
        )

        fun pressKey(
            gestureType: GestureType,
            keycode: Int,
            modifiers: Set<ModifierKeyKind> = setOf(),
            respectShift: Boolean = true,
            appSymbol: AppSymbol? = null,
            label: String? = null,
        ) = create(gestureType, PressKey,
            appSymbol = appSymbol,
            label = label,
            threeStrings = triple(label?: appSymbol?.value?:""),
            modifiers = modifiers,
            respectShift = respectShift,
            keycode = keycode
        )

        fun simpleInput(
            gestureType: GestureType,
            label: String,
        ) = create(gestureType, SimpleInput,
            label = label,
            threeStrings = triple(label)
        )

        fun changeModifier(
            gestureType: GestureType,
            changeModifierArgs: ChangeModifierArgs,
        ) = create(gestureType, ChangeModifier,
            appSymbol = changeModifierArgs.appSymbol,
            modifierThemeSet = changeModifierArgs.modifierKeyKind.theme(changeModifierArgs.modifierAction),
            isIndicator = true,
            handleModifierArgs = changeModifierArgs,
        )

        /**
         * Create a gesture with the provided arguments.
         */
        fun create(
            gestureType: GestureType,
            operation: Operation,
            appSymbol: AppSymbol? = null,
            label: String? = appSymbol?.value,
            threeStrings: Triple<String, String, String> = caseSensitive(
                label ?: appSymbol?.value ?: ""
            ),
            foregroundColor: Color? = null,
            backgroundColor: Color? = null,
            borderColor: Color? = null,
            modifierTheme: ModifierTheme = ModifierTheme(
                primaryBorderColor = borderColor ?: Color.Transparent,
                primaryBackgroundColor = backgroundColor ?: Color.Transparent,
                primaryTextColor = foregroundColor ?: DEFAULT_PRIMARY_THEME.primaryTextColor,
                text = threeStrings.first,
            ),
            modifiers: Set<ModifierKeyKind> = setOf(),
            modifierThemeSet: ModifierThemeSet = ModifierThemeSet(
                ModifierKeyKind.SHIFT,
                modifierTheme,
                modifierTheme,
                modifierTheme
            )
                .withTextTriple(threeStrings),
            isIndicator: Boolean = false,
            respectShift: Boolean = true,
            isPeripheral: Boolean = false,
            keycode: Int? = null,
            griddleSetting: GriddleSetting? = null,
            adjustmentType: IncrementalAdjustmentType? = null,
            layerKind: LayerKind? = null,
            layerName: String? = null,
            handleModifierArgs: ChangeModifierArgs? = null,
            remappedSymbolLookupArgs: RemappedSymbolLookupArgs? = null,
            switchScreenArgs: SwitchScreenArgs? = null,
        ) = create(
            gestureType,
            operation,
            modifiers = modifiers,
            modifierThemeSet = modifierThemeSet.let{
                if(layerKind != null)
                    it.withTextTriple(layerKind.symbol.value)
                else it
            },
            appSymbol = appSymbol?: layerKind?.symbol,
            isIndicator = isIndicator,
            respectShift = respectShift,
            isPeripheral = isPeripheral,
            keycode = keycode,
            args = when(operation) {
                is ParameterizedOperation<*> -> encodeOperationArgs(
                    operation,
                    keycode,
                    respectShift,
                    modifiers.toTypedArray(),
                    layerName,
                    griddleSetting,
                    adjustmentType,
                    layerKind,
                    handleModifierArgs,
                    remappedSymbolLookupArgs,
                    switchScreenArgs,
                )
                else -> null
            }
        )

        @OptIn(InternalSerializationApi::class)
        fun encodeOperationArgs(
            operation: Operation,
            keycode: Int?,
            respectShift: Boolean,
            modifiers: Array<out ModifierKeyKind>,
            layerName: String?,
            griddleSetting: GriddleSetting?,
            adjustmentType: IncrementalAdjustmentType?,
            layerKind: LayerKind?,
            handleModifierArgs: ChangeModifierArgs?,
            remappedSymbolLookupArgs: RemappedSymbolLookupArgs?,
            switchScreenArgs: SwitchScreenArgs?,
        ) = when(operation) {
            PressKey -> Json.encodeToString(
                PressKeyArgs::class.serializer(), PressKeyArgs(
                    keycode!!,
                    respectShift,
                    overrideMetaState = false,
                    modifierKeys = modifiers
                )
            )

            SwitchLayer -> Json.encodeToString(
                SwitchLayerArgs::class.serializer(),
                SwitchLayerArgs(layerName, layerKind)
            )

            ChangeUserSetting -> Json.encodeToString(
                ChangeUserSettingArgs::class.serializer(),
                ChangeUserSettingArgs(griddleSetting!!, adjustmentType!!)
            )

            SwitchScreens -> Json.encodeToString(
                SwitchScreenArgs::class.serializer(),
                switchScreenArgs!!
            )

            ChangeModifier -> Json.encodeToString(
                ChangeModifierArgs::class.serializer(),
                handleModifierArgs!!
            )

            RemappedSymbolLookup -> Json.encodeToString(
                RemappedSymbolLookupArgs::class.serializer(),
                remappedSymbolLookupArgs!!
            )
            else -> throw UnsupportedOperationException()
        }
    }
}

/**
 * A Simple click gesture
 */
class Click(gestureAssignment: GestureAssignment) : Gesture(gestureAssignment)

/**
 * The hold gesture
 */
class Hold(gestureAssignment: GestureAssignment) : Gesture(gestureAssignment)

/**
 * The parent class for swipe and boomerang gestures.
 */
abstract class Drag(val direction: Direction, gestureAssignment: GestureAssignment) : Gesture(gestureAssignment)

/**
 * The parent class for swipe gestures, in 8 directions.
 */
abstract class Swipe(
    gestureAssignment: GestureAssignment,
    direction: Direction,
) : Drag(direction, gestureAssignment)

class SwipeNorth(gestureAssignment: GestureAssignment) : Swipe(gestureAssignment, Direction.NORTH)
class SwipeSouth(gestureAssignment: GestureAssignment) : Swipe(gestureAssignment, Direction.SOUTH)
class SwipeEast(gestureAssignment: GestureAssignment) : Swipe(gestureAssignment, Direction.EAST)
class SwipeWest(gestureAssignment: GestureAssignment) : Swipe(gestureAssignment, Direction.WEST)
class SwipeNorthEast(gestureAssignment: GestureAssignment) : Swipe(gestureAssignment, Direction.NORTHEAST)
class SwipeSouthEast(gestureAssignment: GestureAssignment) : Swipe(gestureAssignment, Direction.SOUTHEAST)
class SwipeNorthWest(gestureAssignment: GestureAssignment) : Swipe(gestureAssignment, Direction.NORTHWEST)
class SwipeSouthWest(gestureAssignment: GestureAssignment) : Swipe(gestureAssignment, Direction.SOUTHWEST)

/**
 * The parent class for boomerang gestures, in 8 directions.
 */
abstract class Boomerang(direction: Direction, gestureAssignment: GestureAssignment) : Drag(direction, gestureAssignment)
class BoomerangNorth(gestureAssignment: GestureAssignment) : Boomerang(Direction.NORTHEAST, gestureAssignment)
class BoomerangSouth(gestureAssignment: GestureAssignment) : Boomerang(Direction.SOUTHWEST, gestureAssignment)
class BoomerangEast(gestureAssignment: GestureAssignment) : Boomerang(Direction.NORTHWEST, gestureAssignment)
class BoomerangWest(gestureAssignment: GestureAssignment) : Boomerang(Direction.SOUTHEAST, gestureAssignment)
class BoomerangNorthEast(gestureAssignment: GestureAssignment) : Boomerang(Direction.NORTH, gestureAssignment)
class BoomerangSouthEast(gestureAssignment: GestureAssignment) : Boomerang(Direction.WEST, gestureAssignment)
class BoomerangNorthWest(gestureAssignment: GestureAssignment) : Boomerang(Direction.EAST, gestureAssignment)
class BoomerangSouthWest(gestureAssignment: GestureAssignment) : Boomerang(Direction.SOUTH, gestureAssignment)

/**
 * The parent class for circle gestures.
 */
abstract class Circle(rototionDirection: RotationDirection, gestureAssignment: GestureAssignment) : Gesture(gestureAssignment) {
    private val rotationPrefix = if (rototionDirection == RotationDirection.ANTI_CLOCKWISE)
        "counter-"
    else
        ""
}
class CircleClockwise(gestureAssignment: GestureAssignment) : Circle(RotationDirection.CLOCKWISE, gestureAssignment)
class CircleCounterClockwise(gestureAssignment: GestureAssignment) : Circle(RotationDirection.ANTI_CLOCKWISE, gestureAssignment)