package hr.foi.pop.backend.services

import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.exceptions.BadRefreshTokenFormatException
import hr.foi.pop.backend.exceptions.RefreshTokenInvalidException
import hr.foi.pop.backend.models.refresh_token.RefreshToken
import hr.foi.pop.backend.models.user.User
import hr.foi.pop.backend.repositories.RefreshTokenRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random

@Service
class RefreshTokenService {
    @Value("\${hr.foi.pop.backend.refreshToken.expirationMinutes}")
    private var refreshTokenExpirationMinutes: Long = 1440

    @Autowired
    private lateinit var refreshTokenRepository: RefreshTokenRepository

    private val refreshTokenRegex = Regex("^[A-Za-z0-9+/=]{64}$")

    fun createNewRefreshTokenForUser(user: User): RefreshToken {
        val generatedToken = generateToken()

        val refreshTokenEntity = RefreshToken().apply {
            owner = user
            token = generatedToken
            dateCreated = LocalDateTime.now()
            expirationDate = generateExpirationDate()
        }

        refreshTokenRepository.save(refreshTokenEntity)
        return refreshTokenEntity
    }

    private fun generateExpirationDate() = LocalDateTime.now().plusMinutes(refreshTokenExpirationMinutes)


    private fun generateToken(): String {
        val randomBytes = Random.Default.nextBytes(48)
        val base64Encoder = Base64.getEncoder()
        return base64Encoder.encodeToString(randomBytes)
    }

    fun createNewRefreshTokenFromExistingRefreshToken(refreshToken: String): RefreshToken {
        ensureRefreshTokenHasCorrectFormat(refreshToken)

        val foundToken = refreshTokenRepository.getRefreshTokenByToken(refreshToken)
            ?: throw RefreshTokenInvalidException(
                "Provided refresh token not recognized!",
                ApplicationErrorType.ERR_REFRESH_TOKEN_INVALID
            )

        ensureRefreshTokenNotExpired(foundToken)
        updateRefreshTokenValue(foundToken)

        return foundToken
    }

    private fun ensureRefreshTokenHasCorrectFormat(refreshToken: String) {
        if (!refreshToken.matches(refreshTokenRegex)) {
            throw BadRefreshTokenFormatException()
        }
    }

    private fun ensureRefreshTokenNotExpired(foundToken: RefreshToken) {
        val currentTime = LocalDateTime.now()
        if (foundToken.expirationDate < currentTime) {
            throw RefreshTokenInvalidException("Refresh token expired!", ApplicationErrorType.ERR_REFRESH_TOKEN_EXPIRED)
        }
    }

    private fun updateRefreshTokenValue(foundToken: RefreshToken) {
        foundToken.token = generateToken()
        foundToken.dateCreated = LocalDateTime.now()
        foundToken.expirationDate = generateExpirationDate()
        refreshTokenRepository.save(foundToken)
    }
}
