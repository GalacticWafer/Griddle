@file:JvmName("GriddleModifierKeyKt")

package com.galacticware.griddle.domain.model.keyboard

import android.content.Context
import android.util.Log
import android.view.KeyEvent
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.galacticware.griddle.R
import com.galacticware.griddle.domain.model.button.GestureButton
import com.galacticware.griddle.domain.model.button.GestureButtonBuilder
import com.galacticware.griddle.domain.model.collection.ConcurrentStack
import com.galacticware.griddle.domain.model.error.Errors
import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.input.TextSelectionAnchor
import com.galacticware.griddle.domain.model.layer.LayerDefinable
import com.galacticware.griddle.domain.model.layer.LayerDesignMetadata
import com.galacticware.griddle.domain.model.layer.LayerKind
import com.galacticware.griddle.domain.model.layer.LayerRegistry
import com.galacticware.griddle.domain.model.modifier.AppModifierState
import com.galacticware.griddle.domain.model.modifier.ModifierCycleDirection
import com.galacticware.griddle.domain.model.modifier.ModifierKeyKind
import com.galacticware.griddle.domain.model.modifier.ModifierKeyState
import com.galacticware.griddle.domain.model.modifier.ModifierTheme
import com.galacticware.griddle.domain.model.textreplacement.TextReplacement
import com.galacticware.griddle.domain.model.usercontolled.GriddleSetting
import com.galacticware.griddle.domain.model.usercontolled.IncrementalAdjustmentType
import com.galacticware.griddle.domain.model.usercontolled.UserHandedness
import com.galacticware.griddle.domain.model.util.PreferencesHelper
import com.galacticware.griddle.domain.model.screen.NestedAppScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import java.io.IOException
import kotlin.math.roundToInt
import kotlin.reflect.KClass


/**
 * This object represents a keyboard.
 */
