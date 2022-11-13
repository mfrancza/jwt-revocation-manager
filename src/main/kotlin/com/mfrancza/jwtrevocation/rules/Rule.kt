package com.mfrancza.jwtrevocation.rules

import kotlinx.serialization.Serializable

/**
 * Represents a JWT revocation rule
 * the conditions for each claim have an "and" relationship and the rule must have at least one condition
 *
 * The claims that conditions may be specified for are described in https://www.rfc-editor.org/rfc/rfc7519#section-4.1
 */
@Serializable
data class Rule(
    /**
     * The ID of the rule; assigned when the rule is persisted
     */
    val ruleId: String? = null,
    /**
     * The expiration time of the rule, specified as Epoch seconds
     */
    val ruleExpires: Long,
    /**
     * The condition for the iss claim
     */
    val iss: StringCondition? = null,
    /**
     * The condition for the sub claim
     */
    val sub: StringCondition? = null,
    /**
     * The condition for the aud claim
     */
    val aud: StringCondition? = null,
    /**
     * The condition for the exp claim
     */
    val exp: DateTimeCondition? = null,
    /**
     * The condition for the nbf claim
     */
    val nbf: DateTimeCondition? = null,
    /**
     * The condition for the iat claim
     */
    val iat: DateTimeCondition? = null,
    /**
     * The condition for the jti claim
     */
    val jti: StringCondition? = null
) {
    init {
        //validate that at least one condition is specified
        if (
            iss == null &&
            sub == null &&
            aud == null &&
            exp == null &&
            iat == null &&
            jti == null
        ) {
            throw IllegalArgumentException("A condition must be specified for at least one claim")
        }
    }
}