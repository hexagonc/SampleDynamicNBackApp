package com.evolved.automata.app.dynamicnback.model.types

data class TrialConfig (val stimulusId:Int,
                        val trialId:Int,
                        val truePositiveReward:Float, // When the current stimulus is the one N back and user clicked
                        val falsePositivePenalty:Float, // When the current stimulus is not the one N back but the user clicked anyway
                        val trueNegativeReward:Float, // When the current stimulus is not the one N steps away and the user doesn't click
                        val falseNegativePenalty:Float, // When the current stimulus is the one from N back but the user doesn't click
                        val onAttestationScoreDelta:Float,
                        val onAttestationTimeoutScoreDelta:Float,
                        val isSameAsNBack:Boolean

    )