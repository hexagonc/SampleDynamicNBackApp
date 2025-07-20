package com.evolved.automata.app.dynamicnback.model.usecases

import com.evolved.automata.app.dynamicnback.model.types.CellType
import com.evolved.automata.app.dynamicnback.model.GameRunner
import com.evolved.automata.app.dynamicnback.model.types.GameState
import com.evolved.automata.app.dynamicnback.model.types.Profile
import com.evolved.automata.app.dynamicnback.model.types.Stimulus
import com.evolved.automata.app.dynamicnback.model.types.TrialConfig
import com.evolved.automata.app.dynamicnback.ui.UIController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PlayGameUsecase(val uiController: UIController, val gameRunner: GameRunner, val profile:Profile, val scope:CoroutineScope = CoroutineScope(
    Dispatchers.IO
)) {
    private var state: GameState = getInitialGameState()

    private var _gameState:MutableStateFlow<GameState> = MutableStateFlow(state)

    val gameState:StateFlow<GameState> = _gameState

    private var currentProfile:Profile = profile

    private var lastAttestationTS:Long = 0

    private var N:Int = profile.lastConfig.N

    private var impressionPeriod:Long = profile.lastConfig.impressionPeriodMS

    private var reviewPeriod:Long = profile.lastConfig.reviewPeriodMS

    private var attestationEarnsBadge = false

    private var returnedOut: Deferred<Float>? = null

    fun run(): Deferred<Float> {
        val out = scope.async(Dispatchers.Default) {
            var trials:MutableList<TrialConfig> = mutableListOf()

            var n = N
            delay(java.util.concurrent.TimeUnit.SECONDS.toMillis(5))

            val nunTrials = profile.experimentConfig.maxTrials

            var totalScore = 0.0F

            var averageScore = 0.0F

            log("Starting game runner:profile: ${profile.name}  score $totalScore, num trials: $nunTrials")

            for (trialId in 0 until nunTrials) {
                val trial = gameRunner.next(trials,profile.experimentConfig, n )
                // Add to the total score the attestation score of the trial
                val attestionScore = trial.onAttestationScoreDelta
                val timeoutScore = trial.onAttestationTimeoutScoreDelta

                attestationEarnsBadge = attestionScore > timeoutScore

                log("Trial: $trialId) results in stimulus: ${trial.stimulusId} and is repeated: ${trial.isSameAsNBack}")


                val newStimulusId = trial.stimulusId
                val newStimulus = Stimulus(false, newStimulusId, CellType.COLORS)
                val gameState: GameState = state.copy(totalScore = totalScore, averageScore = averageScore, trialIndex = (1 + trialId), stimulus = newStimulus, badgeRewarded = false)

                val impressionDuration = impressionPeriod
                log("Impression period actual: ${impressionDuration}, shoud be:  ${impressionPeriod}")
                val reviewDuration = reviewPeriod

                // Step 1: Present the stimulus
                updateState(gameState)

                var inactiveStimulus: Stimulus? = null

                val presentationTS = System.currentTimeMillis()
                val totalTimeout = presentationTS + impressionDuration + reviewDuration
                val scanDelayMS = 10L
                while (System.currentTimeMillis() < totalTimeout) {
                    delay(scanDelayMS)
                    if (System.currentTimeMillis() > presentationTS + impressionDuration){
                        if (inactiveStimulus == null) {
                            inactiveStimulus = newStimulus.copy(isInactive = true)
                            // hide the stimulus after the impression duration
                            updateState(state.copy(stimulus = inactiveStimulus, badgeRewarded = false))
                        }
                    }
                }
                // Step 2 update the score
                val userAttestedSameAsNBack = lastAttestationTS > presentationTS

                val trialReward = if (userAttestedSameAsNBack) attestionScore else timeoutScore
                totalScore += trialReward
                averageScore = ((averageScore * trialId) + trialReward)/(trialId + 1)
                val badge = trialReward > 0
                if (!badge) {
                    onBadgeMissed()
                }

                updateState(state.copy(lastResponseReward = trialReward, totalScore, averageScore, badgeRewarded = false))
                n = N
            }

            val gameConfig = profile.lastConfig.copy(N = N, impressionPeriodMS = impressionPeriod, reviewPeriodMS = reviewPeriod)
            currentProfile = profile.copy(lastConfig = gameConfig)
            totalScore
        }
        returnedOut = out
        return out
    }

    fun getInitialGameState(): GameState {
        val initialStimulus = Stimulus(isInactive = true)
        return GameState(totalScore = 0F, averageScore = 0.0F, trialIndex = 0, totalTrials = profile.experimentConfig.maxTrials, stimulus = initialStimulus)
    }

    fun log(message:String) {
        uiController.log(message)
    }



    fun onBadgeRewarded(){
        log("Badge earned")
    }

    fun onBadgeMissed() {
        log("Badge missed")
    }

    fun updateState(gamestate: GameState) {
        state = gamestate
        _gameState.value = state
        log("Updated state: ${state}")
    }

    fun quit() {
        returnedOut?.cancel()
    }

    fun onAttestation(ts:Long) {
        lastAttestationTS = ts
        log("Player attests same as $N back")
        if (attestationEarnsBadge) {
            onBadgeRewarded()
            updateState(state.copy(badgeRewarded = true))
        }
    }

    fun onNewN(n:Int) {
        this.N = n
        log("Player updated N to $n")
        val gameConfig = profile.lastConfig.copy(N = N, impressionPeriodMS = impressionPeriod, reviewPeriodMS = reviewPeriod)
        currentProfile = profile.copy(lastConfig = gameConfig)
    }

    fun onUpdatedImpressionPeriod(imp:Long) {

        impressionPeriod = imp
        log("Player updated impression period to to $imp")
        val gameConfig = profile.lastConfig.copy(N = N, impressionPeriodMS = impressionPeriod, reviewPeriodMS = reviewPeriod)
        currentProfile = profile.copy(lastConfig = gameConfig)
    }

    fun onUpdatedReviewPeriod(review:Long) {
        reviewPeriod = review
        log("Player updated review period to to $review")
        val gameConfig = profile.lastConfig.copy(N = N, impressionPeriodMS = impressionPeriod, reviewPeriodMS = reviewPeriod)
        currentProfile = profile.copy(lastConfig = gameConfig)
    }
}