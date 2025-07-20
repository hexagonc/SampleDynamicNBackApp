package com.evolved.automata.app.dynamicnback.repo

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.evolved.automata.app.dynamicnback.deserializeDataClassString
import com.evolved.automata.app.dynamicnback.illegalIfNull
import com.evolved.automata.app.dynamicnback.model.types.ExperimentConfig
import com.evolved.automata.app.dynamicnback.model.types.GameConfig
import com.evolved.automata.app.dynamicnback.model.types.PROFILE_VERSION
import com.evolved.automata.app.dynamicnback.model.types.Profile
import com.evolved.automata.app.dynamicnback.model.types.BadgeConfig
import com.evolved.automata.app.dynamicnback.model.types.BadgeType
import com.evolved.automata.app.dynamicnback.serializeDataClassInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NBackProfileRepo(context: Context): ProfileRepo {

    // Get rid of this eventually
    val activeProfileKey = "Active"

    val activeProfileNameKey = "LastActiveProfileName"

    val allProfilesKey = "Proflies"

    val profileKeySuffix = "Prof_"

    val prefs: SharedPreferences = context.getSharedPreferences("INTERNAL", Context.MODE_PRIVATE)

    val versionUpgradeMap:Map<Int, (Profile)-> Profile>

    override fun getEasyModeDefaultProfile(): Profile {
        return getInitialProfile()
    }

    var _profiles: MutableStateFlow<List<String>> = MutableStateFlow(listOf())
    override val profiles: StateFlow<List<String>> = _profiles.asStateFlow()

    override suspend fun loadProfile(name: String): Profile? {
        val key_name = "$profileKeySuffix${name}"
        if (prefs.contains(key_name)) {
            val serialized =  prefs.getString(key_name, null).illegalIfNull()
            val original = deserializeDataClassString(
                serialized, Profile::class.java
            )
            prefs.edit() {
                putString(activeProfileNameKey, name)

                putString(activeProfileKey, serialized)
            }
            return original
        }
        else
            return null
    }

    init {
        versionUpgradeMap = mapOf(
            0 to { old: Profile -> old.copy(version = PROFILE_VERSION, scoreHistory = mutableListOf())},
            2 to { old: Profile -> old.copy(version = PROFILE_VERSION, experimentConfig = old.experimentConfig.copy(badgeConfig = BadgeConfig(
                BadgeType.VISUAL, name = "good job!!", contentKey = "ic_thumb_up_black_24p"
            )
            ))})


    }

    override suspend fun reloadProfiles() {
        val allProfileNames = prefs.getStringSet(allProfilesKey, setOf<String>()).illegalIfNull()
        _profiles.value = allProfileNames.toList()

        // Cleanup code that will be removed in next app major version when activeProfileKey is removed
        if (prefs.contains(activeProfileKey)) {
            val original = deserializeDataClassString(
                prefs.getString(activeProfileKey, null).illegalIfNull(), Profile::class.java
            )


            val name = original.name
            val key_name = "$profileKeySuffix${name}"

            if (!prefs.contains(key_name)) {
                prefs.edit(){putString(key_name, prefs.getString(activeProfileKey, null).illegalIfNull())}
            }
        }

    }

    override suspend fun updateActiveProfile(profile: Profile) {
        val serialized = serializeDataClassInstance(profile)
        val name = profile.name
        val key_name = "$profileKeySuffix${name}"

        prefs.edit() {
            putString(activeProfileKey, serialized);
            putString(activeProfileNameKey, name)
            putString(key_name, serialized);
        }
        updateProfileNameList(name)
    }

    private fun updateProfileNameList(name:String) {
        val allProfileNames = prefs.getStringSet(allProfilesKey, setOf<String>()).illegalIfNull()
        val update = !allProfileNames.contains(name)
        val s:MutableSet<String> = mutableSetOf()
        s.addAll(allProfileNames)
        s.add(name)
        prefs.edit { putStringSet(allProfilesKey, s) }

        if (update) {
            _profiles.value = s.toList()
        }
    }

    override suspend fun getLastActiveProfile(): Profile? {
        if (prefs.contains(activeProfileNameKey)) {
            val lastProfile = prefs.getString(activeProfileNameKey, null).illegalIfNull()
            updateProfileNameList(lastProfile)
            return loadProfile(lastProfile)
        }
        else if (prefs.contains(activeProfileKey)) {
            val original = deserializeDataClassString(
                prefs.getString(activeProfileKey, null).illegalIfNull(), Profile::class.java
            )
            updateProfileNameList(original.name)
            return upgrade(original)
        }
        else
            return null
    }

    override fun upgrade(oldProfile: Profile): Profile {
        val versions = versionUpgradeMap
        if (oldProfile.version == 0) {
            return oldProfile.copy(version = PROFILE_VERSION, scoreHistory = mutableListOf())
        }
        else {
            var versionToUprade = oldProfile
            while (versions.containsKey(versionToUprade.version) && versionToUprade.version < PROFILE_VERSION) {
                versionToUprade = versions[oldProfile.version].illegalIfNull()(oldProfile)
            }
            if (versionToUprade.version == PROFILE_VERSION)
                return versionToUprade
            else
                throw IllegalStateException("Profile object has no version")
        }
    }

    fun getInitialGameConfig(): GameConfig {
        return GameConfig(N = 3, impressionPeriodMS = 1000, reviewPeriodMS = 0)
    }

    fun getInitialExperimentConfig(): ExperimentConfig {
        return ExperimentConfig(9 * 9, 30, BadgeConfig(BadgeType.VISUAL, "Good job!", "thumbs up"))
    }

    fun getInitialProfile(): Profile {
        return Profile(
            "default",
            lastConfig = getInitialGameConfig(),
            experimentConfig = getInitialExperimentConfig(),
            scoreHistory = mutableListOf()
        )
    }
}