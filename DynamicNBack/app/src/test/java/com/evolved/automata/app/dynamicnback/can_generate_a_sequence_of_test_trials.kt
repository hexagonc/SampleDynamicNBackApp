package com.evolved.automata.app.dynamicnback

import com.evolved.automata.app.dynamicnback.model.types.ExperimentConfig
import com.evolved.automata.app.dynamicnback.model.GameRunner
import com.evolved.automata.app.dynamicnback.model.types.TrialConfig
import com.evolved.automata.app.dynamicnback.model.types.BadgeConfig
import com.evolved.automata.app.dynamicnback.model.types.BadgeType
import org.junit.Before
import org.junit.Test

class GamePlaying {

    @Before
    fun setUp(){
        println()
        println()
    }

    @Test
    fun can_create_a_game_config(){
        val expConfig: ExperimentConfig = ExperimentConfig(81, 30, BadgeConfig(BadgeType.VISUAL, "", ""))
        val N = 3

        val history:MutableList<TrialConfig> = mutableListOf()
        val runner = GameRunner()

        for (i in 1..20) {
            val trialConfig:TrialConfig = runner.next(history, expConfig, N)
            println("Trial: $i) results in stimulus: ${trialConfig.stimulusId} and is repeated: ${trialConfig.isSameAsNBack}")
        }
    }
}