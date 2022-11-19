package com.mfrancza.jwtrevocation.rules

import io.ktor.server.sessions.autoSerializerOf
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RuleSetTest {

    @Test
    fun testIsMetByNoRuleIsMet() {
        val ruleSet = RuleSet(
            rules = listOf(
                Rule(
                    ruleExpires = Instant.now().epochSecond + 60,
                    exp = listOf(
                        DateTimeCondition(
                            DateTimeCondition.Operation.After,
                            Instant.now().plus(1, ChronoUnit.HOURS).epochSecond
                        )
                    )
                ),
                Rule(
                    ruleExpires = Instant.now().epochSecond + 60,
                    iss = listOf(
                        StringCondition(
                            StringCondition.Operation.Equals,
                            "bad-iss.mfrancza.com"
                        )
                    )
                ),
                Rule(
                    ruleExpires = Instant.now().plus(1, ChronoUnit.DAYS).epochSecond,
                    aud = listOf(
                        StringCondition(StringCondition.Operation.Equals, "bad-aud.mfrancza.com")
                    )
                )
            ),
            timestamp = Instant.now().epochSecond
        )

        assertNull(ruleSet.isMetBy(Claims(
            aud = "good-aud.mfrancza.com",
            iss = "good-iss.mfrancza.com",
            exp = Instant.now().epochSecond
        )), "No rule should match, so null should be returned")

    }

    @Test
    fun testIsMetByReturnsRuleWhichMeetsIt() {
        val expectedRule = Rule(
            ruleExpires = Instant.now().epochSecond + 60,
            iss = listOf(
                StringCondition(
                    StringCondition.Operation.Equals,
                    "bad-iss.mfrancza.com"
                )
            )
        )

        val ruleSet = RuleSet(
            rules = listOf(
                Rule(
                    ruleExpires = Instant.now().epochSecond + 60,
                    exp = listOf(
                        DateTimeCondition(
                            DateTimeCondition.Operation.After,
                            Instant.now().plus(1, ChronoUnit.HOURS).epochSecond
                        )
                    ),
                ),
                expectedRule,
                Rule(
                    ruleExpires = Instant.now().plus(1, ChronoUnit.DAYS).epochSecond,
                    aud = listOf(
                        StringCondition(StringCondition.Operation.Equals, "bad-aud.mfrancza.com")
                    )
                )
            ),
            timestamp = Instant.now().epochSecond
        )

        assertEquals(expectedRule, ruleSet.isMetBy(Claims(
            aud = "good-aud.mfrancza.com",
            iss = "bad-iss.mfrancza.com",
            exp = Instant.now().epochSecond
        )),"The rule that matches should be returned")
    }

    @Test
    fun testIsMet() {
        val ruleSet = RuleSet(
            rules = listOf(
                Rule(
                    ruleExpires = Instant.now().epochSecond + 60,
                    exp = listOf(
                        DateTimeCondition(
                            DateTimeCondition.Operation.After,
                            Instant.now().plus(1, ChronoUnit.HOURS).epochSecond
                        )
                    ),
                ),
            ),
            timestamp = Instant.now().epochSecond
        )

        assertTrue(ruleSet.isMet(Claims(
            exp = Instant.now().plus(2, ChronoUnit.HOURS).epochSecond
        )),"True should be returned when the claims match a rule")

        assertFalse(ruleSet.isMet(Claims(
            exp = Instant.now().epochSecond
        )),"False should be returned when the claims do not match a rule")
    }
}