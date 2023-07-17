package com.mfrancza.jwtrevocation.manager.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.exceptions.InvalidClaimException
import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.Payload
import io.ktor.server.auth.jwt.JWTPrincipal
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SecurityTest {

    /**
     * Creates a scope claim
     * @param scopeClaimValue space-delimited scopes
     * @return a Claim with the value
     */
    private fun getScopeClaim(scopeClaimValue: String) = object : Claim {
        override fun isNull(): Boolean {
            return false
        }

        override fun isMissing(): Boolean {
            return false
        }

        override fun asBoolean(): Boolean {
            throw InvalidClaimException("Claim is String")
        }
        override fun asInt(): Int {
            throw InvalidClaimException("Claim is String")
        }
        override fun asLong(): Long {
            throw InvalidClaimException("Claim is String")
        }
        override fun asDouble(): Double {
            throw InvalidClaimException("Claim is String")
        }
        override fun asString(): String {
            return scopeClaimValue
        }
        override fun asDate(): Date {
            throw InvalidClaimException("Claim is String")
        }
        override fun <T : Any?> asArray(tClazz: Class<T>?): Array<T> {
            throw InvalidClaimException("Claim is String")
        }
        override fun <T : Any?> asList(tClazz: Class<T>?): MutableList<T> {
            throw InvalidClaimException("Claim is String")
        }
        override fun asMap(): MutableMap<String, Any> {
            throw InvalidClaimException("Claim is String")
        }
        override fun <T : Any?> `as`(tClazz: Class<T>?): T {
            throw InvalidClaimException("Claim is String")
        }
    }

    /**
     * Generates a token with the specified claims
     * @param claims map of claim names to values
     * @return a principal with the scope claim
     */
    private fun getTestToken(claims : MutableMap<String, Claim>) : JWTPrincipal {
        return JWTPrincipal(object : Payload {
            override fun getIssuer(): String {
                TODO("Not yet implemented")
            }
            override fun getSubject(): String {
                TODO("Not yet implemented")
            }
            override fun getAudience(): MutableList<String> {
                TODO("Not yet implemented")
            }
            override fun getExpiresAt(): Date {
                TODO("Not yet implemented")
            }
            override fun getNotBefore(): Date {
                TODO("Not yet implemented")
            }
            override fun getIssuedAt(): Date {
                TODO("Not yet implemented")
            }
            override fun getId(): String {
                TODO("Not yet implemented")
            }
            override fun getClaim(name: String?): Claim {
                return claims[name] ?: object : Claim {
                    override fun isNull(): Boolean {
                        TODO("Not yet implemented")
                    }

                    override fun isMissing(): Boolean {
                        return true
                    }

                    override fun asBoolean(): Boolean {
                        TODO("Not yet implemented")
                    }

                    override fun asInt(): Int {
                        TODO("Not yet implemented")
                    }

                    override fun asLong(): Long {
                        TODO("Not yet implemented")
                    }

                    override fun asDouble(): Double {
                        TODO("Not yet implemented")
                    }

                    override fun asString(): String {
                        TODO("Not yet implemented")
                    }

                    override fun asDate(): Date {
                        TODO("Not yet implemented")
                    }

                    override fun <T : Any?> asArray(clazz: Class<T>?): Array<T> {
                        TODO("Not yet implemented")
                    }

                    override fun <T : Any?> asList(clazz: Class<T>?): MutableList<T> {
                        TODO("Not yet implemented")
                    }

                    override fun asMap(): MutableMap<String, Any> {
                        TODO("Not yet implemented")
                    }

                    override fun <T : Any?> `as`(clazz: Class<T>?): T {
                        TODO("Not yet implemented")
                    }

                }
            }
            override fun getClaims(): MutableMap<String, Claim> {
                return claims
            }
        })
    }

    @Test
    fun hasScopeReturnsTrueIfTheScopeIsPresent(){
        val jwt = getTestToken(mutableMapOf("scope" to getScopeClaim("GET:/ruleset POST:/rules GET:/rules/{id}")))
        assertTrue(jwt.hasScope("GET:/rules/{id}"), "Should return true when the value is in the scope")
    }

    @Test
    fun hasScopeReturnsFalseIfTheScopeIsNotPresent(){
        val jwt = getTestToken(mutableMapOf("scope" to getScopeClaim("GET:/ruleset POST:/rules GET:/rules/{id}")))
        assertFalse(jwt.hasScope("GET:/rules"), "Should return false when the value is not in the scope")
    }

    @Test
    fun hasScopeReturnsFalseIfTheTokenDoesNotHaveAScopeClaim(){
        val jwt = getTestToken(mutableMapOf())
        assertFalse(jwt.hasScope("GET:/rules"), "Should return false when the value is not in the scope")
    }

    @Test
    fun hasScopeReturnsFalseIfTheTokenHasANullScopeClaim(){
        val jwt = getTestToken(mutableMapOf("scope" to object : Claim {
            override fun isNull(): Boolean {
                return true
            }

            override fun isMissing(): Boolean {
                return false
            }

            override fun asBoolean(): Boolean {
                TODO("Not yet implemented")
            }

            override fun asInt(): Int {
                TODO("Not yet implemented")
            }

            override fun asLong(): Long {
                TODO("Not yet implemented")
            }

            override fun asDouble(): Double {
                TODO("Not yet implemented")
            }

            override fun asString(): String {
                TODO("Not yet implemented")
            }

            override fun asDate(): Date {
                TODO("Not yet implemented")
            }

            override fun <T : Any?> asArray(clazz: Class<T>?): Array<T> {
                TODO("Not yet implemented")
            }

            override fun <T : Any?> asList(clazz: Class<T>?): MutableList<T> {
                TODO("Not yet implemented")
            }

            override fun asMap(): MutableMap<String, Any> {
                TODO("Not yet implemented")
            }

            override fun <T : Any?> `as`(clazz: Class<T>?): T {
                TODO("Not yet implemented")
            }

        }))
        assertFalse(jwt.hasScope("GET:/rules"), "Should return false when the value is not in the scope")
    }
}
