package com.mfrancza.jwtrevocationmanager.rules

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Rule
 */
@Serializable
data class Rule(
    val ruleId: String = UUID.randomUUID().toString(),
    val ruleExpires: Long,
    val iss: StringCondition? = null,
    val sub: StringCondition? = null,
    val aud: StringCondition? = null,
    val exp: DateTimeCondition? = null,
    val nbf: DateTimeCondition? = null,
    val iat: DateTimeCondition? = null,
    val jti: StringCondition? = null
)