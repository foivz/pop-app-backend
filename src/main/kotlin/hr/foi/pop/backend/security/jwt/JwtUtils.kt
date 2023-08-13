package hr.foi.pop.backend.security.jwt

import hr.foi.pop.backend.models.user.User
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*


@Component
class JwtUtils {

    @Value("\${hr.foi.pop.backend.jwt.expirationMinutes}")
    private var jwtExpirationMinutes: Int = 0

    @Value("\${hr.foi.pop.backend.jwt.secret}")
    private lateinit var jwtSecret: String

    private val logger = LoggerFactory.getLogger(JwtUtils::class.java)

    private fun getSecretKey() = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret))
    private fun getJwtParser() = Jwts.parserBuilder()
        .setSigningKey(getSecretKey())
        .build()

    fun generateJwtToken(userPrincipal: User): String {
        return Jwts.builder()
            .setSubject(userPrincipal.username)
            .setIssuedAt(Date())
            .setExpiration(Date(Date().time + convertExpirationFromMinutesToMs()))
            .signWith(getSecretKey(), SignatureAlgorithm.HS256)
            .compact()
    }

    private fun convertExpirationFromMinutesToMs(): Int = jwtExpirationMinutes * 60 * 1000

    fun validateJwtToken(jwtToken: String): Boolean {
        var success = false

        try {
            getJwtParser().apply { parse(jwtToken) }
            success = true
        } catch (e: MalformedJwtException) {
            logger.error("Invalid JWT token: ${e.message}")
        } catch (e: ExpiredJwtException) {
            logger.error("JWT token is expired: ${e.message}")
        } catch (e: UnsupportedJwtException) {
            logger.error("JWT token is unsupported: ${e.message}")
        } catch (e: IllegalArgumentException) {
            logger.error("JWT claims string is empty: ${e.message}")
        }

        return success
    }

    fun getUsernameFromJwtToken(jwtToken: String): String {
        val jwtParser = getJwtParser()
        val parsedJwt = jwtParser.parseClaimsJwt(jwtToken)
        val jwtBody = parsedJwt.body
        return jwtBody.subject
    }
}
