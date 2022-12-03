package com.mfrancza.jwtrevocation.rules.conditions

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class DateTimeEqualsTest {
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

        assertEquals(equalsConditionWithValue, Json.decodeFromString(Json.encodeToString(equalsConditionWithValue)), "Serializing and deserializing an instance should result in the same value")
    }
}