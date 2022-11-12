package com.mfrancza.jwtrevocationmanager.rules

import kotlinx.serialization.Serializable

@Serializable
data class RuleSet(
    val rules: List<Rule>,
    val timestamp: Long
)