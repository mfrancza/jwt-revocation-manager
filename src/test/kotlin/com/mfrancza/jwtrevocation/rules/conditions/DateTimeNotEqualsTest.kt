package com.mfrancza.jwtrevocation.rules.conditions

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DateTimeNotEqualsTest {
    @Test
    fun testNotEquals() {
        val equalsConditionWithValue = DateTimeNotEquals(1666928921)
        val equalsConditionWithNull = DateTimeNotEquals(null)
        assertFalse(
            equalsConditionWithValue.isMet(equalsConditionWithValue.value),
            "The same value does not meet it")
        assertTrue(equalsConditionWithValue.isMet(
            1666928922),
            "A different value does meet it")
        assertTrue(
            equalsConditionWithValue.isMet(null),
            "A null value does meet a non-null value condition")
        assertTrue(
            equalsConditionWithNull.isMet(1666928921),
            "A non-null value does meet a null value condition")
        assertFalse(
            equalsConditionWithNull.isMet(null),
            "A null value does not meet a null value condition")
    }
}