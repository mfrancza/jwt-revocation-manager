package com.mfrancza.jwtrevocation.rules

import kotlinx.serialization.Serializable

@Serializable
data class StringCondition(
    override val operation: Operation,
    override val value: String?
) : Condition<String, StringCondition.Operation> {
    enum class Operation {
        Equals,
    }

    override fun isMet(value: String?) : Boolean =
        when(operation) {
            Operation.Equals -> value == this.value
        }
}