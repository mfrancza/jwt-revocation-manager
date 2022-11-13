package com.mfrancza.jwtrevocation.rules

import kotlinx.serialization.Serializable

@Serializable
data class RuleSet(
    val rules: List<Rule>,
    val timestamp: Long
)