@Serializable
open class Keyboard(
    val name: String,
    val layers: Set<LayerDefinable>,
    val keyboardKind: KeyboardKind = KeyboardKind.DEFAULT
) {
    // To find min_scale allowed based on minBoardHeight:
    // height / min_height = scale / min_scale
    // ∴ min_scale = scale * min_height / current_height
    @Transient var context: Context? = null
    var userDefinedScale: Float = 1f//.5f
        set(value) {
            field = value.coerceAtMost(1f).coerceAtLeast(
                // ∴ min_scale = field * value / _currentLayer.minBoardHeight.value
                field * value / _currentLayer.minBoardHeight.value
            )
        }

    // A secondary constructor that provides context manually
    constructor(
        context: Context,
        name: String,
        layers: Set<LayerDefinable>,
        keyboardKind: KeyboardKind = KeyboardKind.DEFAULT
    ) : this(name = name, layers = layers, keyboardKind = keyboardKind) {
        this.context = context
    }

    val model get() = KeyboardModel(layers.map { it.model }, keyboardKind, name)
    init {
        /**
         * No funny business. Make sure the layers are what they should be.
         */
        val containedLayers = layers.map { it.layerKind }
        val missingLayers = mutableListOf<String>()
        for(mandatoryLayerKind in keyboardKind.mandatoryLayerKinds()) {
            if(mandatoryLayerKind !in containedLayers) {
                missingLayers.add(mandatoryLayerKind.name)
            }
        }

        if(missingLayers.size > 0 && keyboardKind != KeyboardKind.SINGLE_BUTTON_DESIGNER_MODE) {
            throw Errors.UNSATISFACTORY_LAYER_SET.send(
                "keyboard of type '${keyboardKind.label}' is missing the following layers",
                *missingLayers.toTypedArray()
            )
        }

        layers.forEach { LayerRegistry.add(it) }
    }

    // todo, when the board is horizontal, we should probably describe the maximum width as less
    //  than the screen width.
    val rotatedColumnCount: Int get() = _currentLayer.keyboardHandedness.pivotColumn

    private var _currentLayer = try {
        layers.first { it.isPrimary }
    } catch (e: NoSuchElementException) {
        try {
            layers.first { it.layerKind == LayerKind.ALPHA }
        } catch (e: NoSuchElementException) {
            throw Errors.MISSING_LAYER_DEFINITION.send()
        }
    }
    val primaryLayer get() = layers.first { it === _currentLayer }

    val defaultTheme: ModifierTheme get() = _currentLayer.defaultModifierTheme

    private var _previousLayer = primaryLayer

    @Transient var buttons = buildButtons(_currentLayer, replacementButtons = null)

    var rowHeight get() = _currentLayer.rowHeight
        set(value) {
            layers.forEach{
                it.rowHeight = value
            }
        }

    var colWidth get() = _currentLayer.colWidth
        set(value) {
            layers.forEach {
                it.colWidth = value
            }
        }

    val minWidth get() = _currentLayer.minColWidth * _currentLayer.colSpan

    val colSpan get() = _currentLayer.colSpan
    val rowSpan get() = _currentLayer.rowSpan
    var width: Dp
        get() = (colWidth * colSpan).dp
        set(value) {
            layers.forEach {
                it.colWidth = (value.value / colSpan).roundToInt()
            }
        }

    var height: Dp
        get() = (rowHeight * rowSpan).dp
        set(value) {
            val currentBoardHeight = height
            layers.forEach {
                val originalAspectRatio = it.originalColWidth.toFloat() / it.originalRowHeight
                it.rowHeight = ((currentBoardHeight / it.rowSpan).value.roundToInt() +
                    if (value < currentBoardHeight)
                        -1
                    else if (value > currentBoardHeight)
                        1
                    else 0
                ).coerceIn(it.minRowHeight, it.maxRowHeight)
            }
        }

    /**
     * The default aspect ratio of the keyboard, defined as the width divided by the height.
     */
    val defaultWidthToHeightAspectRatio = width.value / height.value

    // Todo, cache the displayItems arrays for each layer in a Map to optimize performance.

    private val otherLayers = (layers.plus(_currentLayer))
        .associateBy { it.layerKind }
        .toMutableMap()

    override fun toString(): String = _currentLayer.layerKind.name

    fun switchToPreviousLayer() {
        otherLayers[_currentLayer.layerKind] = _currentLayer
        _currentLayer = _previousLayer
        _previousLayer = primaryLayer
        currentLayer = _currentLayer
    }

    fun switchToLayerKind(layerKind: LayerKind, swapPreviousLayer: Boolean = true) {
        context?.let { ctx ->
            otherLayers[_currentLayer.layerKind] = _currentLayer
            _currentLayer.let {
                PreferencesHelper.saveBoardPositionAndSize(ctx, offsetAndSize, _currentLayer.name)
            }
            if (swapPreviousLayer) _previousLayer = _currentLayer
            val nextKeyboardLayer = otherLayers[layerKind]
            val qualifiedName = nextKeyboardLayer!!.name
                ?: throw Errors.MISSING_LAYER_DEFINITION.send("Layer $layerKind not found")
            offsetAndSize = PreferencesHelper.getBoardPositionAndSize(ctx, qualifiedName)
            _currentLayer = nextKeyboardLayer
            currentLayer = _currentLayer
            nextKeyboardLayer.resizeToFitScreen()
            buttons = buildButtons(nextKeyboardLayer)
        }
    }

    fun buildButtons(
        layer: LayerDefinable =  _currentLayer,
        replacementButtons: Map<Pair<Int, Int>, Pair<GestureButton, List<Pair<ModifierTheme, Gesture>>>>? = null,
    ) = layer.loadButtons(replacementButtons)

    companion object {
        /**
         * Flag used to determine whether the keyboard has temporarily swapped one or more gestures
         * on one or more buttons.
         */
        var isHotSwapped: Boolean = false
        var isWordPredictionEnabled: Boolean = false
        var predictions = listOf<String>()
        val nestedAppScreenStack: ConcurrentStack<NestedAppScreen> = ConcurrentStack()
        var wasLastActionBackspace: Boolean = false
        var didLastActionAutoCapitalize: Boolean = false
        var didLastActionAutoPuctuate: Boolean = false
        var didLastActionAutoCorrect: Boolean = false
        var offsetAndSize = KeyboardOffsetAndSize(0f, 0f, 0f)
            set(value) {
                field = value
                currentLayer?.let { layer ->
                    val identicallySizedLayers = LayerRegistry.layersTheSameSizeAs(layer)
                    identicallySizedLayers
                        .forEach { similarlySizedLayer ->
                            similarlySizedLayer.saveBoardPositionAndSize(value)
                        }
                }
            }

        var isResizingAndMoving: Boolean = false
        var selectionAnchor = TextSelectionAnchor.NONE

         /**
          * If this is not null, then sending textReplacements is blocked due to whatever screen is currently
          * open, as identified by class. Currently only used for the TextReplacementEditorScreen, so it
          * doesn't invoke the textReplacements when you're trying to define them... But could be used for
          * other screens in the future too if sending textReplacements is undesirable for any reason.
          */
        var textReplacementScreenBlocker: KClass<out NestedAppScreen>? = null

        var textReplacements: List<TextReplacement> = emptyList()

        fun loadKeyboard(context: Context): Keyboard? {
            val sharedPreferences = context.getSharedPreferences(R.string.keyboard_prefs.toString(), Context.MODE_PRIVATE)
            val key = context.getString(R.string.user_keyboard)
            val fileName = sharedPreferences.getString(key, null)?: return null
            return SavedKeyboardFileType.loadKeyboard(context, fileName)
                .also { it.context = context }
        }

        private fun metaState(
            modifierKeyState: ModifierKeyState,
            modifierKeyState1: ModifierKeyState,
            modifierKeyState2: ModifierKeyState
        ): Int {
            var metaState = 0
            if (modifierKeyState != ModifierKeyState.OFF) {
                metaState = metaState or KeyEvent.META_SHIFT_ON
            }
            if (modifierKeyState1 != ModifierKeyState.OFF) {
                metaState = metaState or KeyEvent.META_CTRL_ON
            }
            if (modifierKeyState2 != ModifierKeyState.OFF) {
                metaState = metaState or KeyEvent.META_ALT_ON
            }
            return metaState
        }

        /**
         * We always start on the alpha layer.
         */
        var currentLayerKind: LayerKind = LayerKind.ALPHA

        /**
         * Indicates the current page of the clipboard layer.
         */
        var clipboardIndex: Int = 0

        /**
         * Exposes the current layer's modifier keys without having to have a
         * reference to the board
         * Useful for functions that need to operate on the editor and
         * potentially edit the text.
         */
        private var _metaState: AppModifierState = AppModifierState()

        /**
         * The current shift state of the keyboard.
         */
        val shiftState get() = _metaState.shift

        /**
         * The current ctrl state of the keyboard.
         */
        val ctrlState get() = _metaState.ctrl

        /**
         * The current alt state of the keyboard.
         */
        val altState get() = _metaState.alt

        /**
         * The current meta state of the keyboard.
         */
        val currentMetaState get() = AppModifierState(shiftState, ctrlState, altState)

        /**
         * Cycles the modifier key state for the given modifier key kind.
         */
        fun cycleToNextModifierKeyStateFor(
            modifierKeyKind: ModifierKeyKind,
            cycleDirection: ModifierCycleDirection = ModifierCycleDirection.FORWARD) {
            _metaState.cycleToNextModifierKeyStateFor(modifierKeyKind, cycleDirection)
        }

        /**
         * Cancels all non-repeating modifiers.
         */
        fun cancelNonRepeatingModifiers() {
            _metaState.cancelOneShotModifiers()
        }

        fun cancelModifier(modifierKeyKind: ModifierKeyKind) {
            while (modifierKeyStateFor(modifierKeyKind) != ModifierKeyState.OFF) {
                cycleToNextModifierKeyStateFor(modifierKeyKind, ModifierCycleDirection.FORWARD)
            }
        }

        /**
         * Returns the current state of the modifier key for the given [modifierKeyKind].
         */
        fun modifierStateFor(modifierKeyKind: ModifierKeyKind) = when(modifierKeyKind) {
            ModifierKeyKind.SHIFT -> shiftState
            ModifierKeyKind.CONTROL -> ctrlState
            ModifierKeyKind.ALT -> altState
        }

        /**
         * Cycles the modifier key state for the given modifier key kind.
         */
        fun withHandedness(
            columnSpan: Int,
            userHandedness: UserHandedness,
            pivot: Int,
            colStart: Int,
        ): Int {
           return ((if(currentLayer!!.layerKind == LayerKind.UNIFIED_ALPHA_NUMERIC) {
                (if(colStart == pivot) {
                    colStart
                } else {
                    if(userHandedness == UserHandedness.RIGHT) colStart else colStart + (pivot) * if(colStart > pivot) 1 else -1
                })
           } else {
               colStart + if(userHandedness == UserHandedness.LEFT) pivot else 0
           }) + columnSpan) % columnSpan
        }
        var currentLayer: LayerDefinable? = null
        var userHandedness: UserHandedness = UserHandedness.RIGHT

        /**
         * Returns the current state of the modifier key for the given [modifierKeyKind].
         */
        fun modifierKeyStateFor(modifierKeyKind: ModifierKeyKind) = when(modifierKeyKind) {
            ModifierKeyKind.SHIFT -> shiftState
            ModifierKeyKind.CONTROL -> ctrlState
            ModifierKeyKind.ALT -> altState
        }

        fun cycleToOneShotStateFor(modifierKeyKind: ModifierKeyKind) {
            while (modifierKeyStateFor(modifierKeyKind) != ModifierKeyState.ONE_SHOT) {
                cycleToNextModifierKeyStateFor(modifierKeyKind, ModifierCycleDirection.FORWARD)
            }
            didLastActionAutoCapitalize = true
        }

        fun setCurrentLayerFromInstance(keyboard: Keyboard) {
            currentLayer = keyboard._currentLayer
        }

        var layerDesignMetadata: LayerDesignMetadata? = null

        /**
         * @param [layerDefinable] to
         */
        fun setDesignerLayer(layerDefinable: LayerDefinable?) {
            layerDesignMetadata = layerDefinable?.let { LayerDesignMetadata(layerDefinable, currentLayer!!) }
        }

        fun clearScreenStack() {
            while(nestedAppScreenStack.isNotEmpty()) { nestedAppScreenStack.pop() }
            layerDesignMetadata = null
        }
    }

    fun addButton(resizeButton: GestureButtonBuilder, alphaLayer: LayerKind) {
        val layer = otherLayers[alphaLayer]
        if (layer != null) {
            layer.gestureButtonBuilders = layer.gestureButtonBuilders.plus(resizeButton).toMutableSet()
        }
    }

    fun changeIncrementalSetting(context: Context, setting: GriddleSetting, incrementalAdjustmentType: IncrementalAdjustmentType) {
        var currentValue = setting.currentSettingValueOf(context)
        if(setting.isToggleable) {
            println("before: $currentValue")
            setting.setValue(context, if(currentValue == setting.minValue) {setting.maxValue} else {setting.minValue})
            println("after: ${setting.currentSettingValueOf(context)}")
            return
        }
        setting.setValue(
            context, currentValue + (if (incrementalAdjustmentType == IncrementalAdjustmentType.INCREASE) 1 else -1) * setting.stepSize
        )

        when(setting) {
            GriddleSetting.IS_VIBRATION_ENABLED -> {
                /*PreferencesHelper.setUserVibrationChoice(context,
                    (if(currentValue == setting.minValue) VibrationChoice.OFF
                    else VibrationChoice.ON).ordinal)
                PreferencesHelper.setUserVibrationAmplitude(context, currentValue)*/
            }
            GriddleSetting.MINIMUM_DRAG_LENGTH -> {/*we literally just updated this value*/}
            else -> {}
        }
    }

    fun swapHandedness() {
        userHandedness = when(userHandedness) {
            UserHandedness.RIGHT -> UserHandedness.LEFT
            UserHandedness.LEFT -> UserHandedness.RIGHT
        }
    }

    fun saveBoardPositionAndSize(offsetAndSize: KeyboardOffsetAndSize) {
        context?.let { ctx ->
            LayerRegistry.layersTheSameSizeAs(currentLayer()).forEach {
                PreferencesHelper.saveBoardPositionAndSize(
                    ctx,
                    offsetAndSize,
                    it.name
                )
            }
        }?: throw Errors.MISSING_CONTEXT.send("saving the position and size of the board.")
    }

    fun currentLayer(): LayerDefinable = currentLayer?: primaryLayer

    fun designerButtonSave(
        context: Context,
        buttonContainingFirstPoint: GestureButton,
        newGesture: Gesture,
        savedKeyboardFileType: SavedKeyboardFileType = SavedKeyboardFileType.ZIP,
    ) {
        buttonContainingFirstPoint.builder.gestureSet.let { g ->
            g.removeIf { it::class == newGesture::class }
            g.add(newGesture)
        }
        val gestureButtonBuilders = layerDesignMetadata!!.currentlyEditedLayer.gestureButtonBuilders
        gestureButtonBuilders
            .firstOrNull {
                it.gridPosition == buttonContainingFirstPoint.builder.gridPosition
            }
            ?: run {
                currentLayer().gestureButtonBuilders.find {
                    it.gridPosition == buttonContainingFirstPoint.builder.gridPosition
                }!!
            }.update(newGesture)
        saveKeyboardFile(context, savedKeyboardFileType,)
    }

    fun saveKeyboardFile(
        context: Context,
        savedKeyboardFileType: SavedKeyboardFileType,
    ) {
        try {
            savedKeyboardFileType.save(
                context,
                Json.encodeToString(KeyboardModel.serializer(), model),
                name
            )
        } catch (e: IOException) {
            // Handle any IO exceptions that might occur
            Log.e("KeyboardSave", "Error saving keyboard model to file", e)
        }
    }

    fun switchToLayerByName(layerName: String?) {
        val nextLayer = layers.first { it.name == layerName }
        _previousLayer = _currentLayer
        _currentLayer = nextLayer
    }
}