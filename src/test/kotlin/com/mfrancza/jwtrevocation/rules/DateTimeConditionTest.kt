package com.mfrancza.jwtrevocation.rules

import com.mfrancza.jwtrevocation.rules.conditions.DateTimeAfter
import com.mfrancza.jwtrevocation.rules.conditions.DateTimeBefore
import com.mfrancza.jwtrevocation.rules.conditions.DateTimeCondition
import com.mfrancza.jwtrevocation.rules.conditions.DateTimeEquals
import com.mfrancza.jwtrevocation.rules.conditions.DateTimeNotAfter
import com.mfrancza.jwtrevocation.rules.conditions.DateTimeNotBefore
import com.mfrancza.jwtrevocation.rules.conditions.DateTimeNotEquals
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DateTimeConditionTest {

    @Test
    fun testEquals() {
        val equalsConditionWithValue = DateTimeEquals(1666928921)
        val equalsConditionWithNull = DateTimeEquals(null)
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
    fun testEqualsSerialization() {
        val startingJson = "{\"operation\":\"=\",\"value\":1666928921}"

        val equalsConditionWithValue = Json.decodeFromString<DateTimeCondition>(startingJson)

        assertIs<DateTimeEquals>(equalsConditionWithValue, "The JSON should deserialize as an EqualsCondition")
        assertEquals(1666928921, equalsConditionWithValue.value)

        assertEquals(equalsConditionWithValue, Json.decodeFromString(Json.encodeToString(equalsConditionWithValue)), "")
    }

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

    @Test
    fun testNotBefore() {
        val condition = DateTimeNotBefore(1666928921)

        assertTrue(
            condition.isMet(condition.value),
            "The same value does meet it")
        assertFalse(condition.isMet(
            condition.value - 10),
            "A lower value does not meet it")
        assertTrue(condition.isMet(
            condition.value + 10),
            "A higher value does meet it")
        assertFalse(
            condition.isMet(null),
            "A null value does not meet the condition")
    }

    @Test
    fun testAfter() {
        val condition = DateTimeAfter(1666928921)

        assertFalse(
            condition.isMet(condition.value),
            "The same value does not meet it")
        assertFalse(condition.isMet(
            condition.value - 10),
            "A lower value does not meet it")
        assertTrue(condition.isMet(
            condition.value + 10),
            "A higher value does meet it")
        assertFalse(
            condition.isMet(null),
            "A null value does not meet the condition")
    }

    @Test
    fun testNotAfter() {
        val condition = DateTimeNotAfter(1666928921)

        assertTrue(
            condition.isMet(condition.value),
            "The same value does meet it")
        assertTrue(condition.isMet(
            condition.value - 10),
            "A lower value does meet it")
        assertFalse(condition.isMet(
            condition.value + 10),
            "A higher value does not meet it")
        assertFalse(
            condition.isMet(null),
            "A null value does not meet the condition")
    }
}