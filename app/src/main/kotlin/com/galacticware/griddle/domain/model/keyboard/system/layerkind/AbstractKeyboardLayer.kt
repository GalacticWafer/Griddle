package com.galacticware.griddle.domain.model.keyboard.system.layerkind

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import com.galacticware.griddle.domain.model.button.GestureButton
import com.galacticware.griddle.domain.model.button.GestureButtonBuilder
import com.galacticware.griddle.domain.model.button.SettingsValueProvider
import com.galacticware.griddle.domain.model.geometry.AxialParams
import com.galacticware.griddle.domain.model.geometry.CartesianAxis
import com.galacticware.griddle.domain.model.geometry.GridPosition
import com.galacticware.griddle.domain.model.geometry.StartAndSpan
import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.gesture.GestureType
import com.galacticware.griddle.domain.model.keyboard.Keyboard
import com.galacticware.griddle.domain.model.keyboard.KeyboardHandedness
import com.galacticware.griddle.domain.model.keyboard.KeyboardOffsetAndSize
import com.galacticware.griddle.domain.model.keyboard.definition.designs.griddle.english.button.GriddleButtonBuilders
import com.galacticware.griddle.domain.model.language.LanguageTag
import com.galacticware.griddle.domain.model.layer.LayerDefinable
import com.galacticware.griddle.domain.model.layer.LayerKind
import com.galacticware.griddle.domain.model.modifier.ModifierKeyKind
import com.galacticware.griddle.domain.model.modifier.ModifierKeyState
import com.galacticware.griddle.domain.model.modifier.ModifierTheme
import com.galacticware.griddle.domain.model.util.PreferencesHelper
import com.galacticware.griddle.domain.model.keyboard.definition.theme.DEFAULT_PRIMARY_THEME
import com.galacticware.griddle.domain.model.keyboard.definition.theme.DEFAULT_SECONDARY_THEME
import com.galacticware.griddle.domain.model.keyboard.definition.theme.DEFAULT_SIZE
import com.galacticware.griddle.domain.model.prototyping.phonemicboard.geneticalgorithm.CrossReferencingMap
import kotlinx.serialization.Transient
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.roundToInt

