package com.evolved.automata.app.dynamicnback.model.types

import kotlinx.serialization.Serializable

@Serializable
data class BadgeConfig(val badgeType: BadgeType, val contentKey:String, val name:String)
