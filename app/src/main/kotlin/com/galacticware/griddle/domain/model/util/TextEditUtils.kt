package com.galacticware.griddle.domain.model.util

import com.galacticware.griddle.domain.model.appsymbol.AppSymbol


/**
 * Helper method to simplify creating case-sensitive [Triple]s corresponding to lowercase,
 * uppercase, and caps-locked respectively.
 */
fun caseSensitive(
    lowercase: String,
    uppercase: String? = null,
    capscase: String? = null,
) = Triple(
    lowercase.lowercase(),
    uppercase?: lowercase.uppercase(),
    capscase?: uppercase?: lowercase.uppercase()
)

/**
 * Helper method to simplify creating reversed-case-sensitive [Triple]s corresponding to lowercase,
 * uppercase, and caps-locked respectively.
 */
fun reversedCase(
    s: String,
    s2: String? = null,
    s3: String? = null,
) = Triple(s.uppercase(), s2?: s.lowercase(), s3?: s2?: s.lowercase())

/**
 * Helper method to simplify creating [Triple]s with all the same String.
 */
fun triple(s: String) = Triple(s, s, s)
/**
 * Helper method to simplify creating [Triple]s with all the same String.
 */
fun triple(symbol: AppSymbol) = triple(symbol.value)