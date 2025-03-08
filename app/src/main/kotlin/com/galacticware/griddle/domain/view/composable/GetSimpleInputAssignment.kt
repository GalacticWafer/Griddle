package com.galacticware.griddle.domain.view.composable

import kotlin.math.min

/**
 * Displays a help message in the text field until the user puts some text in it.
 * Up to 4 characters are printed, replacing the fourth character as new characters are typed.
 */
class GetSimpleInputAssignment {
    private var stringValue = "enter up to four characters"
    fun updateText(s: String?) {
        if(s == null) {
            stringValue = "enter up to four characters"
            return
        }
        if(stringValue.isEmpty()) {
            stringValue = if(s.isEmpty()) {
                "enter up to four characters"
            } else {
                s[0].toString()
            }
        } else if(stringValue.length >= 4){
            if(stringValue == "enter up to four characters") {
                if(s.isNotEmpty()) {
                    stringValue = s[0].toString()
                }
                return
            }
            stringValue = if(s.isNotEmpty()) {
                stringValue.substring(0, min(3, stringValue.length)) + s[0]
            } else {
                stringValue.substring(0, stringValue.length - 1)
            }
        } else {
            if(s.isNotEmpty()) {
                stringValue = stringValue.substring(0, min(3, stringValue.length)) + s[0]
            } else {
                if(stringValue.isNotEmpty()) {
                    stringValue = stringValue.substring(0, stringValue.length - 1)
                    if(stringValue.isEmpty()) {
                        stringValue = "enter up to four characters"
                    }
                }
            }
        }
    }
    fun value(): String = stringValue
}