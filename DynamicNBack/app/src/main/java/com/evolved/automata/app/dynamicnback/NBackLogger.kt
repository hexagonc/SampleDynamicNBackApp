package com.evolved.automata.app.dynamicnback

import android.util.Log

class NBackLogger(val tag:String = "o___o"): Logger {
    override fun child(tag: String): Logger {
        return NBackLogger(tag)
    }

    override fun logInfo(tag: String, message: String) {
        Log.i(tag, message)
    }

    override fun logWarn(tag: String, message: String) {
        Log.w(tag, message)
    }

    override fun logError(tag: String, message: String) {
        Log.e(tag, message)
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