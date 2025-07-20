package com.evolved.automata.app.dynamicnback.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evolved.automata.app.dynamicnback.HostInterface
import com.evolved.automata.app.dynamicnback.repo.ProfileRepo
import com.evolved.automata.app.dynamicnback.illegalIfNull
import com.evolved.automata.app.dynamicnback.model.GameRunner
import com.evolved.automata.app.dynamicnback.model.types.Profile
import com.evolved.automata.app.dynamicnback.model.usecases.PlayGameUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class NBackViewModel @Inject constructor (val hostInterface: HostInterface, val profileRepo: ProfileRepo): ViewModel() {



    val profileNames:StateFlow<List<String>> = profileRepo.profiles


    var isReady:Boolean = false
    private set

    private var _ready: CompletableDeferred<Boolean> = CompletableDeferred(false)
    val isReadyAsync: Deferred<Boolean> = _ready

    init {
        viewModelScope.launch(Dispatchers.Default) {
            profileRepo.reloadProfiles()
            val initialProfile:Profile = profileRepo.getLastActiveProfile()?:profileRepo.getEasyModeDefaultProfile()
            _lastConfiguredProfile = MutableStateFlow<Profile>(initialProfile)
            isReady = true
            _ready.complete(true)
        }
    }

    private var _lastConfiguredProfile:MutableStateFlow<Profile> = MutableStateFlow(profileRepo.getEasyModeDefaultProfile())
    val lastConfiguredProfile:StateFlow<Profile>
        get() = _lastConfiguredProfile

    fun playBadgeAlert(uri:String) {
        hostInterface.playRingtone(uri)
    }

    fun fetchProfileByName(nameMustExists:String): Deferred<Profile> {
        return viewModelScope.async(Dispatchers.IO) {
            profileRepo.loadProfile(nameMustExists).illegalIfNull()
        }
    }

    fun saveProfile(profile:Profile): Deferred<Boolean> {
        return viewModelScope.async(Dispatchers.IO) {
            profileRepo.updateActiveProfile(profile)
            true
        }
    }

    fun getPlayGameUsecase(uiController: UIController, profile: Profile): PlayGameUsecase {
        return PlayGameUsecase(uiController, getRandomGameRunner(), profile, viewModelScope)
    }

    fun getRandomGameRunner():GameRunner{
        return GameRunner(repeatAssessmentMultiplier = Random.nextInt(2, 6))
    }

}