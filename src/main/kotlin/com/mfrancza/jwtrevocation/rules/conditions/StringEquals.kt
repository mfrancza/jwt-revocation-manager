package com.mfrancza.jwtrevocation.rules.conditions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val STRING_EQUALS_OPERATION = "="

@Serializable
@SerialName(STRING_EQUALS_OPERATION)
data class StringEquals (val value: String?) : StringCondition() {
    override fun isMet(value: String?): Boolean = (this.value == value)
}