package com.mfrancza.jwtrevocationmanager.rules

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