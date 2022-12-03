package com.mfrancza.jwtrevocation.rules.conditions

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DateTimeAfterTest {
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
}