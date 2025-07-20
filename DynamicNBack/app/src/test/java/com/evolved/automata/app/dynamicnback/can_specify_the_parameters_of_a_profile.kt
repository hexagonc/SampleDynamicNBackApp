package com.evolved.automata.app.dynamicnback

import com.evolved.automata.app.dynamicnback.repo.ProfileRepo
import com.evolved.automata.app.dynamicnback.model.types.Profile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import org.junit.Before
import org.junit.Test

class TestLogger(val tag:String = "<>___<>"): Logger {
    override fun child(tag: String): Logger {
        return TestLogger(tag)
    }

    override fun logInfo(tag: String, message: String) {
        val ts = showDatetimeMoment(System.currentTimeMillis())
        println("$ts INFO $tag ---- $message")
    }

    override fun logWarn(tag: String, message: String) {
        val ts = showDatetimeMoment(System.currentTimeMillis())
        println("$ts WARN $tag ---- $message")
    }

    override fun logError(tag: String, message: String) {
        val ts = showDatetimeMoment(System.currentTimeMillis())
        println("$ts **** ERROR **** $tag ---- $message")
    }

    override fun logInfo(message: String) {
        logInfo(tag, message)
    }

    override fun logWarn(message: String) {
        logWarn(tag, message)
    }

    override fun logError(message: String) {
        logError(tag, message)
    }

}

class TestProfileRepo(override val profiles: StateFlow<List<String>>) : ProfileRepo {
    override suspend fun loadProfile(name: String): Profile? {
        TODO("Not yet implemented")
    }

    override suspend fun updateActiveProfile(profile: Profile) {
        TODO("Not yet implemented")
    }

    override suspend fun getLastActiveProfile(): Profile {
        TODO("Not yet implemented")
    }

    override suspend fun reloadProfiles() {
        TODO("Not yet implemented")
    }

    override fun getEasyModeDefaultProfile(): Profile {
        TODO("Not yet implemented")
    }

}

class ConfiguringNewGame {

    @Before
    fun setUp(){
        println()
        println()
    }

    @Test
    fun can_create_a_game_config(){
        val testScope = CoroutineScope(Dispatchers.Unconfined)

    }


}