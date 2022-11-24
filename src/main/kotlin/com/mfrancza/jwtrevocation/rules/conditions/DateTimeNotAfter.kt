package com.mfrancza.jwtrevocation.rules.conditions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val DATE_TIME_NOT_AFTER_OPERATION = "<="

@Serializable
@SerialName(DATE_TIME_NOT_AFTER_OPERATION)
data class DateTimeNotAfter (val value: Long): DateTimeCondition() {
    override fun isMet(value: Long?): Boolean = (value != null && value <= this.value)
}