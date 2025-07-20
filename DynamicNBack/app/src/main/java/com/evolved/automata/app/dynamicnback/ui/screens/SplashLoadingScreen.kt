package com.evolved.automata.app.dynamicnback.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.evolved.automata.app.dynamicnback.ui.NBackViewModel
import com.evolved.automata.app.dynamicnback.ui.UIController
import kotlinx.serialization.Serializable


@Serializable
object SplashLoadLastProfile

@Composable
fun SplashLoadingScreen(uiController:UIController, modifier: Modifier, viewModel: NBackViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) {
        viewModel.isReadyAsync.await()
        uiController.returnToNewGameConfig(viewModel.lastConfiguredProfile.value)
    }

    Column(modifier = modifier.fillMaxSize()) {
        Text("Splash screen!")
    }
}