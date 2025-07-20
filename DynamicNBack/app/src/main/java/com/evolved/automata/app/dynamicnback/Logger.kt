package com.evolved.automata.app.dynamicnback

interface Logger {
    fun child(tag:String): Logger

    fun logInfo(tag:String, message:String)
    fun logWarn(tag:String, message:String)
    fun logError(tag:String, message:String)
    fun logInfo(message:String)
    fun logWarn( message:String)
    fun logError(message:String)
}