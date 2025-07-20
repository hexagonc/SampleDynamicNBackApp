package com.evolved.automata.app.dynamicnback.model.types

import kotlinx.serialization.Serializable

@Serializable
data class GameConfig (val N:Int, val impressionPeriodMS:Long, val reviewPeriodMS:Long)