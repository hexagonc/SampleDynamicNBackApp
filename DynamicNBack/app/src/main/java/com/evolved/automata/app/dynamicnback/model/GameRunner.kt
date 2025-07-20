package com.evolved.automata.app.dynamicnback.model

import com.evolved.automata.app.dynamicnback.model.types.ExperimentConfig
import com.evolved.automata.app.dynamicnback.model.types.TrialConfig
import com.evolved.automata.app.dynamicnback.model.types.sample
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class GameRunner(val repeatAssessmentMultiplier:Int = 2, val minMemorizationAssessmentLength:Int = 6) {
    fun next(history:MutableList<TrialConfig>, expConfig: ExperimentConfig, N:Int): TrialConfig {
        if (history.size < N) {
            val nextStimulus = expConfig.sample()
            val trialId = history.size

            val nextConfig: TrialConfig = TrialConfig(
                stimulusId = nextStimulus,
                trialId = trialId,
                truePositiveReward = 0.0F,
                trueNegativeReward = 0.0F,
                falsePositivePenalty = 0.0F,
                falseNegativePenalty = 0.0F,
                onAttestationScoreDelta = 0.0F,
                onAttestationTimeoutScoreDelta = 0.0F,
                isSameAsNBack = false
            )

            history.add(nextConfig)
            return nextConfig
        }
        else {
            val historyLength = history.size
            val nBack: TrialConfig = history[historyLength - N]

            val distinctStimuli:MutableSet<Int> = mutableSetOf()
            var repeatCount = 0
            val repeatProbAssessmentInterval = max(minMemorizationAssessmentLength, N*repeatAssessmentMultiplier)
            val cutoff = min(historyLength-1, repeatProbAssessmentInterval)
            for (i in 0..cutoff){
                if (history[historyLength - i - 1].isSameAsNBack)
                    repeatCount++
                distinctStimuli.add(history[historyLength - i - 1].stimulusId)
            }

            val numStimuliToRemember = distinctStimuli.size

            val maxRepeatProb = 1.0

            val baseProb = min(maxRepeatProb, 1.0*numStimuliToRemember/repeatProbAssessmentInterval)

            // Should the user be tested on the item N back?
            val repeatProbability = (baseProb/(1 + repeatCount)).toFloat()

            val truePositiveScoreDelta = 1 - repeatProbability // If you expect
            val trueNegativeScoreDelta = repeatProbability
            val falsePositiveScorePenalty = repeatProbability - 1
            val falseNegativeScorePenalty = -repeatProbability

            val shouldRepeat = Random.nextDouble() < repeatProbability

            val trialId = history.size
            if (shouldRepeat) {
                val nextStimulusId = nBack.stimulusId
                // Attestation now would be a truePositive so the reward for attetestion is potentlly
                // truePositiveScoreDelta

                // Not attesting is a falseNegative so allowing a timeout gets punished by falseNegativeScorePenalty

                val nextConfig: TrialConfig = TrialConfig(
                    stimulusId = nextStimulusId,
                    trialId = trialId,
                    truePositiveReward = truePositiveScoreDelta,
                    trueNegativeReward = trueNegativeScoreDelta,
                    falsePositivePenalty = falsePositiveScorePenalty,
                    falseNegativePenalty = falseNegativeScorePenalty,
                    onAttestationScoreDelta = truePositiveScoreDelta,
                    onAttestationTimeoutScoreDelta = falseNegativeScorePenalty,
                    isSameAsNBack = shouldRepeat
                )
                history.add(nextConfig)
                return nextConfig
            }
            else {
                val nextStimulusId = expConfig.sample(nBack.stimulusId) // Chose a stimulus other than the one n back
                // Attestation now would be a falsePositive so the penalty for attetestion is falsePositiveScorePenalty
                // Not attesting is a trueNegative so allowing a timeout gets rewarded by trueNegativeScoreDelta

                val nextConfig: TrialConfig = TrialConfig(
                    stimulusId = nextStimulusId,
                    trialId = trialId,
                    truePositiveReward = truePositiveScoreDelta,
                    trueNegativeReward = trueNegativeScoreDelta,
                    falsePositivePenalty = falsePositiveScorePenalty,
                    falseNegativePenalty = falseNegativeScorePenalty,
                    onAttestationScoreDelta = falsePositiveScorePenalty,
                    onAttestationTimeoutScoreDelta = trueNegativeScoreDelta,
                    isSameAsNBack = shouldRepeat
                )
                history.add(nextConfig)
                return nextConfig
            }
        }

    }
}