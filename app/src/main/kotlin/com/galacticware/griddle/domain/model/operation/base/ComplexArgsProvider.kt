package com.galacticware.griddle.domain.model.operation.base

import android.content.Context
import androidx.compose.runtime.Composable
import com.galacticware.griddle.domain.model.gesture.Gesture

interface ComplexArgsProvider<T: OperationArgs> {
    fun provideArgs(jsonString: String): T
    @Composable fun ShowArgsFinalizationScreen(context: Context, gesture: Gesture)
}