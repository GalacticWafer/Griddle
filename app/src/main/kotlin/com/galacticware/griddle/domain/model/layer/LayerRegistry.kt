package com.galacticware.griddle.domain.model.layer

import kotlin.reflect.KClass

class LayerRegistry {
    companion object {
        private val registeredLayers = mutableSetOf<LayerDefinable>()
        fun add(ld: LayerDefinable) {
            registeredLayers.add(ld)
        }

        fun layersTheSameSizeAs(layer: LayerDefinable): Set<LayerDefinable> {
            return registeredLayers
                .filter { otherLayer ->
                    otherLayer.originalRowHeight == layer.originalRowHeight
                        && otherLayer.originalColWidth == layer.originalColWidth
                        && otherLayer.colSpan == layer.colSpan
                        && otherLayer.rowSpan == layer.rowSpan
            }.toSet()
        }

        fun get(currentlyEditedLayer: KClass<out LayerDefinable>?): LayerDefinable {
            return registeredLayers.first { it == currentlyEditedLayer }
        }
    }
}