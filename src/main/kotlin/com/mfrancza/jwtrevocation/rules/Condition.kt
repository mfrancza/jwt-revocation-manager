package com.mfrancza.jwtrevocation.rules

/**
 * Generic type for conditions with different value types
 */
sealed interface Condition<V, O> {
    val value: V?
    val operation: O

    /**
     * @param value the value of the claim to check the condition against
     * @return true if the value from the claim mets the condition
     */
    fun isMet(value: V?): Boolean
}