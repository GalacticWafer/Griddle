package com.galacticware.griddle.domain.model.keyboard.definition.designs.griddle.english.keyboard

import android.content.Context
import com.galacticware.griddle.domain.model.keyboard.Keyboard
import com.galacticware.griddle.domain.model.keyboard.definition.designs.base.KeyboardBuilder
import com.galacticware.griddle.domain.model.keyboard.definition.designs.griddle.english.layer.GriddleAlphanumericEnglishLayerBuilder
import com.galacticware.griddle.domain.model.keyboard.definition.designs.griddle.english.layer.GriddleEnglishLayerBuilder
import com.galacticware.griddle.domain.model.keyboard.definition.designs.griddle.english.layer.GriddleNumeroSymbolicLayerBuilder
import com.galacticware.griddle.domain.model.keyboard.definition.designs.griddle.english.layer.GriddleFunctionLayerBuilder
import com.galacticware.griddle.domain.model.keyboard.definition.designs.griddle.english.layer.GriddleNumericLayerBuilder

/**
 *
 * Build an English keyboard with the "Griddle" layout, using all the layers
 * defined in the
 * [com.galacticware.griddle.domain.model.keyboard.definition.designs.griddle] package.
 */
object GriddleEnglishKeyBoardBuilder: KeyboardBuilder {
    override fun build(context: Context ): Keyboard = Keyboard(
        context,
        name = "GriddleEnglishBoard",
        layers = setOf(
            GriddleEnglishLayerBuilder,
            GriddleNumericLayerBuilder,
            GriddleNumeroSymbolicLayerBuilder,
            GriddleAlphanumericEnglishLayerBuilder,
            GriddleFunctionLayerBuilder,
        )
            .map { it.build(context) }
            .toMutableSet()
    )
}