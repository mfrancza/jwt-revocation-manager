package com.mfrancza.jwtrevocationmanager.rules

import kotlin.test.assertTrue
import kotlin.test.Test
import kotlin.test.assertFalse

class DateTimeConditionTest {

    @Test
    fun testEquals() {
        val equalsConditionWithValue = DateTimeCondition(
            operation = DateTimeCondition.Operation.Equals,
            value = 1666928921
        )
        val equalsConditionWithNull = DateTimeCondition(
            operation = DateTimeCondition.Operation.Equals,
            value = null
        )
        assertTrue(
            equalsConditionWithValue.isMet(equalsConditionWithValue.value),
            "The same value meets it")
        assertFalse(equalsConditionWithValue.isMet(
            1666928922),
            "A different value does not meet it")
        assertFalse(
            equalsConditionWithValue.isMet(null),
            "A null value does not meet a non-null value condition")
        assertFalse(
            equalsConditionWithNull.isMet(1666928921),
            "A non-null value does not meet a null value condition")
        assertTrue(
            equalsConditionWithNull.isMet(null),
            "A null value meets a null value condition")
    }
}