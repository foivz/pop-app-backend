package hr.foi.pop.backend.services.authentication_service

import hr.foi.pop.backend.exceptions.RefreshTokenInvalidException
import hr.foi.pop.backend.repositories.UserRepository
import hr.foi.pop.backend.security.jwt.TokenPair
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
        val initialTokenPairAfterLogin: TokenPair = getTokenPairByLoggingIn()

        val originalRefreshToken = initialTokenPairAfterLogin.refreshToken.token
        val firstRefreshedRefreshToken = getNewValidRefreshTokenByUsingRefreshToken(originalRefreshToken)
        val secondRefreshedRefreshToken = getNewValidRefreshTokenByUsingRefreshToken(firstRefreshedRefreshToken)

        Assertions.assertNotEquals(firstRefreshedRefreshToken, secondRefreshedRefreshToken)
    }

    private fun getNewValidRefreshTokenByUsingRefreshToken(refreshToken: String): String {
        val refreshedTokenPair: TokenPair =
            authenticationService.procureNewTokenPairUsingRefreshToken(refreshToken)
        ensureRefreshedTokenPairIsValid(refreshedTokenPair)
        return refreshedTokenPair.refreshToken.token
    }

    private fun ensureRefreshedTokenPairIsValid(tokenPair: TokenPair) {
        TokenPairValidator.assertTokenPairValid(tokenPair)
    }

    @Test
    @Transactional
    fun givenValidRefreshToken_whenUsedMoreThanOnce_thenRefreshTokenInvalidExceptionThrown() {
        val initialTokenPairAfterLogin: TokenPair = getTokenPairByLoggingIn()

        val validRefreshToken = initialTokenPairAfterLogin.refreshToken
        getNewValidRefreshTokenByUsingRefreshToken(validRefreshToken.token)
        assertThrowsWhenSameRefreshTokenUsedForTheSecondTime(validRefreshToken.token)
    }

    private fun assertThrowsWhenSameRefreshTokenUsedForTheSecondTime(alreadyUsedRefreshToken: String) {
        val thrownException = assertThrows<RefreshTokenInvalidException> {
            authenticationService.procureNewTokenPairUsingRefreshToken(alreadyUsedRefreshToken)
        }
        Assertions.assertEquals("Provided refresh token not recognized!", thrownException.message)
    }

    private fun getTokenPairByLoggingIn(): TokenPair = authenticationService.authenticateAndGenerateTokenPair(
        mockLoginUser.username,
        mockLoginUser.password
    )
}
