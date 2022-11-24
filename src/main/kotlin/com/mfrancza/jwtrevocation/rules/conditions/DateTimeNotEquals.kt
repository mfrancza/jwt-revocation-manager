package com.mfrancza.jwtrevocation.rules.conditions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val DATE_TIME_NOT_EQUALS_OPERATION = "!="

@Serializable
@SerialName(DATE_TIME_NOT_EQUALS_OPERATION)
data class DateTimeNotEquals (
    val value: Long?
): DateTimeCondition() {
    override fun isMet(value: Long?): Boolean = (this.value != value)
}