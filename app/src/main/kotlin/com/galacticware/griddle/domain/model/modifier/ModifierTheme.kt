package com.galacticware.griddle.domain.model.modifier

import android.graphics.Paint
import android.graphics.Rect
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.galacticware.griddle.domain.model.geometry.GridArea
import com.galacticware.griddle.domain.model.geometry.RectangleLocation
import com.galacticware.griddle.domain.model.gesture.GestureType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * A [ModifierTheme] defines the way a gesture's displayable text object will be colorized,
 * as well as either [text] to paste directly into the editor, or a [keyCode] to send.
 */
object ColorSerializer : KSerializer<Color> {

    // Define the descriptor for Color (it will be a string in the format "#RRGGBBAA")
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Color", PrimitiveKind.STRING)

    // Serialize: Convert Color to its hexadecimal string representation
    override fun serialize(encoder: Encoder, value: Color) {
        val hexString = String.format("#%08X", value.toArgb())  // Convert Color to ARGB hex
        encoder.encodeString(hexString)
    }

    // Deserialize: Convert a hexadecimal string back to a Color
    override fun deserialize(decoder: Decoder): Color {
        val hexString = decoder.decodeString()
        val regex = """#([0-9A-Fa-f]{8})""".toRegex()  // Match 8 characters for ARGB format
        val matchResult = regex.matchEntire(hexString)
            ?: throw SerializationException("Invalid Color format: $hexString")

        val (argbHex) = matchResult.destructured
        val colorInt = argbHex.toLong(16).toInt()  // Convert hex to integer

        return Color(colorInt)  // Return the Color instance
    }
}
object TextUnitSerializer : KSerializer<TextUnit> {

    // Define the descriptor for TextUnit (it will be a String in the format "value unit")
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("TextUnit", PrimitiveKind.STRING)

    // Serialize: Convert TextUnit to a string with its value and unit
    override fun serialize(encoder: Encoder, value: TextUnit) {
        val valueInPixels = value.value  // Get the raw value (without unit)
        encoder.encodeString("${valueInPixels}sp")
    }

