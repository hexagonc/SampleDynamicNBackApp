package com.evolved.automata.app.dynamicnback.model.types

data class GameState (val lastResponseReward:Float = 0F, val totalScore:Float = 0F, val averageScore:Float = 0F, val trialIndex:Int = 0, val totalTrials:Int = 0, val stimulus: Stimulus = Stimulus(), val badgeRewarded:Boolean = false
)

