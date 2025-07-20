package com.evolved.automata.app.dynamicnback.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.evolved.automata.app.dynamicnback.R
import com.evolved.automata.app.dynamicnback.formatWithSignificantDigits
import com.evolved.automata.app.dynamicnback.model.types.CellType
import com.evolved.automata.app.dynamicnback.model.types.GameConfig
import com.evolved.automata.app.dynamicnback.model.types.Profile
import com.evolved.automata.app.dynamicnback.model.types.BadgeConfig
import com.evolved.automata.app.dynamicnback.model.types.BadgeType
import com.evolved.automata.app.dynamicnback.model.usecases.PlayGameUsecase
import com.evolved.automata.app.dynamicnback.ui.NBackViewModel
import com.evolved.automata.app.dynamicnback.ui.UIController
import kotlinx.serialization.Serializable


@Serializable
data class StartNewGame(val initialGameConfig: String)

@Composable
fun PlayGameScreen(uiController:UIController, modifier: Modifier, initialProfile:Profile, viewModel: NBackViewModel = hiltViewModel()) {
    var playGameUsecase:PlayGameUsecase by remember { mutableStateOf(viewModel.getPlayGameUsecase(uiController, initialProfile))}

    val onAttestation:()->Unit = {
        playGameUsecase.onAttestation(System.currentTimeMillis())
    }
    val profile = initialProfile
    var Nback by remember { mutableStateOf(profile.lastConfig.N) }
    var impressionLength by remember { mutableStateOf(profile.lastConfig.impressionPeriodMS) }
    var reviewLength by remember { mutableStateOf(profile.lastConfig.reviewPeriodMS) }

    val onChangeN:(Int) ->Unit = { N:Int ->
        playGameUsecase.onNewN(N)
        Nback = N
    }

    val onChangedImpressionPeriod: (Long) -> Unit = { newDuration:Long ->
        playGameUsecase.onUpdatedImpressionPeriod(newDuration)
        impressionLength = newDuration
    }

    val onChangedReviewPeriod: (Long) -> Unit = {newPeriod:Long ->
        playGameUsecase.onUpdatedReviewPeriod(newPeriod)
        reviewLength = newPeriod
    }

    val onReturnToNewGame:()-> Unit = {
        uiController.returnToNewGameConfig(initialProfile.copy(lastConfig = GameConfig(Nback, impressionLength, reviewLength)))
    }

    LaunchedEffect(Unit) {
        val finalScore:Float = playGameUsecase.run().await()
        uiController.reviewLastGame(initialProfile.copy(lastConfig = GameConfig(Nback, impressionLength, reviewLength)), finalScore)
    }

    DisposableEffect(Unit) {
        onDispose {
            playGameUsecase.quit()
        }
    }

    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Playing with profile: ${profile.name}.  Try to get a new high score!", style = typography.headlineLarge, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        GameStatsBar(Modifier.weight(2.5F), playGameUsecase)
        StimulusPanel(playGameUsecase)
        ControlPanel(onAttestation)
        GameConfigPanel(Modifier.weight(7.5F), Nback, impressionLength, reviewLength, onChangeN, onChangedImpressionPeriod, onChangedReviewPeriod, onReturnToNewGame)
    }
}

@Composable
fun GameStatsBar(modifier:Modifier = Modifier,gameRunner: PlayGameUsecase, model: NBackViewModel = hiltViewModel()) {
    val gameState by gameRunner.gameState.collectAsStateWithLifecycle()

    val earnedBadge = gameState.badgeRewarded
    val badgeConfig:BadgeConfig = gameRunner.profile.experimentConfig.badgeConfig

    when (badgeConfig.badgeType) {
        BadgeType.VISUAL, BadgeType.AUDIO_VISUAL -> {
            Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                if (earnedBadge) {
                    RenderVisualBadge(Modifier.fillMaxHeight(0.5F), badgeConfig)
                    if (badgeConfig.badgeType == BadgeType.AUDIO_VISUAL) {
                        model.playBadgeAlert(badgeConfig.contentKey)
                    }
                }
                else {
                    Spacer(modifier = Modifier.fillMaxHeight(0.5F))
                }
                Row(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.5F), horizontalArrangement = Arrangement.SpaceBetween){
                    Text("Score: ${formatWithSignificantDigits(gameState.totalScore, 2)}")
                    Text("Avg Score: ${formatWithSignificantDigits(gameState.averageScore, 2)}")
                    Text("Trial: ${(1 + gameState.trialIndex)}")
                }
            }
        }
        BadgeType.AUDIO -> {
            if (earnedBadge) {
                Log.i("o___o", "Playing tone")
                model.playBadgeAlert(badgeConfig.contentKey)
            }
            Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically){
                Text("Score: ${formatWithSignificantDigits(gameState.totalScore, 2)}")
                Text("Avg Score: ${formatWithSignificantDigits(gameState.averageScore, 2)}")
                Text("Trial: ${(1 + gameState.trialIndex)}")
            }
        }
    }
}

