package com.mfrancza.jwtrevocation.manager.persistence

import com.mfrancza.jwtrevocation.manager.api.PartialList
import com.mfrancza.jwtrevocation.rules.Rule
import java.util.UUID

/**
 * A basic in-memory rule store for suitable for testing the application without dependencies
 * Takes an optional list of rules to initialize the store with, which can be assigned rule Ids or not
 */
class InMemoryRuleStore(initialRules: List<Rule> = emptyList()) : RuleStore {

    /**
     * the map used to store the values
     */
    private val ruleMap = HashMap<String, Rule>()

    init {
        //load the initial rules into the rule store
        for (rule in initialRules) {
            if (rule.ruleId == null) {
                create(rule)
            } else {
                ruleMap[rule.ruleId!!]=rule
            }
        }
    }

    override fun initialize() {
    }

    override fun create(newRule: Rule) : Rule {
        if (newRule.ruleId != null) {
            throw IllegalArgumentException("New rules must not have a ruleId set")
        } else {
            val ruleWithId = newRule.copy(ruleId = UUID.randomUUID().toString())
            ruleMap[ruleWithId.ruleId!!] = ruleWithId
            return ruleWithId
        }
    }

    override fun read(ruleId: String): Rule? {
        return ruleMap[ruleId]
    }

    override fun delete(ruleId: String) : Rule? {
        return ruleMap.remove(ruleId)
    }

    override fun list(cursor: String?, limit: Int?): PartialList {
        val start = cursor?.toInt() ?: 0
        val rules = ruleMap.values.toList()

        if (start > rules.size - 1) {
            return PartialList(
                emptyList(),
                null
            )
        }

        val end = if (limit != null) {
            val max = start + limit
            if (max < rules.size) {
                max
            } else {
                rules.size
            }
        } else {
            rules.size
        }

        return PartialList(
            rules = rules.subList(start, end),
            cursor = if (limit != null && end < rules.size) {
                end.toString()
            } else {
                null
            }
        )
    }
}