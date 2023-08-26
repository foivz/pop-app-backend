package hr.foi.pop.backend.services.authentication_service

import hr.foi.pop.backend.exceptions.RefreshTokenInvalidException
import hr.foi.pop.backend.repositories.UserRepository
import hr.foi.pop.backend.security.jwt.JwtPair
import hr.foi.pop.backend.services.AuthenticationService
import hr.foi.pop.backend.services.UserService
import hr.foi.pop.backend.utils.MockObjectsHelper
import hr.foi.pop.backend.utils.TokenPairValidator
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthenticationServiceTokensTest {
    @Autowired
    lateinit var authenticationService: AuthenticationService

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var userRepository: UserRepository

    private val mockLoginUser =
        MockObjectsHelper.getMockRegisterRequestBody("tokens-tester", "test@tokens.com")

    @BeforeAll
    fun setup() {
        val user = userService.registerUser(mockLoginUser)
        user.isAccepted = true
        userRepository.save(user)
    }

    @Test
    @Transactional
    fun givenValidRefreshToken_whenUserRequestsNewTokenPair_newDifferentTokensGenerated() {
        val initialJwtPairAfterLogin: JwtPair = getJwtPairByLoggingIn()

        val originalRefreshToken = initialJwtPairAfterLogin.refreshToken
        val firstRefreshedRefreshToken = getNewValidRefreshTokenByUsingRefreshToken(originalRefreshToken)
        val secondRefreshedRefreshToken = getNewValidRefreshTokenByUsingRefreshToken(firstRefreshedRefreshToken)

        Assertions.assertNotEquals(firstRefreshedRefreshToken, secondRefreshedRefreshToken)
    }

    private fun getNewValidRefreshTokenByUsingRefreshToken(refreshToken: String): String {
        val refreshedJwtPair: JwtPair =
            authenticationService.procureNewJwtPairUsingRefreshToken(refreshToken)
        ensureRefreshedTokenPairIsValid(refreshedJwtPair)
        return refreshedJwtPair.refreshToken
    }

    private fun ensureRefreshedTokenPairIsValid(tokenPair: JwtPair) {
        TokenPairValidator.assertTokenPairValid(tokenPair)
    }

    @Test
    @Transactional
    fun givenValidRefreshToken_whenUsedMoreThanOnce_thenRefreshTokenInvalidExceptionThrown() {
        val initialJwtPairAfterLogin: JwtPair = getJwtPairByLoggingIn()

        val validRefreshToken = initialJwtPairAfterLogin.refreshToken
        getNewValidRefreshTokenByUsingRefreshToken(validRefreshToken)
        assertThrowsWhenSameRefreshTokenUsedForTheSecondTime(validRefreshToken)
    }

    private fun assertThrowsWhenSameRefreshTokenUsedForTheSecondTime(alreadyUsedRefreshToken: String) {
        val thrownException = assertThrows<RefreshTokenInvalidException> {
            authenticationService.procureNewJwtPairUsingRefreshToken(alreadyUsedRefreshToken)
        }
        Assertions.assertEquals("Given refresh token not found!", thrownException.message)
    }

    private fun getJwtPairByLoggingIn(): JwtPair = authenticationService.authenticateAndGenerateJWTPair(
        mockLoginUser.username,
        mockLoginUser.password
    )
}
