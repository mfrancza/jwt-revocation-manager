package com.mfrancza.jwtrevocation.rules

import com.mfrancza.jwtrevocation.rules.conditions.StringCondition
import com.mfrancza.jwtrevocation.rules.conditions.StringEquals
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StringConditionTest {
    @Test
    fun testEquals() {
        val equalsConditionWithValue = StringEquals(
            value = "BadValue"
        )
        val equalsConditionWithNull = StringEquals(
            value = null
        )
        assertTrue(
            equalsConditionWithValue.isMet(equalsConditionWithValue.value),
            "The same value meets it")
        assertFalse(equalsConditionWithValue.isMet(
            "GoodValue"),
            "A different value does not meet it")
        assertFalse(
            equalsConditionWithValue.isMet(null),
            "A null value does not meet a non-null value condition")
        assertFalse(
            equalsConditionWithNull.isMet("AnyValue"),
            "A non-null value does not meet a null value condition")
        assertTrue(
            equalsConditionWithNull.isMet(null),
            "A null value meets a null value condition")
    }
}