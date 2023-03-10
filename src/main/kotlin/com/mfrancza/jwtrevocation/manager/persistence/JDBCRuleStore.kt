package com.mfrancza.jwtrevocation.manager.persistence

import com.mfrancza.jwtrevocation.rules.Rule
import com.mfrancza.jwtrevocation.rules.conditions.StringEquals
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

/**
 * A RuleStore implementation that stores the rules in a database specified by a JDBC connection
 */
class JDBCRuleStore(url: String, user: String = "", password: String = "") : RuleStore {
    private val db = Database.connect(url = url, user = user, password = password)

    /**
     * Table for storing rules; conditions are not normalized since we don't need to query or mutate them
     */
    private object Rules: Table() {
        val ruleId = varchar("ruleId", 36)
        val ruleExpires = long("ruleExpires")
        val iss = text("iss")
        val sub = text("sub")
        val aud = text("aud")
        val exp = text("exp")
        val nbf = text("nbf")
        val iat = text("iat")
        val jti = text("jti")
        override val primaryKey = PrimaryKey(ruleId)
    }

    override fun initialize() {
        transaction(db) {
            SchemaUtils.create(Rules)
        }
    }

    override fun create(newRule: Rule): Rule {
        if (newRule.ruleId != null) {
            throw IllegalArgumentException("New rules must not have a ruleId set")
        }
        return transaction(db) {
            val ruleId = Rules.insert {
                it[ruleId] = UUID.randomUUID().toString()
                it[ruleExpires] = newRule.ruleExpires
                it[iss] = Json.encodeToString(newRule.iss)
                it[sub] = Json.encodeToString(newRule.sub)
                it[aud] = Json.encodeToString(newRule.aud)
                it[exp] = Json.encodeToString(newRule.exp)
                it[nbf] = Json.encodeToString(newRule.nbf)
                it[iat] = Json.encodeToString(newRule.iat)
                it[jti] = Json.encodeToString(newRule.jti)
            } get Rules.ruleId
            newRule.copy(ruleId)
        }
    }

    /**
     * Maps a row from the Rules table to a Rule object
     */
    private fun rowToRule(row: ResultRow) : Rule {
        return Rule(
            ruleId = row[Rules.ruleId],
            ruleExpires = row[Rules.ruleExpires],
            iss = Json.decodeFromString(row[Rules.iss]),
            sub = Json.decodeFromString(row[Rules.sub]),
            aud = Json.decodeFromString(row[Rules.aud]),
            exp = Json.decodeFromString(row[Rules.exp]),
            nbf = Json.decodeFromString(row[Rules.nbf]),
            iat = Json.decodeFromString(row[Rules.iat]),
            jti = Json.decodeFromString(row[Rules.jti])
        )
    }

    override fun read(ruleId: String): Rule? {
        return transaction(db) {
            Rules.select {
                Rules.ruleId.eq(ruleId)
            }.map {
                rowToRule(it)
            }.singleOrNull()
        }
    }

    override fun delete(ruleId: String): Rule? {
        val rule = read(ruleId)
        if (rule != null) {
            transaction(db) {
                Rules.deleteWhere {
                    Rules.ruleId.eq(ruleId)
                }
            }
        }
        return rule
    }

    override fun list(): List<Rule> {
        return transaction(db) {
            Rules.selectAll().map { rowToRule(it) }
        }
    }

}