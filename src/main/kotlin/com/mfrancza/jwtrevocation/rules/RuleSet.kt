package com.mfrancza.jwtrevocation.rules

import kotlinx.serialization.Serializable

/**
 * The set of rules to apply to determine if a JWT has been revoked
 */
@Serializable
data class RuleSet(
    val rules: List<Rule>,
    val timestamp: Long
) {
    /**
     * @param claimsSource a source of claims to evaluate for the rules
     * @return a Rule which the claims meet or null if no rule is met
     */
    fun isMetBy(claimsSource: ClaimsSource) : Rule? = rules.firstOrNull { it.isMet(claimsSource) }

    /**
     * @param claimsSource a source of claims to evaluate for the rules
     * @return true if at least one rule is met by the claims
     */
    fun isMet(claimsSource: ClaimsSource) : Boolean = (isMetBy(claimsSource) != null)
}
