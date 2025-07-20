package com.evolved.automata.app.dynamicnback.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.evolved.automata.app.dynamicnback.formatWithSignificantDigits
import com.evolved.automata.app.dynamicnback.model.types.Profile
import com.evolved.automata.app.dynamicnback.model.types.ScoreHistory
import com.evolved.automata.app.dynamicnback.showDatetimeMoment
import com.evolved.automata.app.dynamicnback.ui.NBackViewModel
import com.evolved.automata.app.dynamicnback.ui.UIController
import kotlinx.serialization.Serializable


@Serializable
data class ReviewLastGame(val lastGameConfig:String, val lastGameScore:Float)

@Composable
fun  ReviewGameScreen(uiController:UIController, modifier: Modifier, initialProfile:Profile, lastScore:Float, viewModel: NBackViewModel = hiltViewModel()) {

    val scoreHistory = initialProfile.scoreHistory

    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
        Column(horizontalAlignment = Alignment.CenterHorizontally){
            Text("Game Review", style = typography.titleLarge)
            Text("Profile: ${initialProfile.name}")
            Text("Last total score: ${formatWithSignificantDigits(lastScore, 2)}")
            if (scoreHistory.isNotEmpty())
                ScoreHistoryPane(history = scoreHistory)
        }

        Column(modifier = Modifier.fillMaxWidth()){
            TextButton(onClick = { uiController.returnToGameScreen(initialProfile) }) {Text("Play Again", style = typography.headlineLarge) }
            TextButton(onClick = { uiController.returnToNewGameConfig(initialProfile)}) {Text("Configure New Game", style = typography.headlineLarge) }
        }

    }

}

@Composable
fun ScoreHistoryPane(modifier:Modifier = Modifier, history:List<ScoreHistory> = listOf<ScoreHistory>()) {
    val scoreHistory:List<ScoreHistory> = history
    LazyColumn(modifier = modifier.height(200.dp)) {
        items(scoreHistory.size, {id:Int -> history[id].finishTS}, itemContent = { it: Int ->
            val history = history[it]
            Text("${showDatetimeMoment(history.finishTS)} ${formatWithSignificantDigits( history.score, 2)}")

        })
    }
}