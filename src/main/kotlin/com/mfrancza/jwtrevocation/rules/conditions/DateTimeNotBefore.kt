package com.mfrancza.jwtrevocation.rules.conditions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val DATE_TIME_NOT_BEFORE_OPERATION = ">="

@Serializable
@SerialName(DATE_TIME_NOT_BEFORE_OPERATION)
data class DateTimeNotBefore (val value: Long): DateTimeCondition() {
    override fun isMet(value: Long?): Boolean = (value != null && value >= this.value)
}