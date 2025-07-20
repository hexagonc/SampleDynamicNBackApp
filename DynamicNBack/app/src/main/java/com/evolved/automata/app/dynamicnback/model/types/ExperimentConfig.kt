package com.evolved.automata.app.dynamicnback.model.types

import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class ExperimentConfig (val numStimulusInstances:Int, val maxTrials:Int, val badgeConfig: BadgeConfig)

fun ExperimentConfig.sample (excluded:Int? = null): Int {
    if (excluded == null || excluded >= numStimulusInstances) {
        return Random.nextInt(numStimulusInstances)
    }
    else if (excluded >= 0) {
        val numBelow = excluded
        val numAbove = numStimulusInstances - excluded - 1

        val uppersetCutoff = 1 - numAbove*1.0/(numBelow + numAbove)

        val sampleAbove = Random.nextDouble() > uppersetCutoff
        val index = if (sampleAbove) {
            excluded + 1 + (numAbove*Random.nextDouble()).toInt()
        }
        else
            Random.nextInt(excluded)
        return index
    }
    else {
        throw IllegalStateException("Invalid index")
    }
}