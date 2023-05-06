package com.mfrancza.jwtrevocation.manager.persistence

import com.mfrancza.jwtrevocation.rules.Rule
import com.mfrancza.jwtrevocation.rules.conditions.StringEquals
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Abstract test class that should be implemented for each RuleStore implementation to ensure it meet the interface contract
 */
abstract class RuleStoreTest {
    abstract val ruleStore : RuleStore

    /**
     * Test that create persist the rule and assigns it an ID
     */
    @Test
    fun testCreatePersistsRule() {
        val newRule = Rule(
            ruleExpires = 1666928921,
            iss = listOf(StringEquals(value = "badissuer.mfrancza.com"))
        )

        val returnedRule = ruleStore.create(newRule)

        assertNotNull(returnedRule.ruleId, "The returned rule should be assigned an ID")

        val readRule = ruleStore.read(returnedRule.ruleId!!)

        assertEquals(returnedRule, readRule, "The content of the rule returned by create and read form storage should be the same")
    }

    /**
     * Test that Rules with an ID already assigned cannot be created
     */
    @Test
    fun testCreateRequiresNullId() {
        val newRule = Rule(
            ruleId = UUID.randomUUID().toString(),
            ruleExpires = 1666928921,
            iss = listOf(
                StringEquals(
                    value = "badissuer.mfrancza.com"
                )
            )
        )
        assertFailsWith<IllegalArgumentException>( "Should throw an exception if the ID is already set") {
            ruleStore.create(newRule)
        }
    }

    /**
     * Test that list returns all the rules in the rule store
     */
    @Test
    fun testList() {
        val firstRule = ruleStore.create(Rule(
            ruleExpires = 1667156265,
            jti = listOf(StringEquals(value = "badId"))
        ))

        assertContains(ruleStore.list().rules, firstRule)

        val secondRule = ruleStore.create(Rule(
            ruleExpires = 1667156265,
            aud = listOf(StringEquals(value = "badaud.mfrancza.com"))
        ))

        val rules = ruleStore.list().rules

        assertContains(rules, firstRule, "List should contain the first rule")
        assertContains(rules, secondRule, "List should contain the second rule")

        for(i in 0..10) {
            ruleStore.create(Rule(
                ruleExpires = 1667156265,
                iss = listOf(StringEquals(value = "badiss${i.toString()}.mfrancza.com"))
            ))
        }

        val firstTwoPartialList = ruleStore.list(cursor = null, limit = 2)
        assertEquals(2, firstTwoPartialList.rules.size, "No more than the limit should be returned")
        assertNotNull(firstTwoPartialList.cursor, "A cursor should be returned if not all the items have been listed")

        val nextFivePartialList = ruleStore.list(cursor = firstTwoPartialList.cursor, limit = 5)
        assertEquals(5, nextFivePartialList.rules.size, "No more than the limit should be returned")
        assertNotNull(nextFivePartialList.cursor, "A cursor should be returned if not all the items have been listed")
        for (rule in firstTwoPartialList.rules) {
            assertFalse(nextFivePartialList.rules.contains(rule), "Already returned rules should not be repeated")
        }

        val remainingPartialList = ruleStore.list(cursor = nextFivePartialList.cursor, limit = 100)
        assertNull(remainingPartialList.cursor, "A cursor should not be returned if all the items have been listed")

    }

    /**
     * Test that deleting a rule removes it from the rule store
     */
    @Test
    fun testDeleteRuleExists() {
        val existingRule = ruleStore.create(Rule(
            ruleExpires = 1667156265,
            jti = listOf(StringEquals(value = "badId"))
        ))

        assertEquals(existingRule, ruleStore.delete(existingRule.ruleId!!), "The deleted rule should be returned")

        assertNull(ruleStore.read(existingRule.ruleId!!), "The rule should no longer be read")

        assertFalse(ruleStore.list().rules.contains(existingRule), "The rule should no longer be listed")
    }

    /**
     * Tests that deleting a rule that does not exist returns null
     */
    @Test
    fun testDeleteRuleDoesNotExists() {
        assertNull(ruleStore.delete("non-existent-rule-id"), "Null should be returned if no matching rule was deleted")
    }
}