package com.mfrancza.jwtrevocation.rules

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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

    @Test
    fun testNotEquals() {
        val equalsConditionWithValue = DateTimeCondition(
            operation = DateTimeCondition.Operation.NotEquals,
            value = 1666928921
        )
        val equalsConditionWithNull = DateTimeCondition(
            operation = DateTimeCondition.Operation.NotEquals,
            value = null
        )
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

    @Test
    fun testBefore() {
        val equalsConditionWithValue = DateTimeCondition(
            operation = DateTimeCondition.Operation.Before,
            value = 1666928921
        )

        assertFailsWith(IllegalArgumentException::class, "Before should not accept a null value") {
            DateTimeCondition(
                operation = DateTimeCondition.Operation.Before,
                value = null
            )
        }

        assertFalse(
            equalsConditionWithValue.isMet(equalsConditionWithValue.value),
            "The same value does not meet it")
        assertTrue(equalsConditionWithValue.isMet(
            equalsConditionWithValue.value!! - 10),
            "A lower value does meet it")
        assertFalse(equalsConditionWithValue.isMet(
            equalsConditionWithValue.value!! + 10),
            "A higher value does not meet it")
        assertFalse(
            equalsConditionWithValue.isMet(null),
            "A null value does not meet the condition")
    }

    @Test
    fun testNotBefore() {
        val equalsConditionWithValue = DateTimeCondition(
            operation = DateTimeCondition.Operation.NotBefore,
            value = 1666928921
        )

        assertFailsWith(IllegalArgumentException::class, "NotBefore should not accept a null value") {
            DateTimeCondition(
                operation = DateTimeCondition.Operation.NotBefore,
                value = null
            )
        }

        assertTrue(
            equalsConditionWithValue.isMet(equalsConditionWithValue.value),
            "The same value does meet it")
        assertFalse(equalsConditionWithValue.isMet(
            equalsConditionWithValue.value!! - 10),
            "A lower value does not meet it")
        assertTrue(equalsConditionWithValue.isMet(
            equalsConditionWithValue.value!! + 10),
            "A higher value does meet it")
        assertFalse(
            equalsConditionWithValue.isMet(null),
            "A null value does not meet the condition")
    }

    @Test
    fun testAfter() {
        val equalsConditionWithValue = DateTimeCondition(
            operation = DateTimeCondition.Operation.After,
            value = 1666928921
        )

        assertFailsWith(IllegalArgumentException::class, "After should not accept a null value") {
            DateTimeCondition(
                operation = DateTimeCondition.Operation.After,
                value = null
            )
        }

        assertFalse(
            equalsConditionWithValue.isMet(equalsConditionWithValue.value),
            "The same value does not meet it")
        assertFalse(equalsConditionWithValue.isMet(
            equalsConditionWithValue.value!! - 10),
            "A lower value does not meet it")
        assertTrue(equalsConditionWithValue.isMet(
            equalsConditionWithValue.value!! + 10),
            "A higher value does meet it")
        assertFalse(
            equalsConditionWithValue.isMet(null),
            "A null value does not meet the condition")
    }

    @Test
    fun testNotAfter() {
        val equalsConditionWithValue = DateTimeCondition(
            operation = DateTimeCondition.Operation.NotAfter,
            value = 1666928921
        )

        assertFailsWith(IllegalArgumentException::class, "NotAfter should not accept a null value") {
            DateTimeCondition(
                operation = DateTimeCondition.Operation.NotAfter,
                value = null
            )
        }

        assertTrue(
            equalsConditionWithValue.isMet(equalsConditionWithValue.value),
            "The same value does meet it")
        assertTrue(equalsConditionWithValue.isMet(
            equalsConditionWithValue.value!! - 10),
            "A lower value does meet it")
        assertFalse(equalsConditionWithValue.isMet(
            equalsConditionWithValue.value!! + 10),
            "A higher value does not meet it")
        assertFalse(
            equalsConditionWithValue.isMet(null),
            "A null value does not meet the condition")
    }
}