abstract class AbstractKeyboardLayer(
    @Transient val context: Context,
    override var gestureButtonBuilders: MutableSet<GestureButtonBuilder>,
    var borderColor: Color = Color.Transparent,
    var backgroundColor: Color = Color.Transparent,
    var textColor: Color = DEFAULT_PRIMARY_THEME.primaryTextColor,
    var fontSize: TextUnit = DEFAULT_PRIMARY_THEME.fontSize,
    override val keyboardHandedness: KeyboardHandedness,
    private val defaultButtonSize: IntSize,
    override var secondaryModifierTheme: ModifierTheme = DEFAULT_SECONDARY_THEME,
    override val isPrimary: Boolean,
    override val name: String,
    override val layerKind: LayerKind,
    override val languageTag: LanguageTag?,
): LayerDefinable {

    override val model: LayerModel get() = LayerModel(
        gestureButtonBuilders.associate {
            it.gridPosition.rectangleLocation.toString() to it.model
        },
        borderColor,
        backgroundColor,
        textColor,
        fontSize,
        keyboardHandedness,
        defaultButtonSize,
        secondaryModifierTheme,
        isPrimary,
        name,
        layerKind,
        languageTag,
    )

    override var offsetX = 0f

    /**
     * Define the rowShift as the integer needed for the top-most row to be row 0.
     */
    private val rowShift = gestureButtonBuilders.minOf { f -> f.gridPosition.rowStart }

    /**
     * Define the colShift as the integer needed for the left-most column to be column 0.
     */
    private val colShift = gestureButtonBuilders.minOf { f -> f.gridPosition.colStart }

    private var gestureButtons = run {
        gestureButtonBuilders.map {
            GestureButton(
                gridPosition = GridPosition(
                    rowParams = AxialParams(
                        CartesianAxis.Y,
                        StartAndSpan(it.gridPosition.rowStart + rowShift, it.gridPosition.rowSpan),
                    ),
                    colParams = AxialParams(
                        CartesianAxis.X,
                        StartAndSpan(it.gridPosition.colStart + colShift, it.gridPosition.colSpan),
                    ),
                ),
                gestureSet = it.gestureSet,
                widthRuler = it.size.width,
                heightRuler = it.size.height,
                modifierTheme = it.modifierTheme,
                settingsValueProvider = null,
                builder = it
            )
        }
            .associateWith { it.gestures }
    }


    override val rowSpan: Int get() = gestureButtons.keys.maxOf { it.rowStart + it.rowSpan } - rowShift
    override val colSpan: Int get() = gestureButtons.keys.maxOf { it.colStart + it.colSpan } - colShift

    override fun resizeToFitScreen() {
        val density = context.resources.displayMetrics.density
        val screenWidth = context.resources.displayMetrics.widthPixels / density
        if(screenWidth <= this.colWidth * this.colSpan) {
            val possibleRowHeight = (this.colWidth / aspectRatio).roundToInt().coerceAtMost(maxRowHeight)
            this.colWidth = (screenWidth / this.colSpan).roundToInt().coerceAtLeast(minColWidth)
            val desiredRowHeight = (screenWidth / aspectRatio).roundToInt()
            this.rowHeight = desiredRowHeight.coerceAtMost(possibleRowHeight).coerceAtLeast(minRowHeight)
        }

        rowHeight = rowHeight.coerceAtLeast(minRowHeight).coerceAtMost(maxRowHeight)
        this.colWidth = (this.rowHeight * aspectRatio).roundToInt().coerceAtMost(maxColWidth)
        saveBoardPositionAndSize(KeyboardOffsetAndSize(offsetX, rowHeight.toFloat(), colWidth.toFloat()))
    }

    override fun saveBoardPositionAndSize(
        value: KeyboardOffsetAndSize
    ) {
        PreferencesHelper.saveBoardPositionAndSize(context, value, this.name)
    }

    override val maxColWidth: Int
        get() = run {
            (context.resources.displayMetrics.let {
                it.widthPixels / it.density
            } / colSpan).toInt()
        }
    override val minColWidth: Int
        get() = (maxColWidth / 2).coerceAtLeast(35)

    override val maxRowHeight: Int
        get() = run {
            (context.resources.displayMetrics.let {
                it.heightPixels * .45 / it.density
            } / rowSpan).toInt()
        }
    override val minRowHeight: Int
        get() = maxRowHeight / 2
    override val originalColWidth: Int get() = defaultButtonSize.width

    override val originalRowHeight: Int get() = defaultButtonSize.height

    private val aspectRatio get() = this.originalColWidth.toDouble() / this.originalRowHeight

    override fun toString(): String = layerKind.name

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractKeyboardLayer) return false

        return layerKind == other.layerKind
    }

    override var defaultModifierTheme: ModifierTheme
        get() = DEFAULT_PRIMARY_THEME
        set(value) {}

    override fun loadButtons(
        replacementButtons: Map<Pair<Int, Int>, Pair<GestureButton, List<Pair<ModifierTheme, Gesture>>>>?
    ) = run {
        if(replacementButtons != null) {
            gestureButtons = replacementButtons.values.associate {
                it.first to it.second.associate { (_, gesture) ->
                    GestureType.fromInstance(gesture) to gesture
                }.toMutableMap()
            }
        }
        generateThemeMap(gestureButtons)
    }

    val additiveHotSwapKeys = setOf(GriddleButtonBuilders.repeat)
    val removalHotSwapKeys = setOf<GestureButtonBuilder>()
    private fun generateThemeMap(
        griddleKeyboardButtons1: Map<GestureButton, Map<GestureType, Gesture>>,
    ) = (griddleKeyboardButtons1.let {_gkb ->
        val gkb = _gkb.toMutableMap()
        val (additions, removals) = if(Keyboard.isHotSwapped){
            additiveHotSwapKeys to removalHotSwapKeys
        } else removalHotSwapKeys to additiveHotSwapKeys
        gkb.entries.removeIf { it.key.builder in removals }
        additions.forEach { builder ->
            val button = builder()
            gkb[button] = button.gestures
        }
        gkb
    }).entries.associate { (gestureButton, gestures) ->
        (gestureButton.rowStart + rowShift to gestureButton.colStart + colShift) to (
                gestureButton to gestures
                    .filter { (_, gesture) -> gesture.isDisplayable }
                    .map { (_, gesture) ->
                        val themes =  gesture.currentAssignment.modifierThemeSet
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

                                themes.none
                            else
                                it).text ?: "") to gesture
                        }
                    }
                )
    }

    override fun hashCode(): Int {
        var result = layerKind.hashCode()
        result = 31 * result + rowSpan
        result = 31 * result + rowShift
        result = 31 * result + colSpan
        result = 31 * result + colShift
        result = 31 * result + originalColWidth.hashCode()
        result = 31 * result + originalColWidth.hashCode()
        result = 31 * result + gestureButtons.hashCode()
        return result
    }

    override var rowHeight = defaultButtonSize.height
    override var colWidth = defaultButtonSize.width


    init {
        offsetX = try {
            PreferencesHelper.getBoardPositionAndSize(context, this::class.qualifiedName!!).offsetX
        } catch (e: NullPointerException) {
            0f
        }
    }
    companion object {

//        fun fromJson(context: Context, json: JsonObject) = object : AbstractKeyboardLayer(
//            context = context,
//            gestureButtonBuilders = json.get("gestureButtonBuilders").asJsonArray.map { builder ->
//                GestureButtonBuilder.fromJson(builder.asJsonObject)
//            }.toMutableSet(),
//            borderColor = Color(json.get("borderColor").asInt),
//            backgroundColor = Color(json.get("backgroundColor").asInt),
//            textColor = Color(json.get("textColor").asInt),
//            fontSize =  json.get("fontSize").asFloat.sp,
//            keyboardHandedness = Gson().fromJson(json.get("keyboardHandedness").asString, KeyboardHandedness::class.java),
//            defaultButtonSize = Gson().fromJson(json.get("defaultButtonSize").asString, IntSize::class.java),
//            secondaryModifierTheme = Gson().fromJson(json.get("secondaryModifierTheme"),ModifierTheme::class.java),
//            isPrimary = json.get("isPrimary").asBoolean,
//            name = json.get("name").asString,
//            layerKind = LayerKind.valueOf(json.get("layerKind").asString),
//            languageTag = json.get("languageTag")?.asString?.let { LanguageTag.valueOf(it) },
//        ){}
        private val penaltyMap = mapOf (
            GestureType.CLICK to 1,
            GestureType.SWIPE_DOWN_LEFT to 3,
            GestureType.SWIPE_DOWN_RIGHT to 3,
            GestureType.SWIPE_UP_LEFT to 3,
            GestureType.SWIPE_UP_RIGHT to 3,
            GestureType.SWIPE_LEFT to 3,
            GestureType.SWIPE_RIGHT to 3,
            GestureType.SWIPE_UP to 3,
            GestureType.SWIPE_DOWN to 3,
            GestureType.BOOMERANG_DOWN_LEFT to 6,
            GestureType.BOOMERANG_DOWN_RIGHT to 6,
            GestureType.BOOMERANG_UP_LEFT to 6,
            GestureType.BOOMERANG_UP_RIGHT to 6,
            GestureType.BOOMERANG_LEFT to 6,
            GestureType.BOOMERANG_RIGHT to 6,
            GestureType.BOOMERANG_UP to 6,
            GestureType.BOOMERANG_DOWN to 6,
            GestureType.CIRCLE_CLOCKWISE to 8,
            GestureType.CIRCLE_ANTI_CLOCKWISE to 8,
            GestureType.HOLD to 15,
        )

        fun buttonBuilder(
            rowStart: Int,
            colStart: Int,
            rowSpan: Int,
            colSpan: Int,
            gestureSet: MutableSet<Gesture> = mutableSetOf(),
            widthRuler: Int = DEFAULT_SIZE.width,
            heightRuler: Int = DEFAULT_SIZE.height,
            theme: ModifierTheme = DEFAULT_PRIMARY_THEME,
            settingsValueProvider: SettingsValueProvider? = null,
            isPeripheral: Boolean = false,
        ) = GestureButtonBuilder(
            gridPosition = GridPosition(
                rowParams = AxialParams(CartesianAxis.Y, StartAndSpan(rowStart, rowSpan),),
                colParams = AxialParams(CartesianAxis.X, StartAndSpan(colStart, colSpan),),
            ),
            gestureSet = gestureSet,
            modifierTheme = theme,
            settingsValueProvider = settingsValueProvider,
            isPeripheral = isPeripheral,
            size = IntSize(widthRuler, heightRuler), // todo, this is probably fucked up
        )

        private var totalCharsCounted = 0


        fun score(referencingMap: CrossReferencingMap, text: String): Double
                = calculateScore(text.substring(0, min(text.length, 1000)), referencingMap)
        private val applySize = { start: Int, span: Int -> 100.0 * (start + (span / 2.0)) }

        private fun calculateScore(
            text: String,
            boxes: CrossReferencingMap
        ): Double {
            var previous = GridPosition.originUnit
                .withPosition(1, 1)
                .let { it to GestureType.CLICK }
            val score = text.toCharArray().withIndex().sumOf { (_, char) ->
                totalCharsCounted++
                val symbol = char.toString()
                boxes.stringToPosition(symbol)?.let { position ->
                    val currentButtonCenter = position.let { p ->
                        p.size
                        applySize(p.rowStart, p.rowSpan) to applySize(p.colStart, p.colSpan)
                    }

                    val mutableMap = boxes[position]!!
                    val gestureType = mutableMap[symbol]!!
                    val gestureTypePenalty = penaltyMap[gestureType]!! + hypot(
                        currentButtonCenter.first - previous.first.rowStart,
                        currentButtonCenter.second - previous.first.colStart
                    )
                    previous = position to gestureType
                    gestureTypePenalty
                } ?: 0.0
            }
            return score
        }
    }
}