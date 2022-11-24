package com.mfrancza.jwtrevocation.rules

import com.mfrancza.jwtrevocation.rules.conditions.Condition
import com.mfrancza.jwtrevocation.rules.conditions.DateTimeCondition
import com.mfrancza.jwtrevocation.rules.conditions.StringCondition
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
     * The conditions for the iss claim
     */
    val iss: List<StringCondition> = emptyList(),
    /**
     * The conditions for the sub claim
     */
    val sub: List<StringCondition> = emptyList(),
    /**
     * The condition for the aud claim
     */
    val aud: List<StringCondition> = emptyList(),
    /**
     * The condition for the exp claim
     */
    val exp: List<DateTimeCondition> = emptyList(),
    /**
     * The condition for the nbf claim
     */
    val nbf: List<DateTimeCondition> = emptyList(),
    /**
     * The condition for the iat claim
     */
    val iat: List<DateTimeCondition> = emptyList(),
    /**
     * The condition for the jti claim
     */
    val jti: List<StringCondition> = emptyList()
) {
    init {
        //validate that at least one condition is specified
        if (
            iss.isEmpty() &&
            sub.isEmpty() &&
            aud.isEmpty() &&
            exp.isEmpty() &&
            nbf.isEmpty() &&
            iat.isEmpty() &&
            jti.isEmpty()
        ) {
            throw IllegalArgumentException("A condition must be specified for at least one claim")
        }
    }

    fun isNotMetBy(claimsSource: ClaimsSource): Condition<*>? =
        iss.firstOrNull { !it.isMet(claimsSource.iss()) }
            ?: sub.firstOrNull { !it.isMet(claimsSource.sub()) }
            ?: aud.firstOrNull { !it.isMet(claimsSource.aud()) }
            ?: exp.firstOrNull { !it.isMet(claimsSource.exp()) }
            ?: nbf.firstOrNull { !it.isMet(claimsSource.nbf()) }
            ?: iat.firstOrNull { !it.isMet(claimsSource.iat()) }
            ?: jti.firstOrNull { !it.isMet(claimsSource.jti()) }

    fun isMet(claimsSource: ClaimsSource): Boolean = (isNotMetBy(claimsSource) == null)
}