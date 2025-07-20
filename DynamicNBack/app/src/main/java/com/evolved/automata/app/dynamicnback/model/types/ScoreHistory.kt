package com.evolved.automata.app.dynamicnback.model.types

import kotlinx.serialization.Serializable

@Serializable
data class ScoreHistory(val score:Float, val finishTS:Long)
