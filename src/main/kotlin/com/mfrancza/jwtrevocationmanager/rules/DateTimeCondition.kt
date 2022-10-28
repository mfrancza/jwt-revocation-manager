package com.mfrancza.jwtrevocationmanager.rules

import kotlinx.serialization.Serializable

@Serializable
data class DateTimeCondition (
    val operation: Operation,
    val value: Long?
    ){
        enum class Operation {
            Equals
        }

        fun isMet(comparisonValue: Long?) : Boolean =
            when(operation) {
                Operation.Equals -> comparisonValue == value
            }
    }