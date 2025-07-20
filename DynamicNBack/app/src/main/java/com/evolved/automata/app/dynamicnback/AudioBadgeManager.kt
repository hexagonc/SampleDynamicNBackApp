package com.evolved.automata.app.dynamicnback


import com.evolved.automata.app.dynamicnback.model.types.BadgeConfig

interface AudioBadgeManager {
    fun getAllAudioBadgeIds(): List<BadgeConfig>
    fun playAudioBadge(badgeKey:String, volume:Float = 1F): Boolean
}