    // Deserialize: Convert a string back to TextUnit
    override fun deserialize(decoder: Decoder): TextUnit {
        val serialized = decoder.decodeString()
        val regex = """([0-9.-]+)([a-zA-Z]+)""".toRegex()
        val matchResult = regex.matchEntire(serialized)
            ?: throw SerializationException("Invalid TextUnit format: $serialized")

        val (valueStr, unitStr) = matchResult.destructured
        val value = valueStr.toFloat()

        // Based on the unit string, we convert the value to the correct TextUnit
        return when (unitStr) {
            "sp" -> value.sp
            else -> throw SerializationException("Unsupported unit: $unitStr")
        }
    }
}
@Serializable
data class ModifierTheme(
    @Serializable(with = ColorSerializer::class) var primaryTextColor: Color,
    @Serializable(with = ColorSerializer::class) var primaryBackgroundColor: Color,
    @Serializable(with = ColorSerializer::class) var primaryBorderColor: Color,
    var text: String? = null,
    var keyCode: Int? = null,
    @Serializable(with = ColorSerializer::class) var secondaryTextColor: Color = primaryTextColor,
    @Serializable(with = ColorSerializer::class) var secondaryBackgroundColor: Color = primaryBackgroundColor,
    @Serializable(with = ColorSerializer::class) var secondaryBorderColor: Color = primaryBorderColor,
) {
    @Serializable(with = TextUnitSerializer::class) var fontSize = calculateFontSize(availableSpace = Rect(0, 0, 1, 1))


    /**
     * Returns true if the text fits in the GridCell.
     */
    private fun doesTextFit(fontSize: Float, availableSpace: Rect): Boolean {
        val paint = Paint()
        paint.textSize = fontSize
        if (paint.measureText(text ?: "") > availableSpace.width()) return false
        val size = (text?.split("\n") ?: listOf()).size
        return paint.fontMetrics.let { it.descent - it.ascent } * size <= availableSpace.height()
    }


    /**
     * Calculate a TextUnit size appropriate for the size of the board.
     */
    fun calculateFontSize(availableSpace: Rect): TextUnit {
        var minFontSize = 6f
        var maxFontSize = 24f
        while (maxFontSize - minFontSize > 0.01f) {
            val midFontSize = (minFontSize + maxFontSize) / 2
            if (doesTextFit(midFontSize, availableSpace)) {
                minFontSize = midFontSize
            } else {
                maxFontSize = midFontSize
            }
        }
        return minFontSize.sp
    }

    /**
     * Return a copy of this GridColor, overwritten with the provided text.
     */
    fun withText(s: String) = ModifierTheme(
        primaryTextColor = primaryTextColor.copy(),
        primaryBackgroundColor = primaryBackgroundColor.copy(),
        primaryBorderColor = primaryBorderColor.copy(),
        text = s,
    )


    /**
     * Return a copy of this GridColor, overwritten with the provided background color.
     */
    fun withButtonBackgroundColor(
        primaryColor: Color,
        secondaryColor: Color = primaryColor,
    ) = copy(primaryBackgroundColor = primaryColor, secondaryBackgroundColor = secondaryColor)


    /**
     * Return a copy of this GridColor, overwritten with the provided background color.
     */
    fun withButtonBorderColor(
        primaryColor: Color,
        secondaryColor: Color = primaryColor,
    ) = copy(primaryBorderColor = primaryBorderColor, secondaryBackgroundColor = secondaryColor)

    companion object {
        /**
         * Black background with white foreground.
         */
        val CHALK_BOARD: ModifierTheme = ModifierTheme(
            primaryTextColor = Color.White,
            primaryBackgroundColor = Color.Black,
            primaryBorderColor = Color.White,
        )

        /**
         * Fully transparent, such that the containing GestureButton's color theme
         * takes precedence.
         */
        val BLANK: ModifierTheme = ModifierTheme(
            primaryTextColor = Color.Transparent,
            primaryBackgroundColor = Color.Transparent,
            primaryBorderColor = Color.Transparent,
        )

        /**
         * Return the alignment appropriate for the position of the Gesture.
         */
        fun alignment(gestureType: GestureType): Alignment {
            return when (gestureType) {
                GestureType.SWIPE_UP_LEFT -> Alignment.TopStart
                GestureType.SWIPE_UP -> Alignment.TopCenter
                GestureType.SWIPE_UP_RIGHT -> Alignment.TopEnd
                GestureType.SWIPE_LEFT -> Alignment.CenterStart
                GestureType.CLICK -> Alignment.Center
                GestureType.SWIPE_RIGHT -> Alignment.CenterEnd
                GestureType.SWIPE_DOWN_LEFT -> Alignment.BottomStart
                GestureType.SWIPE_DOWN -> Alignment.BottomCenter
                GestureType.SWIPE_DOWN_RIGHT -> Alignment.BottomEnd
                else -> {
                    Alignment.Center
                }
            }
        }

        /**
         * Return the textAlignment appropriate for the position of the Gesture.
         */
        fun textAlignment(gridArea: GridArea): TextAlign {
            return when (gridArea.position) {
                RectangleLocation.topLeft, RectangleLocation.topCenter, RectangleLocation.topRight ->
                    TextAlign.Start

                RectangleLocation.left, RectangleLocation.center, RectangleLocation.right ->
                    TextAlign.Center

                RectangleLocation.bottomLeft, RectangleLocation.bottomCenter, RectangleLocation.bottomRight ->
                    TextAlign.End

                else -> {
                    TextAlign.Center
                }
            }
        }

        fun create(
            none: String,
            once: String = none.uppercase(),
            repeat: String = once,
            kind: ModifierKeyKind,
        ) = ModifierThemeSet.forModifierWithDefaultTheme(none, once, repeat, kind)
    }

    /**
     * Return a copy of this GridColor, overwritten with the provided border color.
     */
    private fun withBorderColor(transparent: Color): ModifierTheme = ModifierTheme(
        primaryTextColor = primaryTextColor.copy(),
        primaryBackgroundColor = primaryBackgroundColor.copy(),
        primaryBorderColor = transparent,
        text = text,
    )

    /**
     * Return a copy of this GridColor, overwritten with the provided text background color.
     */
    private fun withTextBackgroundColor(transparent: Color): ModifierTheme = ModifierTheme(
        primaryTextColor = primaryTextColor.copy(),
        primaryBackgroundColor = transparent,
        primaryBorderColor = primaryBorderColor.copy(),
        text = text,
    )

    /**
     * Return a copy of this GridColor, overwritten with the provided box foreground color.
     */
    fun withTextColor(color: Color): ModifierTheme = ModifierTheme(
        primaryTextColor = color,
        primaryBackgroundColor = primaryBackgroundColor.copy(),
        primaryBorderColor = primaryBorderColor.copy(),
        text = text,
    )

    /**
     * Return a copy of this GridColor, overwritten with the provided text size.
     */
    fun withTextSize(sp: TextUnit): ModifierTheme {
        return ModifierTheme(
            primaryTextColor = primaryTextColor.copy(),
            primaryBackgroundColor = primaryBackgroundColor.copy(),
            primaryBorderColor = primaryBorderColor.copy(),
            text = text,
        )
    }
}