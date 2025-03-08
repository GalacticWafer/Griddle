package com.galacticware.griddle.domain.viewmodel

import androidx.lifecycle.ViewModel
import com.galacticware.griddle.domain.model.button.GestureButton
import com.galacticware.griddle.domain.model.designer.KeyboardPart
import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.gesture.GesturePerformanceInfo
import com.galacticware.griddle.domain.model.input.AppInputFocus
import com.galacticware.griddle.domain.model.layer.LayerDefinable
import com.galacticware.griddle.domain.model.usercontolled.userdefinedgesturemapping.ReassignmentData
import com.galacticware.griddle.domain.view.composable.GetSimpleInputAssignment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BuildYourOwnKeyboardViewModel : ViewModel() {

    val _doShowChooseAnotherOperationScreen = MutableStateFlow(false)
    val doShowChooseAnotherOperationScreen: StateFlow<Boolean> = MutableStateFlow(false)
    fun setDoShowChooseAnotherOperationScreen(doShow: Boolean) {
        _doShowChooseAnotherOperationScreen.value = doShow
    }

    private val _currentlyEditedKeyboardPart = MutableStateFlow(KeyboardPart.BOARD)
    val currentlyEditedKeyboardPart: StateFlow<KeyboardPart> = _currentlyEditedKeyboardPart
    fun setCurrentlyEditedKeyboardPart(part: KeyboardPart) {
        _currentlyEditedKeyboardPart.value = part
    }

    private val _checkAndSwitchToLayerEditingModeWith = MutableStateFlow<LayerDefinable?>(null)
    val checkAndSwitchToLayerEditingModeWith: StateFlow<LayerDefinable?> = _checkAndSwitchToLayerEditingModeWith
    fun setCheckAndSwitchToLayerEditingModeWith(layer: LayerDefinable) {
        _checkAndSwitchToLayerEditingModeWith.value = layer
    }

    private val _askToSwitchToButtonModeWith = MutableStateFlow<GestureButton?>(null)
    val askToSwitchToButtonModeWith: StateFlow<GestureButton?> = _askToSwitchToButtonModeWith
    fun setAskToSwitchToButtonModeWith(button: GestureButton?) {
        _askToSwitchToButtonModeWith.value = button
    }

    private val _askToSwitchToGestureModeWith = MutableStateFlow<GestureButton?>(null)
    val askToSwitchToGestureModeWith: StateFlow<GestureButton?> = _askToSwitchToGestureModeWith

    private val _currentlySelectedGestureInfo = MutableStateFlow<GesturePerformanceInfo?>(null)
    val currentlySelectedGestureInfo: StateFlow<GesturePerformanceInfo?> = _currentlySelectedGestureInfo
    fun setCurrentlySelectedGestureInfo(gesturePerformanceInfo: GesturePerformanceInfo?) {
        if(!isWaitingForTextInput.value) {
            _currentlySelectedGestureInfo.value = gesturePerformanceInfo
        }
    }

    val layerPartEditorStep: MutableStateFlow<LayerPartEditorStep> = MutableStateFlow(LayerPartEditorStep.SELECT_A_BUTTON)
    fun setLayerPartEditorStep(step: LayerPartEditorStep) {
        layerPartEditorStep.value = step
    }

    fun setAskToSwitchToGestureModeWith(button: GestureButton?) {
        _askToSwitchToGestureModeWith.value = button
    }

    private val _parametricArgsRequest = MutableStateFlow<ReassignmentData?>(null)
    val reassignmentDataStateFlow: StateFlow<ReassignmentData?> = _parametricArgsRequest
    fun setReassignmentData(request: ReassignmentData?) {
        _parametricArgsRequest.value = request
    }

    private val _shouldShowKeyboard = MutableStateFlow(false)
    val shouldShowKeyboard: StateFlow<Boolean> = _shouldShowKeyboard
    fun setShouldShowKeyboard(shouldShowKeyboard: Boolean) {
        _shouldShowKeyboard.value = shouldShowKeyboard
    }

    private val _isWaitingForTextInput = MutableStateFlow(false)
    val isWaitingForTextInput: StateFlow<Boolean> = _isWaitingForTextInput
    fun setIsWaitingForGestureInfo(b: Boolean) {
        _isWaitingForTextInput.value = b
    }

    private val _askForConfirmation = MutableStateFlow<String?>(null)
    val askForConfirmation: StateFlow<String?> = _askForConfirmation
    fun setAskForConfirmation(s: String?) {
        _askForConfirmation.value = s
    }

    fun setGestureInfoFromNewOperation(gesturePerformanceInfo: GesturePerformanceInfo) {
        _currentlySelectedGestureInfo.value = gesturePerformanceInfo
    }

    private val _isWaitingForParameters = MutableStateFlow(false)
    val isWaitingForParameters: StateFlow<Boolean> = _isWaitingForTextInput
    fun setIsWaitingForParameters(b: Boolean) {
        _isWaitingForParameters.value = b
    }

    companion object {
        var currentSelectedGestureInfo: Pair<GestureButton, Gesture?>? = null
        val editableInputCallbackMap = AppInputFocus.entries
            .minus(AppInputFocus.DEFAULT)
            .associateWith { GetSimpleInputAssignment() }
    }
}