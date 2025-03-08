package com.galacticware.griddle.domain.model.keyboard;

import com.galacticware.griddle.domain.model.layer.LayerKind.ALPHA
import com.galacticware.griddle.domain.model.layer.LayerKind.FUNCTION
import com.galacticware.griddle.domain.model.layer.LayerKind.NUMERO_SYMBOLIC
import com.galacticware.griddle.domain.model.layer.LayerKind.UNIFIED_ALPHA_NUMERIC

enum class KeyboardKind(val label: String) {
    DEFAULT("default"),
    SINGLE_BUTTON_DESIGNER_MODE("designer a button"),
    USER_DEFINED("user-defined"),
    ;
    fun mandatoryLayerKinds() = when(this) {
        DEFAULT -> setOf(ALPHA, NUMERO_SYMBOLIC , UNIFIED_ALPHA_NUMERIC, FUNCTION)
        SINGLE_BUTTON_DESIGNER_MODE -> setOf()
        USER_DEFINED -> setOf(ALPHA, NUMERO_SYMBOLIC ,UNIFIED_ALPHA_NUMERIC, FUNCTION)
    }
}
