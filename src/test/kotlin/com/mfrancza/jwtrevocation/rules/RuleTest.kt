package com.mfrancza.jwtrevocation.rules

import com.mfrancza.jwtrevocation.rules.conditions.DateTimeBefore
import com.mfrancza.jwtrevocation.rules.conditions.DateTimeCondition
import com.mfrancza.jwtrevocation.rules.conditions.StringCondition
import com.mfrancza.jwtrevocation.rules.conditions.StringEquals
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue


class RuleTest {

    @Test
    fun testIsNotMetByReturnsNullIfAllMatch() {
        val rule = Rule(
            ruleExpires = Instant.now().epochSecond + 60,
            iss = listOf(
                StringEquals(
                    "bad-iss.mfrancza.com"
                )
            ),
            iat = listOf(
                DateTimeBefore(Instant.now().epochSecond)
            )
        )

        assertNull(
            rule.isNotMetBy(Claims(
                iss = "bad-iss.mfrancza.com",
                iat = Instant.now().epochSecond - 60 * 60,
                jti = "test-token"
            )),
            "Both of the conditions match, so null should be returned"
        )
    }

    @Test
    fun testIsNotMetByReturnsTheRuleThatDoesNotMatch() {
        val rule = Rule(
            ruleExpires = Instant.now().epochSecond + 60,
            iss = listOf(
                StringEquals(
                    "bad-iss.mfrancza.com"
                )
            ),
            iat = listOf(
                DateTimeBefore(Instant.now().epochSecond)
            )
        )

        assertEquals(
            rule.iss.first(),
            rule.isNotMetBy(Claims(
                iss = "good-iss.mfrancza.com",
                iat = Instant.now().epochSecond - 60 * 60,
                jti = "test-token"
            )),
            "The issuer condition which doesn't match should be returned"
        )
    }

    @Test
    fun testIsMet() {
        val rule = Rule(
            ruleExpires = Instant.now().epochSecond + 60,
            iss = listOf(
                StringEquals(
                    "bad-iss.mfrancza.com"
                )
            )
        )

        assertTrue(rule.isMet(Claims(
            iss = "bad-iss.mfrancza.com",
            jti = "test-token"
        )))

        assertFalse(rule.isMet(Claims(
            iss = "good-iss.mfrancza.com",
            jti = "test-token"
        )))
    }
}