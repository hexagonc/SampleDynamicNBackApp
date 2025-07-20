package com.evolved.automata.app.dynamicnback.model.types

import kotlinx.serialization.Serializable


val PROFILE_VERSION:Int = 3
@Serializable
data class Profile(val name:String, val lastConfig: GameConfig, val experimentConfig: ExperimentConfig, val scoreHistory: List<ScoreHistory>, val version:Int = PROFILE_VERSION)

fun Profile.addScoreHistory(game: ScoreHistory):Profile {
    val newList: MutableList<ScoreHistory> = mutableListOf()
    newList.addAll(this.scoreHistory)
    newList.add(game)
    return this.copy(scoreHistory = newList)
}