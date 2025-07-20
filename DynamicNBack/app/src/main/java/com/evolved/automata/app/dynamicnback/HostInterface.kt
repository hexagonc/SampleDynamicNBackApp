package com.evolved.automata.app.dynamicnback

import kotlinx.coroutines.CoroutineScope

interface HostInterface {
    val topScope: CoroutineScope
    val logger: Logger

    fun playRingtone(uri:String)
}