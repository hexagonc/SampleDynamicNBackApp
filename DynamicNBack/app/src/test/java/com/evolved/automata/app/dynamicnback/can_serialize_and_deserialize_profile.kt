package com.evolved.automata.app.dynamicnback

import com.evolved.automata.app.dynamicnback.model.types.ExperimentConfig
import com.evolved.automata.app.dynamicnback.model.types.GameConfig
import com.evolved.automata.app.dynamicnback.model.types.Profile
import com.evolved.automata.app.dynamicnback.model.types.BadgeConfig
import com.evolved.automata.app.dynamicnback.model.types.BadgeType
import com.google.gson.Gson
import org.junit.Before
import org.junit.Test

class TestingSerialization {

    @Before
    fun setUp(){
        println()
        println()
    }

    @Test
    fun can_serialize_deserialize_profile(){
        val profile = Profile(name = "Jack", lastConfig = GameConfig(3, 3000, 0), scoreHistory = listOf(), experimentConfig = ExperimentConfig(2, 30, badgeConfig = BadgeConfig(badgeType = BadgeType.VISUAL, "", "")))

        val serialized = Gson().toJson(profile)

        val newProfile = Gson().fromJson<Profile>(serialized, Profile::class.java)

        println("New profile: $newProfile")
    }
}