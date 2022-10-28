package com.mfrancza.jwtrevocationmanager.rules

import kotlinx.serialization.Serializable

@Serializable
data class Rule(
    val iss: StringCondition? = null,
    val sub: StringCondition? = null,
    val aud: StringCondition? = null,
    val exp: DateTimeCondition? = null,
    val nbf: DateTimeCondition? = null,
    val iat: DateTimeCondition? = null,
    val jti: StringCondition? = null
)