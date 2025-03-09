package com.galacticware.griddle.domain.view.composable.nestedappscreen

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable
import com.galacticware.griddle.domain.model.button.GestureButton
import com.galacticware.griddle.domain.model.designer.KeyboardPart
import com.galacticware.griddle.domain.model.gesture.BoomerangEast
import com.galacticware.griddle.domain.model.gesture.BoomerangNorth
import com.galacticware.griddle.domain.model.gesture.BoomerangNorthEast
import com.galacticware.griddle.domain.model.gesture.BoomerangNorthWest
import com.galacticware.griddle.domain.model.gesture.BoomerangSouth
import com.galacticware.griddle.domain.model.gesture.BoomerangSouthEast
import com.galacticware.griddle.domain.model.gesture.BoomerangSouthWest
import com.galacticware.griddle.domain.model.gesture.BoomerangWest
import com.galacticware.griddle.domain.model.gesture.CircleClockwise
import com.galacticware.griddle.domain.model.gesture.CircleCounterClockwise
import com.galacticware.griddle.domain.model.gesture.Click
import com.galacticware.griddle.domain.model.gesture.Gesture
import com.galacticware.griddle.domain.model.gesture.GestureAssignment
import com.galacticware.griddle.domain.model.shared.GenericGestureType
import com.galacticware.griddle.domain.model.gesture.GesturePerformanceInfo
import com.galacticware.griddle.domain.model.gesture.GestureType
import com.galacticware.griddle.domain.model.gesture.Hold
import com.galacticware.griddle.domain.model.gesture.SwipeEast
import com.galacticware.griddle.domain.model.gesture.SwipeNorth
import com.galacticware.griddle.domain.model.gesture.SwipeNorthEast
import com.galacticware.griddle.domain.model.gesture.SwipeNorthWest
import com.galacticware.griddle.domain.model.gesture.SwipeSouth
import com.galacticware.griddle.domain.model.gesture.SwipeSouthEast
import com.galacticware.griddle.domain.model.gesture.SwipeSouthWest
import com.galacticware.griddle.domain.model.input.AppInputFocus
import com.galacticware.griddle.domain.model.input.GriddleInputConnection
import com.galacticware.griddle.domain.model.keyboard.Keyboard
import com.galacticware.griddle.domain.model.keyboard.KeyboardHandedness
import com.galacticware.griddle.domain.model.keyboard.KeyboardKind
import com.galacticware.griddle.domain.model.keyboard.KeyboardModel
import com.galacticware.griddle.domain.model.layer.LayerDefinable
import com.galacticware.griddle.domain.model.layer.LayerKind
import com.galacticware.griddle.domain.model.operation.implementation.noargs.simpleinput.SimpleInput
import com.galacticware.griddle.domain.model.operation.base.OperationTag.Companion.userConfigurableOperations
import com.galacticware.griddle.domain.model.usercontolled.userdefinedgesturemapping.ReassignmentData
import com.galacticware.griddle.domain.model.util.PreferencesHelper
import com.galacticware.griddle.domain.model.keyboard.definition.theme.DEFAULT_SIZE
import com.galacticware.griddle.domain.model.layer.CreateLayer
import com.galacticware.griddle.domain.model.modifier.ModifierThemeSet
import com.galacticware.griddle.domain.model.operation.base.Operation
import com.galacticware.griddle.domain.model.screen.NestedAppScreen
import com.galacticware.griddle.domain.model.shared.Direction
import com.galacticware.griddle.domain.model.shared.RotationDirection
import com.galacticware.griddle.domain.view.composable.BuildCurrentLayer
import com.galacticware.griddle.domain.view.colorization.Hue
import com.galacticware.griddle.domain.viewmodel.BuildYourOwnKeyboardViewModel
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

val  BuildYourOwnKeyboardScreen = KeyboardDesignerScreen()

/**
 * Kotlin objects don't support dagger injection. We need screens to be objects only because that's
 * the recommended way to work with NavController. But we don't actually use NavController, so
 * we can consider deprecating the use of objects if we run in to more issues.
 * As a work-around, we declare this [KeyboardDesignerScreen] as a class, and make our object
 * instance from that.
 */
@Serializable
class KeyboardDesignerScreen : NestedAppScreen(){
    override val addBackButton get() = false
    @Composable
    override fun Show() {
        val byokViewModel: BuildYourOwnKeyboardViewModel = viewModel()
        WhileOnTop {
            val currentPart by byokViewModel.currentlyEditedKeyboardPart.collectAsState()
            pivotToCurrentlySelectedKeyboardPart(currentPart)
        }
    }

