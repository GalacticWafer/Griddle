package com.galacticware.griddle.domain.model.layer

import com.galacticware.griddle.domain.model.error.Errors
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol.ALPHA_LAYER
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol.FUNCTION_LAYER
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol.NUMERIC_LAYER
import com.galacticware.griddle.domain.model.appsymbol.AppSymbol.USER_DEFINED_LAYER
import com.galacticware.griddle.domain.model.language.LanguageTag
import com.galacticware.griddle.domain.model.usercontolled.UserLanguageSelector

/**
 * Enumeration kinds of layers that exist.
 */
enum class LayerKind(
    val f: () -> List<LanguageTag?>,
    val prettyName: String,
) {
    ALPHA({ UserLanguageSelector.currentLanguages }, "Alphabetic"),
    UNIFIED_ALPHA_NUMERIC({ UserLanguageSelector.currentLanguages }, "Alphanumeric"),
    NUMERO_SYMBOLIC({ listOf(null, null, null) }, "NumeroSymbolic"),
    NUMERIC({ listOf(null, null, null) }, "Numeric"),
    FUNCTION({ listOf(null, null, null) }, "Function"),
    USER_DEFINED({ UserLanguageSelector.currentLanguages }, "User-Defined"),
    ;

    val symbol: AppSymbol
        get() = when(this) {
        UNIFIED_ALPHA_NUMERIC, ALPHA ->  ALPHA_LAYER
        NUMERO_SYMBOLIC, NUMERIC -> NUMERIC_LAYER
        FUNCTION -> FUNCTION_LAYER
        USER_DEFINED -> USER_DEFINED_LAYER
    }

    companion object {
        fun fromAlias(symbol: AppSymbol) = entries.firstOrNull {
            it.symbol == symbol
        }?: throw Errors.UNKNOWN_LAYER_ALIAS.send("'$symbol'(${symbol.value})")
    }
}