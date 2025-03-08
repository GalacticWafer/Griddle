package com.galacticware.griddle.domain.model.button

import androidx.compose.ui.unit.IntSize
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object IntSizeSerializer : KSerializer<IntSize> {
    // Define the descriptor for IntSize as a string
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("IntSize", PrimitiveKind.STRING)

    // Serialize: Convert IntSize to a string format "width,height"
    override fun serialize(encoder: Encoder, value: IntSize) {
        encoder.encodeString("${value.width},${value.height}")
    }

    // Deserialize: Convert the string back to an IntSize
    override fun deserialize(decoder: Decoder): IntSize {
        val str = decoder.decodeString()
        val (width, height) = str.split(",").map { it.toInt() }
        return IntSize(width, height)
    }
}