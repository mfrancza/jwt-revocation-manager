package com.mfrancza.jwtrevocation.rules.conditions

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DateTimeBeforeClass {
    @Test
    fun testBefore() {
        val conditionWithValue = DateTimeBefore(1666928921)

        assertFalse(
            conditionWithValue.isMet(conditionWithValue.value),
            "The same value does not meet it")
        assertTrue(conditionWithValue.isMet(
            conditionWithValue.value - 10),
            "A lower value does meet it")
        assertFalse(conditionWithValue.isMet(
            conditionWithValue.value + 10),
            "A higher value does not meet it")
        assertFalse(
            conditionWithValue.isMet(null),
            "A null value does not meet the condition")
    }
}