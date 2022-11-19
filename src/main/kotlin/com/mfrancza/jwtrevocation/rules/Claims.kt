package com.mfrancza.jwtrevocation.rules

/**
 * Basic implementation of a ClaimsSource as data class
 */
data class Claims  (
    val iss: String? = null,
    val sub: String? = null,
    val aud: String? = null,
    val exp: Long? = null,
    val nbf: Long? = null,
    val iat: Long? = null,
    val jti: String? = null
) : ClaimsSource {
    override fun iss() = iss
    override fun sub() = sub
    override fun aud() = aud
    override fun exp() = exp
    override fun nbf() = nbf
    override fun iat() = iat
    override fun jti() = jti
}