    @Composable
    private fun pivotToCurrentlySelectedKeyboardPart(currentPart: KeyboardPart) {
        Row(
            modifier = Modifier
                .fillMaxHeight(93/100f)
                .border(1.dp, Hue.ORANGE.hex)
                .zIndex(5f)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .border(1.dp, Color.Red)
            ) {
                when (currentPart) {
                    KeyboardPart.BOARD -> BoardDefaultsEditor()
                    KeyboardPart.LAYER -> LayerPartEditor()
                    KeyboardPart.BUTTON -> ButtonPartEditor()
                    KeyboardPart.GESTURE -> GesturePartEditor()
                }
            }
        }
    }

    @Composable fun BoardDefaultsEditor() {
        val byokViewModel: BuildYourOwnKeyboardViewModel = viewModel()
        var currentPart by remember { mutableStateOf(KeyboardPart.BOARD) }
        var askToSwitchToButtonModeWith by remember { mutableStateOf<GestureButton?>(null) }
        var currentPart1 by remember { mutableStateOf(currentPart) }
        var currentlyEditedLayer by remember { mutableStateOf(null as KClass<out LayerDefinable>?) }
        val textFieldValueState = remember {
            mutableStateOf(TextFieldValue(text = ""))
        }
        Column(
            modifier = Modifier
                .border(1.dp, Color.Red)
        ) {
            Row {
                Button(
                    onClick = {
                        stack.pop()
                    }
                ) {
                    Text("Back")
                }
                Button(onClick = {
                    val manager = ContextCompat.getSystemService(applicationContext, ClipboardManager::class.java)!!
                    val keyboard = keyboardContext.keyboard
                    val jsonString = Json.encodeToString(KeyboardModel.serializer(), keyboard.model)
                    manager.setPrimaryClip(ClipData.newPlainText(keyboard.name, jsonString))
                    android.os.Handler(Looper.getMainLooper()).post {
                        Toast.makeText(applicationContext, "Copied ", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Copy json")
                }
                Column {
                    Text("Import json (paste to the right)")
                    Row {
                        BasicTextField(
                            modifier = Modifier
                                .requiredSize(20.dp, 20.dp)
                                .border(1.dp, Color.Red)
                            ,
                            value = textFieldValueState.value,
                            onValueChange = { tfv ->
                                textFieldValueState.value = tfv
                            },
                            minLines = 5,
                            maxLines = 5,
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 20.sp
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        )
                        Button(
                            modifier = Modifier
                                .wrapContentSize()
                                .border(1.dp, Color.Green)

                            ,
                            onClick = {
                                try {
                                    val manager = ContextCompat.getSystemService(
                                        applicationContext,
                                        ClipboardManager::class.java
                                    )!!
                                    val string = manager.primaryClip!!.getItemAt(0).text.toString()
                                    val keyboard = Json.decodeFromString<KeyboardModel>(string).toKeyboard(applicationContext)
                                    keyboardContext.switchKeyboards(keyboard)
                                    Toast.makeText(applicationContext, "Loaded!", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(applicationContext, "Error loading json from clipboard", Toast.LENGTH_SHORT).show()
                                    keyboardContext.keyboard
                                }
                            }) {
                            Text("PASTE HERE")
                        }

                        Button(
                            modifier = Modifier
                                .border(1.dp, Color.Cyan),
                            onClick = {
                                val manager = ContextCompat.getSystemService(applicationContext, ClipboardManager::class.java)!!
                                val keyboard = keyboardContext.keyboard
                                val jsonString = Json.encodeToString(KeyboardModel.serializer(), keyboard.model)
                                manager.setPrimaryClip(ClipData.newPlainText(keyboard.name, jsonString))
                                android.os.Handler(Looper.getMainLooper()).post {
                                    Toast.makeText(applicationContext, "Copied ", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                            Text("Save as json")
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .border(1.dp, Hue.BLUE.hex)
            ) {
                KeyboardComponentLabel("Board Options")
            }
            Row(
                modifier = Modifier
                    .wrapContentSize()
            ) {
                Column(
                    modifier = Modifier
                        .requiredWidth(80.dp)
                        .border(1.dp, Hue.GREEN.hex)
                        .fillMaxHeight()
                        .padding(top = 16.dp),
                ) {
                    KeyboardComponentLabel("Layers")
                    KeyboardComponentLabel("Default\nColors")
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(1.dp, Hue.PURPLE.hex)
                        .padding(top = 16.dp)
                    ,
                ) {
                    Box {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(7.dp)

                        ) {
                            items(keyboardContext.keyboard.layers.toList()) { layer ->
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.TopCenter,
                                ) {
                                    Button(
                                        modifier = Modifier
                                            .heightIn(30.dp, 60.dp)
                                            .wrapContentWidth(),
                                        onClick = {
                                            keyboardContext.keyboard.switchToLayerKind(layer.layerKind)
                                            byokViewModel.setCurrentlyEditedKeyboardPart(KeyboardPart.LAYER)
                                            byokViewModel.setCheckAndSwitchToLayerEditingModeWith(layer)
                                        }) {
                                        Text("${layer.name} (${layer.layerKind.prettyName})")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun KeyboardComponentLabel(label: String) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(label)
        }
    }

    @Composable fun LayerPartEditor() {
        val byokViewModel: BuildYourOwnKeyboardViewModel = viewModel()
        val currentlyEditedLayer by byokViewModel.checkAndSwitchToLayerEditingModeWith.collectAsState()
        val setKeyboardScopeToBoard = { byokViewModel.setCurrentlyEditedKeyboardPart(KeyboardPart.BOARD) }
        Button(
            onClick = setKeyboardScopeToBoard
        ) {
            Text("Back")
        }
        KeyboardComponentLabel("Layer Defaults")
        KeyboardComponentLabel("(Select a button to edit a gesture)")
        val context = LocalContext.current
        currentlyEditedLayer?.let { layer ->
            val keyboard = keyboardContext.keyboard
            Keyboard.currentLayer = keyboard.currentLayer()
            BuildCurrentLayer(keyboard, context)
        } ?: run {
            Text("No layer selected")
        }
    }

    @Composable fun ButtonPartEditor() {
        KeyboardComponentLabel("Button Defaults")

    }

    @Composable fun GesturePartEditor() {
        val byokViewModel: BuildYourOwnKeyboardViewModel = viewModel()
        val askForConfirmation by byokViewModel.askForConfirmation.collectAsState()
        KeyboardComponentLabel("Gesture Info")
        val layerDefinable by byokViewModel.checkAndSwitchToLayerEditingModeWith.collectAsState()
        val performedGestureInfo by byokViewModel.currentlySelectedGestureInfo.collectAsState()
        val performanceInfo = performedGestureInfo?: throw IllegalStateException("No gesture info is available")
        val (gesture, previousOperation, assumedDirection, genericGestureType, buttonContainingFirstPoint, buttonContainingLastPoint, rotationDirection) = performanceInfo
        val doShowChooseAnotherOperationScreen by byokViewModel._doShowChooseAnotherOperationScreen.collectAsState()
        Column {
            Button(onClick = finishEditing(byokViewModel){}) {
                Text("Back")
            }
            Button(onClick = finishEditing(byokViewModel){ Keyboard.clearScreenStack() }) {
                Text("Exit")
            }
            val containerBoxModifier = if(askForConfirmation != null) {
                Modifier
                    .background(Hue.MEOK_DARK_GRAY.hex)
                    .zIndex(6f)
            } else {
                Modifier
            }
            Box(modifier = containerBoxModifier
                ,
                contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                    //                    .imePadding()
                    //                    .verticalScroll(scrollState),
                    //.border(10.dp, Color.Green)
                ) {
                    Column(
                        modifier = Modifier
                        //.border(6.dp, Color.Red)
                    ) {
                        var isSupported = true
                        val editorOperation = gesture?.editorOperation?: DEFAULT_OPERATION
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(8.dp)
                                .background(Hue.MEOK_LIGHT_GRAY.hex)
                        ) {
                            Text("Button type: ${buttonContainingFirstPoint.prettyPrintTypeLabel}")
                            val gestureName = genericGestureType.let {
                                when (genericGestureType) {
                                    GenericGestureType.CLICK -> "Click"
                                    GenericGestureType.HOLD -> "Hold"
                                    GenericGestureType.CIRCLE -> "Circle ${rotationDirection!!.prettyPrinted}"
                                    GenericGestureType.SWIPE -> "Swipe ${assumedDirection!!.prettyPrinted}"
                                    GenericGestureType.BOOMERANG -> "Boomerang ${assumedDirection!!.prettyPrinted}"
                                }
                            }
                            val operationDescription = "Gesture: ${
                                try {
                                    gesture?.prettyPrinted ?: "No operation is assigned to $gestureName"
                                } catch (e: IllegalStateException) {
                                    isSupported = false
                                    "Remapping that operation is not yet supported"
                                }
                            }"
                            try {
                                editorOperation.menuItemDescription
                                editorOperation.userHelpDescription
                            } catch (e: IllegalStateException) {
                                isSupported = false
                            }
                            Text(operationDescription)
                            Row {
                                Button(
                                    onClick = {
                                        byokViewModel.setCurrentlyEditedKeyboardPart(KeyboardPart.LAYER)
                                        byokViewModel.setIsWaitingForGestureInfo(false)
                                        byokViewModel.setCurrentlySelectedGestureInfo(null)
                                        byokViewModel.setReassignmentData(null)
                                        BuildYourOwnKeyboardViewModel.editableInputCallbackMap[AppInputFocus.SIMPLE_INPUT_ASSIGNMENT]!!.updateText(null)
                                        byokViewModel.setIsWaitingForParameters(false)
                                    }
                                ) {
                                    Text("Choose a different gesture")
                                }
                                Button(
                                    onClick = {
                                        byokViewModel.setDoShowChooseAnotherOperationScreen(true)
                                    }
                                ) {
                                    Text("Choose another operation")
                                }
                            }
                            layerDefinable ?: run {
                                Text("No layer selected")
                                return
                            }
                        }
                        val context = LocalContext.current
                        var lc by remember { mutableStateOf(null as LayoutCoordinates?) }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .onGloballyPositioned { lc = it },
                        ) {
                            Column(
                                modifier = Modifier
                                    .wrapContentSize(),
                                //.border(2.dp, Color.Green)
                                verticalArrangement = Arrangement.Center,
                            ) {
                                val singleButtonLayer = buttonContainingFirstPoint.let { btn ->
                                    val setWithOnlyOneButtonBuilder = mutableSetOf(btn.builder.reposition(0, 0))
                                    CreateLayer(
                                        context,
                                        "DesignerMode_SingleButtonLayer",
                                        setWithOnlyOneButtonBuilder,
                                        KeyboardHandedness(false, -1),
                                        DEFAULT_SIZE,
                                        true,
                                        LayerKind.USER_DEFINED,
                                        PreferencesHelper.getUserPrimaryLanguage(context)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    //.border(8.dp, Color.Magenta)
                                    contentAlignment = Alignment.TopCenter,
                                ) {
                                    Keyboard.setDesignerLayer(singleButtonLayer)
                                    BuildCurrentLayer(
                                        Keyboard(context, "SingleButtonBYOKBoard", setOf(singleButtonLayer), KeyboardKind.SINGLE_BUTTON_DESIGNER_MODE),
                                        LocalContext.current
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    //.border(8.dp, Color.Magenta)
                                    contentAlignment = Alignment.TopCenter,
                                ) {
                                    Column {
                                        // List oll of the possible operations that the gesture can be reassigned to
                                        // and allow the user to select one of them.
                                        gesture?.let {
                                            if (isSupported) {
                                                Text("${editorOperation.menuItemDescription}\n${editorOperation.userHelpDescription}")
                                                byokViewModel.setReassignmentData(
                                                    ReassignmentData(gesture, editorOperation)
                                                )
                                                editorOperation.ShowReassignmentScreen(context, gesture)
                                                BuildCurrentLayer(
                                                    keyboardContext.keyboard,
                                                    context
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (doShowChooseAnotherOperationScreen) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                        //                        .zIndex(2f)
                        ,
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .wrapContentSize()
                                .background(Color.Black.copy(alpha = 0.5f))
                            //                            .zIndex(2f)
                            ,
                            contentAlignment = Alignment.Center
                        ) {
                            ChooseAnotherOperationFromList(performanceInfo)
                        }
                    }
                }
                if(askForConfirmation != null) {
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                            .background(Hue.ROARNGE.hex)
                        //                        .zIndex(2f)
                        ,
                        contentAlignment = Alignment.Center
                    ) {
                        Column {
                            Text(askForConfirmation!!)
                            Row {
                                Button(
                                    onClick = {
                                        val data = byokViewModel.reassignmentDataStateFlow.value!!
                                        val newGesture = data.operation.produceNewGesture(data.draftGesture)
                                        val gestureType = GestureType.fromInstance(newGesture)
                                        buttonContainingFirstPoint.gestures[gestureType] = newGesture
                                        keyboardContext.apply {
                                            keyboard.designerButtonSave(
                                                context,
                                                buttonContainingFirstPoint,
                                                newGesture,
                                            )
                                        }
                                        byokViewModel.setAskForConfirmation(null)
                                        byokViewModel.setIsWaitingForGestureInfo(true)
                                        byokViewModel.setCurrentlySelectedGestureInfo(null)
                                        byokViewModel.setReassignmentData(null)
                                        byokViewModel.setDoShowChooseAnotherOperationScreen(false)
                                        byokViewModel.setShouldShowKeyboard(true)
                                    }
                                ) {
                                    Text("Yes!")
                                }
                                Button(
                                    onClick = {
                                        byokViewModel.setAskForConfirmation(null)
                                    }
                                ) {
                                    Text("NO!")
                                }
                                Button(
                                    onClick = {
                                        byokViewModel.setAskForConfirmation(null)
                                    }
                                ) {
                                    Text("idk... let me try it first!")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun finishEditing(byokViewModel: BuildYourOwnKeyboardViewModel, f: ()->Unit) = {
        byokViewModel.setCurrentlyEditedKeyboardPart(KeyboardPart.LAYER)
        byokViewModel.setCurrentlySelectedGestureInfo(null)
        GriddleInputConnection.inputFocus = AppInputFocus.DEFAULT
    }

    @Composable
    fun ChooseAnotherOperationFromList(
        gesturePerformanceInfo: GesturePerformanceInfo,
    ) {
        val byokViewModel: BuildYourOwnKeyboardViewModel = viewModel()
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            userConfigurableOperations
                .forEach { operation ->
                    item {
                        Button(
                            onClick = {
                                val gesture = gesturePerformanceInfo.genericGestureType.newGestureWith(gesturePerformanceInfo, operation)
                                val data = ReassignmentData(gesture, operation)
                                byokViewModel.setReassignmentData(data)
                                val newPerformanceInfo = gesturePerformanceInfo.withNewGesture(gesture)
                                byokViewModel.setGestureInfoFromNewOperation(newPerformanceInfo)
                                byokViewModel.setDoShowChooseAnotherOperationScreen(false)
                                byokViewModel.setIsWaitingForGestureInfo(operation.requiresUserInput)
                            }
                        ) {
                            Text(
                                text = operation.menuItemDescription,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                    }
                }
        }
    }
    private val DEFAULT_OPERATION by lazy { SimpleInput }
}

fun GenericGestureType.newGestureWith(
    gesturePerformanceInfo: GesturePerformanceInfo,
    operation: Operation
) = run {
    val button = gesturePerformanceInfo.buttonContainingFirstPoint
    val modifierThemeSet = ModifierThemeSet.allSameTheme(button.modifierTheme, noneStateText = "")
    val gestureAssignment = GestureAssignment(
        operation = operation,
        modifiers = emptySet(),
        isDisplayable = true,
        gridArea = button.gridArea,
        modifierThemeSet = modifierThemeSet,
        appSymbol = null,
        isIndicator = false,
        argsJson = gesturePerformanceInfo.gesture?.currentAssignment?.argsJson,
    )
    when (this) {
        GenericGestureType.CLICK -> Click(gestureAssignment)
        GenericGestureType.HOLD -> Hold(gestureAssignment)
        GenericGestureType.SWIPE -> gesturePerformanceInfo.swipeDirection!!.newInstance(gestureAssignment, isSwipe = true)
        GenericGestureType.BOOMERANG -> gesturePerformanceInfo.swipeDirection!!.newInstance(gestureAssignment, isSwipe = false)
        GenericGestureType.CIRCLE -> gesturePerformanceInfo.rotationDirection!!.newInstance(gestureAssignment, )
    }
}
fun RotationDirection.newInstance(gestureAssignment: GestureAssignment): Gesture = when (this) {
   RotationDirection.CLOCKWISE -> CircleClockwise::class
    RotationDirection.ANTI_CLOCKWISE -> CircleCounterClockwise::class
}.constructors.first().call(gestureAssignment)

fun Direction.newInstance(gestureAssignment: GestureAssignment, isSwipe: Boolean): Gesture = when (this) {
    Direction.EAST -> if(isSwipe) SwipeEast::class else BoomerangEast::class
    Direction.NORTHEAST -> if(isSwipe) SwipeNorthEast::class else BoomerangNorthEast::class
    Direction.NORTH -> if(isSwipe) SwipeNorth::class else BoomerangNorth::class
    Direction.NORTHWEST -> if(isSwipe) SwipeNorthWest::class else BoomerangNorthWest::class
    Direction.WEST -> if(isSwipe) SwipeSouthWest::class else BoomerangWest::class
    Direction.SOUTHWEST -> if(isSwipe) SwipeSouthWest::class else BoomerangSouthWest::class
    Direction.SOUTH -> if(isSwipe) SwipeSouth::class else BoomerangSouth::class
    Direction.SOUTHEAST -> if(isSwipe) SwipeSouthEast::class else BoomerangSouthEast::class
}.constructors.first().call(gestureAssignment)