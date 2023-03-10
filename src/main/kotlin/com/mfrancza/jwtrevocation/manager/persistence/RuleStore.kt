package com.mfrancza.jwtrevocation.manager.persistence

import com.mfrancza.jwtrevocation.rules.Rule
import com.mfrancza.jwtrevocation.rules.RuleSet
import java.time.Instant

/**
 * Represents any mutable store for revocation rules and the basic operations it needs to support
 */
interface RuleStore {
    /**
     * initializes the RuleStore; called once to initialize the backing data store
     */
    fun initialize()

    /**
     * creates a new rule
     * the rule store is responsible for assigning the ID and should not accept a rule that already has it defined
     * @param newRule a Rule without a value for ruleId
     */
    fun create(newRule: Rule) : Rule

    /**
     * Reads a rule from the store
     * @param ruleId the ID of the rule to retrieve
     * @return the rule with ruleId or null
     */
    fun read(ruleId: String) : Rule?

    /**
     * Removes the rule with ruleId from the store if present
     * @param ruleId the ID of the rule to remove
     * @return the deleted Rule if one was present with the ruleId, otherwise null
     */
    fun delete(ruleId: String) : Rule?

    /**
     * Lists all the rules in the data store
     * @return a list containing all the rules
     */
    fun list() : List<Rule>

    /**
     * Returns a RuleSet created from the current contents of the RuleStore
     */
    fun ruleSet() : RuleSet = RuleSet(
        rules = this.list(),
        timestamp = Instant.now().epochSecond
    )
}