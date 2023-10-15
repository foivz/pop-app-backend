package hr.foi.pop.backend.services

import hr.foi.pop.backend.exceptions.UserAuthenticationException
import hr.foi.pop.backend.exceptions.UserNotAcceptedException
import hr.foi.pop.backend.models.event.Event
import hr.foi.pop.backend.models.user.User
import hr.foi.pop.backend.repositories.UserRepository
import hr.foi.pop.backend.security.jwt.JwtUtils
import hr.foi.pop.backend.security.jwt.TokenPair
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthenticationService : UserDetailsService {
    @Autowired
    private lateinit var refreshTokenService: RefreshTokenService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var jwtUtils: JwtUtils

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    fun authenticateAndGenerateTokenPair(username: String, password: String): TokenPair {
        val user = authenticate(username, password)

        val accessToken = generateAccessToken(user)
        val refreshToken = refreshTokenService.createNewRefreshTokenForUser(user)

        return TokenPair(accessToken, refreshToken)
    }

    private fun authenticate(providedUsername: String, providedPassword: String): User {
        val user = userRepository.getUserByUsername(providedUsername)
            ?: throw UserAuthenticationException("Non-existent user $providedUsername tried to log in")

        ensurePasswordMatch(providedPassword, user.passwordHash)
        ensureUserIsAccepted(user)
        ensureEventIsActive(user.event)

        return user
    }

    private fun ensurePasswordMatch(passwordPlaintext: String, passwordHashed: String) {
        if (!passwordEncoder.matches(passwordPlaintext, passwordHashed)) {
            throw UserAuthenticationException("A wrong password was provided!")
        }
    }

    private fun ensureUserIsAccepted(user: User) {
        if (!user.isAccepted) {
            throw UserNotAcceptedException(user.username)
        }
    }

    private fun ensureEventIsActive(event: Event) {
        if (!event.isActive) {
            throw UserAuthenticationException("User belongs to event \"${event.name}\" which is deactivated!")
        }
    }

    private fun generateAccessToken(user: User): String {
        return jwtUtils.generateJwtToken(user)
    }

    override fun loadUserByUsername(username: String): UserDetails {
        return userRepository.getUserByUsername(username)
            ?: throw UsernameNotFoundException("User '$username' not found!")
    }

    fun procureNewTokenPairUsingRefreshToken(refreshToken: String): TokenPair {
        val newRefreshToken = refreshTokenService.createNewRefreshTokenFromExistingRefreshToken(refreshToken)
        val accessToken = generateAccessToken(newRefreshToken.owner)
        return TokenPair(accessToken, newRefreshToken)
    }
}
