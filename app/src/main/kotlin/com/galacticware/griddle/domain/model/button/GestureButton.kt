package com.galacticware.griddle.domain.model.button

import com.galacticware.griddle.domain.model.modifier.ModifierTheme
import com.galacticware.griddle.domain.model.geometry.GridPosition
import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.shared.Direction
import com.galacticware.griddle.domain.model.geometry.GridArea
import com.galacticware.griddle.domain.model.shared.GenericGestureType
import com.galacticware.griddle.domain.model.gesture.GestureType
import com.galacticware.griddle.domain.model.shared.RotationDirection
import com.galacticware.griddle.domain.model.appsymbol.SettingsValue
import com.galacticware.griddle.domain.model.operation.implementation.someargs.presskey.PressKey
import com.galacticware.griddle.domain.model.operation.implementation.noargs.simpleinput.SimpleInput
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

interface ISettingsValueProvider {
    fun provideValue() : String
}

object SettingsValueProviderSerializer : KSerializer<SettingsValueProvider> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("SettingsValueProvider", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: SettingsValueProvider) {
        // Serialize the Color as an integer
        encoder.encodeInt(SettingsValue.entries.first { it.provider == value }.ordinal)
    }

    override fun deserialize(decoder: Decoder): SettingsValueProvider {
        return SettingsValue.entries[decoder.decodeInt()].provider
    }
}

@Serializable(with = SettingsValueProviderSerializer::class)
abstract class SettingsValueProvider(val valueFetcher: (() -> String)): ISettingsValueProvider {
    override fun provideValue(): String {
        return valueFetcher()
    }
}
val settingsDisplay: (() -> String) -> SettingsValueProvider = {
    object: SettingsValueProvider(it) {}
}

/**
 * Twenty possible gestures, like the MessagEase keyboard.
 */
class GestureButton(
    var gridPosition: GridPosition,
    gestureSet: MutableSet<Gesture>,
    var widthRuler: Int,
    var heightRuler: Int,
    var modifierTheme: ModifierTheme,
    var size: Int = 10,
    val settingsValueProvider: SettingsValueProvider? = null,
    val isPeripheral: Boolean = false,
    val builder: GestureButtonBuilder,
) {
    override fun toString(): String {
        return "GridKeyPosition(row=$colStart, rowSpan=$rowSpan, column=$rowStart, columnSpan=$colSpan)"
    }
    val prettyPrintTypeLabel = "Twenty-Gesture Button"
    val rowStart get() = gridPosition.rowStart
    val colStart get() = gridPosition.colStart
    val rowSpan get() = gridPosition.rowSpan
    val colSpan get() = gridPosition.colSpan
    val gridArea get() = GridArea(
        gridPosition,
        modifierTheme
    )

    val width = widthRuler * colSpan
    val height = heightRuler * rowSpan
    val gestures = gestureSet
        .associateBy {
            GestureType.fromInstance(it)
        }.toMutableMap()
    /**
     * GestureButtons have different ways of choosing which concrete Gesture to return for a given
     * GestureType. This method is abstract and must be implemented by subclasses.
     */
    fun keyBindingForType(
        genericGestureType: GenericGestureType,
        rotationDirection: RotationDirection? = null,
        direction: com.galacticware.griddle.domain.model.shared.Direction? = null,
    ): Gesture? = gestures[when (genericGestureType) {
        GenericGestureType.CLICK -> GestureType.CLICK
        GenericGestureType.HOLD -> GestureType.HOLD
        GenericGestureType.CIRCLE -> rotationDirection?.let {
            rotationDirectionToCircleVariantMap[it]
        }
        GenericGestureType.SWIPE -> directionToSwipeVariantMap[direction]?.first
        GenericGestureType.BOOMERANG -> directionToSwipeVariantMap[direction]?.second
    }]

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GestureButton

        if (rowStart != other.rowStart) return false
        if (colStart != other.colStart) return false
        if (rowSpan != other.rowSpan) return false
        return colSpan == other.colSpan
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    fun nullOnTurboMode(
        isTurboModeEnabled: Boolean,
        gesture: Gesture,
    ): Gesture? {
        if(gesture.currentAssignment.operation.let {
                it !is SimpleInput && it !is PressKey
            }) return gesture

        val gestureType = GestureType.fromInstance(gesture)
        val isCenterLegend = gestureType == GestureType.CLICK
        val isIndicatorLegend = gesture.currentAssignment.isIndicator

        if(isTurboModeEnabled && !isCenterLegend && !isIndicatorLegend &&
            gesture.currentText.any { !it.isLetter() }
            && !gesture.currentAssignment.operation.shouldKeepDuringTurboMode) return null

        return gesture
    }

    companion object {
        val directionToSwipeVariantMap = mapOf(
            Direction.NORTH to (GestureType.SWIPE_UP to GestureType.BOOMERANG_UP),
            Direction.SOUTH to (GestureType.SWIPE_DOWN to GestureType.BOOMERANG_DOWN),
            Direction.EAST to (GestureType.SWIPE_RIGHT to GestureType.BOOMERANG_RIGHT),
            Direction.WEST to (GestureType.SWIPE_LEFT to GestureType.BOOMERANG_LEFT),
            Direction.NORTHEAST to (GestureType.SWIPE_UP_RIGHT to GestureType.BOOMERANG_UP_RIGHT),
            Direction.NORTHWEST to (GestureType.SWIPE_UP_LEFT to GestureType.BOOMERANG_UP_LEFT),
            Direction.SOUTHEAST to (GestureType.SWIPE_DOWN_RIGHT to GestureType.BOOMERANG_DOWN_RIGHT),
            Direction.SOUTHWEST to (GestureType.SWIPE_DOWN_LEFT to GestureType.BOOMERANG_DOWN_LEFT),
        )
        val rotationDirectionToCircleVariantMap = mapOf(
            RotationDirection.CLOCKWISE to GestureType.CIRCLE_CLOCKWISE,
            RotationDirection.ANTI_CLOCKWISE to GestureType.CIRCLE_ANTI_CLOCKWISE
        )
    }
}