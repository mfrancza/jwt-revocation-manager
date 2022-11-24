package com.mfrancza.jwtrevocation.rules.conditions

/**
 * Generic type for conditions with different value types
 */
sealed interface Condition<V> {
    /**
     * @param value the value of the claim to check the condition against
     * @return true if the value from the claim mets the condition
     */
    fun isMet(value: V?): Boolean
}