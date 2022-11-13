package com.mfrancza.jwtrevocation.rules

import kotlinx.serialization.Serializable

@Serializable
data class DateTimeCondition (val operation: Operation, val value: Long?){

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

    fun isMet(comparisonValue: Long?) : Boolean =
        when(operation) {
            Operation.Equals -> comparisonValue == value
            Operation.NotEquals -> comparisonValue != value
            Operation.Before -> comparisonValue != null && comparisonValue < value!!
            Operation.NotBefore -> comparisonValue != null && comparisonValue >= value!!
            Operation.After -> comparisonValue != null && comparisonValue > value!!
            Operation.NotAfter -> comparisonValue != null && comparisonValue <= value!!
        }
}