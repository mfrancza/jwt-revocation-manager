package com.mfrancza.jwtrevocation.rules

import kotlinx.serialization.Serializable

@Serializable
data class DateTimeCondition (
    override val operation: Operation,
    override val value: Long?) : Condition<Long, DateTimeCondition.Operation>
{

    init {
        if (value == null && !operation.acceptsNull) throw IllegalArgumentException("$operation requires a non-null value")
    }

    enum class Operation(val acceptsNull: Boolean) {
        Equals(true),
        NotEquals(true),
        Before(false),
        NotBefore(false),
        After(false),
        NotAfter(false)
    }

    override fun isMet(value: Long?) : Boolean =
        when(operation) {
            Operation.Equals -> value == this.value
            Operation.NotEquals -> value != this.value
            Operation.Before -> value != null && value < this.value!!
            Operation.NotBefore -> value != null && value >= this.value!!
            Operation.After -> value != null && value > this.value!!
            Operation.NotAfter -> value != null && value <= this.value!!
        }
}