@Composable
fun RenderVisualBadge(modifier: Modifier = Modifier, badge:BadgeConfig) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Icon(painter = painterResource(R.drawable.ic_thumb_up_black_24dp), tint = Color.Red, contentDescription = "", modifier = Modifier.size(20.dp))
    }

}

@Composable
fun StimulusPanel(gameRunner: PlayGameUsecase, modifier:Modifier = Modifier.fillMaxWidth(0.66F).aspectRatio(1F)) {
    val gameState by gameRunner.gameState.collectAsStateWithLifecycle()

    val selectedRow = gameState.stimulus.row
    val selectedCol = gameState.stimulus.col
    val isInActive = gameState.stimulus.isInactive

    Column(modifier = modifier, verticalArrangement = Arrangement.SpaceEvenly) {
        for (row in 0..2) {
            Row(modifier = Modifier.fillMaxWidth().weight(1F, true), horizontalArrangement = Arrangement.SpaceEvenly) {
                for (col in 0 .. 2) {
                    if (col == selectedCol && row == selectedRow) {
                        StimulusCell(modifier = Modifier.fillMaxHeight().weight(1F), isInActive, gameState.stimulus.cellType, gameState.stimulus.cellValueKey)
                    }
                    else {
                        StimulusCell(modifier = Modifier.fillMaxHeight().weight(1F), true, gameState.stimulus.cellType, gameState.stimulus.inactiveCellValue)
                    }

                }
            }
        }
    }

}


@Preview
@Composable
fun ControlPanel(onAttestation:()->Unit = {}) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Button(
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 2.dp,
                disabledElevation = 0.dp),
            onClick = { onAttestation()}, colors = ButtonDefaults.buttonColors(contentColor = colorScheme.onPrimaryContainer, containerColor = colorScheme.primary)) {
            Text("SAME", fontSize = 25.sp, fontWeight = FontWeight.Bold)
        }

    }
}

@Preview
@Composable
fun GameConfigPanel(modifier:Modifier = Modifier, N:Int = 3, impressionMS:Long = 400, reviewMS:Long = 400, onChangeN:(Int) ->Unit = {}, onChangedImpressionPeriod: (Long) -> Unit = {}, onChangedReviewPeriod: (Long) -> Unit = {}, onReturnToNewGame:()-> Unit = {}) {
    Column(modifier = modifier.fillMaxWidth()){
        Text("N: ${N}")
        Slider(value = N.toFloat(), steps = 7, onValueChange = { it:Float -> onChangeN(it.toInt()) },  modifier = Modifier.fillMaxWidth(), valueRange = 2.0F .. 8F)
        Text("Impression duration: ${impressionMS}")
        Slider(value = impressionMS.toFloat(), steps = 10, onValueChange = { it:Float -> onChangedImpressionPeriod(it.toLong()) },  modifier = Modifier.fillMaxWidth(), valueRange = 100F .. 4000F)
        Text("Review duration: ${reviewMS}")
        Slider(value = reviewMS.toFloat(), steps = 10, onValueChange = { it:Float -> onChangedReviewPeriod(it.toLong()) },  modifier = Modifier.fillMaxWidth(), valueRange = 100F .. 4000F)

        Row(verticalAlignment = Alignment.CenterVertically){
            IconButton(onClick = { onReturnToNewGame()}) {
                Icon(painter = painterResource(R.drawable.icon_exit_back_medium), contentDescription = "back to game config")
            }
            Text("Exit", modifier = Modifier.clickable(){
                onReturnToNewGame()
            })
        }

    }
}


@Composable
fun StimulusCell(modifier:Modifier = Modifier.size(30.dp), isInactive:Boolean, cellType: CellType = CellType.COLORS, cellValue:String = CellType.COLORS.cellItems[0]) {
    when (cellType) {
        CellType.COLORS -> {
            if (isInactive) {
                Box(modifier = modifier.padding(2.dp).background(Color(cellType.inactiveCell.toColorInt()) )) {

                }
            }
            else {
                Box(modifier = modifier.padding(2.dp).background(Color(cellValue.toColorInt()) )) {

                }
            }
        }
    }
}
