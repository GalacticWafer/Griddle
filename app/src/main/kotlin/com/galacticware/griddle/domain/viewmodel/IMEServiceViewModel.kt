package com.galacticware.griddle.domain.viewmodel

import androidx.lifecycle.ViewModel
import com.galacticware.griddle.domain.model.screen.NestedAppScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class IMEServiceViewModel : ViewModel() {
    val _isVisible = MutableStateFlow(false)
    val isVisible: StateFlow<Boolean> = MutableStateFlow(false)
    fun setIsVisible(doShow: Boolean) {
        _isVisible.value = doShow
    }

    val screen: StateFlow<NestedAppScreen?> get() = MutableStateFlow(NestedAppScreen.stack.peek())
}