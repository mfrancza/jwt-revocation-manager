package com.mfrancza.jwtrevocation.rules

/**
 * A source of claims which can be evaluated against Rules
 */
interface ClaimsSource {
    fun iss(): String?
    fun sub(): String?
    fun aud(): String?
    fun exp(): Long?
    fun nbf(): Long?
    fun iat(): Long?
    fun jti(): String?
}
