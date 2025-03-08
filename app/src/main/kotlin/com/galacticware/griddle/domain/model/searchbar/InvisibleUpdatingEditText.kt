package com.galacticware.griddle.domain.model.searchbar

import android.content.Context
import android.text.InputType
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.EditText

class InvisibleUpdatingEditText {
    companion object {
        fun newInstance(ctx: Context) = run {
            val invisibleEditText = EditText(ctx)
            invisibleEditText.visibility = View.INVISIBLE
            invisibleEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 0f)
            invisibleEditText.setInputType(InputType.TYPE_NULL)
            invisibleEditText.setLayoutParams(ViewGroup.LayoutParams(0, 0)) // Set width and height to 0
            invisibleEditText
        }
    }
}