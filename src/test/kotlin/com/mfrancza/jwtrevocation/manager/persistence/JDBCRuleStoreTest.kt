package com.mfrancza.jwtrevocation.manager.persistence

class JDBCRuleStoreTest : RuleStoreTest() {
    override val ruleStore : RuleStore = run {
        val ruleStore = JDBCRuleStore("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
        ruleStore.initialize()
        ruleStore
    }

}