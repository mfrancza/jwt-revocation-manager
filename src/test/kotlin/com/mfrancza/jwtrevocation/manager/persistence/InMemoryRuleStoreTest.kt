package com.mfrancza.jwtrevocation.manager.persistence

import com.mfrancza.jwtrevocation.rules.Rule
import com.mfrancza.jwtrevocation.rules.conditions.StringCondition
import com.mfrancza.jwtrevocation.rules.conditions.StringEquals
import java.util.UUID
import kotlin.test.assertTrue
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class InMemoryRuleStoreTest : RuleStoreTest() {
    override val ruleStore = InMemoryRuleStore()

    /**
     * Tests that the initial rules are loaded and assigned IDs by the constructor
     */
    @Test
    fun testInitialRules() {
        val initialRules = listOf(
            Rule(
                ruleExpires = 1667156265,
                aud = listOf(
                    StringEquals("badaud.mfrancza.com")
                )
            ),
            Rule(
                ruleId = UUID.randomUUID().toString(),
                ruleExpires = 1667156265,
                iss = listOf(
                    StringEquals(
                        value = "badiss.mfrancza.com"
                    )
                )
            )
        )

        val rules = InMemoryRuleStore(initialRules).list()

        assertEquals(2, rules.size, "Both rules should be present")

        for (rule in rules) {
            assertNotNull(rule.ruleId, "Every rule should have a ruleId")
        }
    }

}