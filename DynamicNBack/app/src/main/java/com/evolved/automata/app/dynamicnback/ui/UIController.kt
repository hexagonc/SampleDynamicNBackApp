package com.evolved.automata.app.dynamicnback.ui

import com.evolved.automata.app.dynamicnback.model.types.Profile

interface UIController {
    fun startGame(profile:Profile)
    fun configNewGame(profileToConfig:Profile)
    fun reviewLastGame(profileResultsToReview:Profile, lastScore:Float)
    fun returnToNewGameConfig(profileToConfig:Profile)
    fun returnToGameScreen(initialGameConfig:Profile)
    fun showSnackbar(message:String)
    fun log(message:String)
}