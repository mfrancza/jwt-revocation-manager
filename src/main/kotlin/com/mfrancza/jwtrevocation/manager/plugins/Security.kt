package com.mfrancza.jwtrevocation.manager.plugins

import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.mfrancza.jwtrevocation.manager.persistence.RuleStore
import com.mfrancza.jwtrevocation.ktor.server.auth.notRevoked
import com.mfrancza.jwtrevocation.ktor.server.auth.toPrincipal
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTAuthenticationProvider
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.request.path
import org.koin.ktor.ext.inject
import java.util.concurrent.TimeUnit

/**
 * Settings for configuring the security plugins
 */
data class SecuritySettings(
    /**
     * The expected audience for the token
     * tokens without a matching audience are rejected
     */
    val audience: String,
    /**
     * The expected issuer for the token
     * tokens without a matching issuer are rejected
     * if RSA
     */
    val issuer: String,
    /**
     * The verification strategy for the tokens
     */
    val verification: Verification,
    /**
     * The realm for the manager instance
     * returned in the WWW-Authenticate header
     * Default is the audience
     */
    val realm: String = audience
) {

    /**
     * Sets the verifier based on the setting's verification
     */
    fun setVerifier(config : JWTAuthenticationProvider.Config) {
        verification.setVerifier(this, config)
    }

    sealed interface Verification {
        fun setVerifier(settings: SecuritySettings, providerConfig: JWTAuthenticationProvider.Config)
    }

    /**
     * Verification using a public key provided at the issuer's URL
     */
    object RS256 : Verification {
        override fun setVerifier(settings: SecuritySettings, providerConfig: JWTAuthenticationProvider.Config) {
            val jwkProvider = JwkProviderBuilder(settings.issuer)
                .cached(10, 24, TimeUnit.HOURS)
                .rateLimited(10, 1, TimeUnit.MINUTES)
                .build()
            providerConfig.verifier(jwkProvider, settings.issuer) {
                acceptLeeway(3)
                withAudience(settings.audience)
            }
        }
    }

    /**
     * Verification using a shared secret between the issuer and the application
     */
    data class HS256(private val secret: String) : Verification {
        override fun setVerifier(settings: SecuritySettings, providerConfig: JWTAuthenticationProvider.Config) {
            providerConfig.verifier(JWT
                .require(com.auth0.jwt.algorithms.Algorithm.HMAC256(this.secret))
                .withAudience(settings.audience)
                .withIssuer(settings.issuer)
                .build())
        }
    }
}

/**
 * validates that the token's scope claim contains a value with method:path for the request
 * @param jwt jwt to validate
 * @param method method of the request
 * @param path path of the request
 * @return true if the claim contains the required scope
 */
fun JWTPrincipal.hasScope(scope: String) : Boolean {
    val claim = payload.getClaim("scope")
    if (claim.isMissing || claim.isNull) return false
    return claim.asString()
        .splitToSequence(" ").toSet()
        .contains(scope)
}

fun Application.configureSecurity(settings : SecuritySettings) {
    val ruleStore by inject<RuleStore>()

    install(Authentication) {
            jwt("auth-jwt") {
                realm = settings.realm
                settings.setVerifier(this)
                validate {
                    it.notRevoked(ruleStore.ruleSet())?.toPrincipal()
                }
            }
    }
}
