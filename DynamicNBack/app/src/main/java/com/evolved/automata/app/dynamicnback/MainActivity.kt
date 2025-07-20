package com.evolved.automata.app.dynamicnback

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

import androidx.compose.material3.MaterialTheme.colorScheme

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.evolved.automata.app.dynamicnback.model.types.Profile
import com.evolved.automata.app.dynamicnback.ui.NBackViewModel

import com.evolved.automata.app.dynamicnback.ui.UIController
import com.evolved.automata.app.dynamicnback.ui.screens.ConfigureNewGame
import com.evolved.automata.app.dynamicnback.ui.screens.GameConfigScreen
import com.evolved.automata.app.dynamicnback.ui.screens.PlayGameScreen
import com.evolved.automata.app.dynamicnback.ui.screens.ReviewGameScreen
import com.evolved.automata.app.dynamicnback.ui.screens.ReviewLastGame
import com.evolved.automata.app.dynamicnback.ui.screens.SplashLoadingScreen
import com.evolved.automata.app.dynamicnback.ui.screens.StartNewGame
import com.evolved.automata.app.dynamicnback.ui.theme.DynamicNBackTheme

import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: NBackViewModel by viewModels()

    @Inject
    lateinit var host: HostInterface


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val useOldDesign = false
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        val initialRoute =
            if (!useOldDesign && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Code for Android 12 (API 31) and above
                splashScreen.setKeepOnScreenCondition { !viewModel.isReady }

                com.evolved.automata.app.dynamicnback.ui.screens.ConfigureNewGame(initialProfileToConfig = viewModel.lastConfiguredProfile.value.toJson())
            } else {
                // Fallback for older versions
                com.evolved.automata.app.dynamicnback.ui.screens.SplashLoadLastProfile
            }

        setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()


            DynamicNBackTheme {
                Scaffold(modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                    var expanded by remember { mutableStateOf(false) }
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = colorScheme.primary,
                            titleContentColor = colorScheme.onPrimary,
                            navigationIconContentColor = colorScheme.onPrimary
                        ),
                        title = {Text("Dynamic NBack", color = Color.White)},
                        actions = {
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More")
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Settings") },
                                    onClick = {

                                        expanded = false
                                        // Handle Settings click
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Debug") },
                                    onClick = {

                                        expanded = false
                                        // Handle Settings click

                                    }
                                )
                            }
                        })
                }
                    ) { innerPadding ->
                        val navigator = rememberNavController()

                        val uiController:UIController = object: UIController {
                            override fun startGame(profile:Profile) {
                                navigator.navigate(StartNewGame(initialGameConfig = profile.toJson()))
                            }

                            override fun configNewGame(profileToConfig:Profile) {
                                navigator.navigate(ConfigureNewGame(initialProfileToConfig = profileToConfig.toJson()))
                            }

                            override fun reviewLastGame(profileResultsToReview:Profile, lastScore:Float) {
                                navigator.navigate(ReviewLastGame(lastGameConfig = profileResultsToReview.toJson(), lastGameScore = lastScore))
                            }

                            override fun returnToNewGameConfig(profileToConfig:Profile) {
                                navigator.navigate(ConfigureNewGame(initialProfileToConfig = profileToConfig.toJson())) {
                                    popUpTo(0) {inclusive = true}
                                }
                            }

                            override fun returnToGameScreen(initialGameConfig:Profile) {
                                navigator.navigate(StartNewGame(initialGameConfig = initialGameConfig.toJson())) {
                                    //popUpTo(Ho)
                                }
                            }
                            override fun showSnackbar(message:String){
                                scope.launch { snackbarHostState. showSnackbar(message) }
                            }

                            override fun log(message:String){
                                host.logger.logInfo(message)
                            }

                        }

                        val background = Brush.verticalGradient(colors = listOf(Color(0xFFF6FFEC), Color(0xFFC5E8D2)))

                        NavHost(navController = navigator, startDestination = initialRoute) {
                            composable<com.evolved.automata.app.dynamicnback.ui.screens.SplashLoadLastProfile> {
                                SplashLoadingScreen(uiController, Modifier.padding(innerPadding).background(brush = background))
                            }
                            composable<ConfigureNewGame> { backStackEntry ->
                                val route:ConfigureNewGame = backStackEntry.toRoute<ConfigureNewGame>()
                                GameConfigScreen(uiController, Modifier.padding(innerPadding).background(brush = background).padding(10.dp), route.initialProfileToConfig.toProfile() )
                            }
                            composable<StartNewGame> { backStackEntry ->
                                val route:StartNewGame = backStackEntry.toRoute()
                                PlayGameScreen(uiController, Modifier.padding(innerPadding).background(brush = background).padding(10.dp), route.initialGameConfig.toProfile() )
                            }
                            composable<ReviewLastGame> { backStackEntry ->
                                val route:ReviewLastGame = backStackEntry.toRoute()
                                ReviewGameScreen(uiController, Modifier.padding(innerPadding).background(brush = background).padding(10.dp), route.lastGameConfig.toProfile(), route.lastGameScore)
                            }
                        }

                }
            }
        }
    }

    override fun onDestroy() {
        Log.i("o___o", "Stopped))))")
        super.onDestroy()
    }
}

fun String.toProfile(): Profile {
    return Gson().fromJson(this, Profile::class.java)
}

fun Profile.toJson(): String {
    return Gson().toJson(this)
}
