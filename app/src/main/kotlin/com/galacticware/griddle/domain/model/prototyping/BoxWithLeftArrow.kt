package com.galacticware.griddle.domain.model.prototyping

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.galacticware.griddle.R
import com.galacticware.griddle.domain.model.keyboard.definition.designs.griddle.english.keyboard.GriddleEnglishKeyBoardBuilder
import com.galacticware.griddle.domain.view.composable.BuildCurrentLayer

@Composable
fun B() {
    Row(Modifier.background(Color.White)) {
        BuildDesignerFourWaySwipeControls()
        Column(Modifier.fillMaxWidth().border(1.dp, Color.Black), verticalArrangement = Arrangement.Top) {
            Row { centerText("Keyboard name:"); centerText("GriddleEnglishBoard") }
            Row { Text("Current layer:"); Text("GriddleEnglishAlphaLayer") }
            Box { Text("Swipe left or right to change layers", Modifier.align(Center)) }
            BuildCurrentLayer(GriddleEnglishKeyBoardBuilder.build(LocalContext.current))
        }
    }
}

@Composable private fun centerText(s: String) = Text(s, textAlign = TextAlign.Center)

@Composable
fun BuildDesignerFourWaySwipeControls() {
    Column {
        FourWaySwipeBox("SAVE", "LOAD", "CANCEL", "EXIT", isDiagonalSwipe = true)
        Spacer(Modifier.requiredWidth(4.dp))
        FourWaySwipeBox("LAYER", "REMOVE", "BUTTON", "ADD", isDiagonalSwipe = false)

    }
}

@Composable
private fun FourWaySwipeBox(
    s1: String,
    s2: String,
    s3: String,
    s4: String,
    isDiagonalSwipe: Boolean
) {
    val n = Modifier
    Box(
        modifier = Modifier
            .size(120.dp, 90.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_1), // Replace with your image name
            contentDescription = "Compass Image",
            modifier = Modifier
                .fillMaxSize(.5f)
                .rotate(if(isDiagonalSwipe)45f else 0f)
                .clip(CircleShape)
                .align(Alignment.Center)
        )
        Box(
            n
                .align(Alignment.TopStart)
                .padding(top = 10.dp, start = 5.dp),
            contentAlignment = Center) { Text(s1) }
        Box(
            n
                .align(Alignment.TopEnd)
                .padding(top = 10.dp, end = 5.dp),
            contentAlignment = Center) { Text(s2) }
        Box(
            n
                .align(Alignment.BottomEnd)
                .padding(bottom = 10.dp, end = 5.dp),
            contentAlignment = Center) { Text(s3) }
        Box(
            n
                .align(Alignment.BottomStart)
                .padding(bottom = 10.dp, start = 5.dp),
            contentAlignment = Center) { Text(s4) }
    }
}