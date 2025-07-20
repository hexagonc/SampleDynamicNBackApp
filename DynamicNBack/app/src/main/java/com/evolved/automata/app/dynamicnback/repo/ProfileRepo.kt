package com.evolved.automata.app.dynamicnback.repo

import com.evolved.automata.app.dynamicnback.model.types.Profile
import kotlinx.coroutines.flow.StateFlow

interface ProfileRepo {
    val profiles:StateFlow<List<String>>

    suspend fun loadProfile(name:String): Profile?

    suspend fun updateActiveProfile(profile:Profile)

    suspend fun getLastActiveProfile(): Profile?

    suspend fun reloadProfiles()

    fun getEasyModeDefaultProfile():Profile

    fun upgrade(oldProfile:Profile): Profile {
        return oldProfile
    }
}