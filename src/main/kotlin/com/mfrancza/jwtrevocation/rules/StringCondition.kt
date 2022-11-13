package com.mfrancza.jwtrevocation.rules

import kotlinx.serialization.Serializable

@Serializable
data class StringCondition (
    val operation: Operation,
    val value: String
){
    enum class Operation {
        Equals,
    }
}