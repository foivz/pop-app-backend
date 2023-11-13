package hr.foi.pop.backend.services.authentication_service

import hr.foi.pop.backend.exceptions.UserAuthenticationException
import hr.foi.pop.backend.exceptions.UserNotAcceptedException
import hr.foi.pop.backend.repositories.UserRepository
import hr.foi.pop.backend.request_bodies.RegisterRequestBody
import hr.foi.pop.backend.security.jwt.TokenPair
import hr.foi.pop.backend.services.AuthenticationService
import hr.foi.pop.backend.services.UserService
import hr.foi.pop.backend.utils.MockObjectsHelper
import hr.foi.pop.backend.utils.TokenPairValidator
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class UserLoginTest {

    @Value("\${hr.foi.pop.backend.auth.disable-activation}")
    private var isAutomaticallyActivated: Boolean = false

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var authenticationService: AuthenticationService

    private val templateRequestBodyForTesting =
        MockObjectsHelper.getMockRegisterRequestBody("auth-tester", "test@auth.com")

    @BeforeAll
    fun registerMockUser() {
        Assertions.assertFalse(userRepository.existsByUsername(templateRequestBodyForTesting.username))
        userService.registerUser(templateRequestBodyForTesting)
    }

    @Test
    fun whenUserWantsToLogInWithCorrectPasswordButIsNotActivated_onLogin_throwException() {
        if (isAutomaticallyActivated) {
            assert(true)
            return
        }

        val notAcceptedUser = userRepository.getUserByUsername(templateRequestBodyForTesting.username)
        Assertions.assertNotNull(notAcceptedUser)
        Assertions.assertFalse(notAcceptedUser!!.isAccepted)

        val correctUsername = templateRequestBodyForTesting.username
        val correctPassword = templateRequestBodyForTesting.password

        Assertions.assertThrows(UserNotAcceptedException::class.java) {
            authenticationService.authenticateAndGenerateTokenPair(correctUsername, correctPassword)
        }
    }

    @Test
    fun whenUserWantsToLogInWithCorrectPassword_onLogin_getValidJWT() {
        acceptUser(templateRequestBodyForTesting.username)

        val correctUsername = templateRequestBodyForTesting.username
        val correctPassword = templateRequestBodyForTesting.password

        val tokenPair: TokenPair =
            authenticationService.authenticateAndGenerateTokenPair(correctUsername, correctPassword)
        TokenPairValidator.assertTokenPairValid(tokenPair)
    }

    private fun acceptUser(username: String) {
        val user = userRepository.getUserByUsername(username)
        Assertions.assertNotNull(user)

        user!!.isAccepted = true
        userRepository.save(user)
    }

    @Test
    fun whenUserWantsToLogInWithIncorrectPassword_onLoginAttempt_throwException() {
        val correctUsername = templateRequestBodyForTesting.username
        val badPassword = "bad password"

        assertExceptionGetsThrownForBadLogin(correctUsername, badPassword)
    }

    @Test
    fun whenNonExistentUserTriesToLogIn_onLoginAttempt_throwException() {
        val nonExistentUsername = "nonexistent tester user"
        val password = "password of non existent user"

        assertExceptionGetsThrownForBadLogin(nonExistentUsername, password)
    }

    private fun assertExceptionGetsThrownForBadLogin(username: String, password: String) {
        val thrownException = assertThrows<UserAuthenticationException> {
            authenticationService.authenticateAndGenerateTokenPair(
                username,
                password
            )
        }
        assertAuthenticationExceptionDescribesError(thrownException)
    }

    private fun assertAuthenticationExceptionDescribesError(ex: UserAuthenticationException) {
        Assertions.assertEquals("Please check your credentials!", ex.message)
    }

    @Test
    fun whenUserFromAnotherEventTriesToLogIn_onLoginAttempt_inactiveEventExceptionThrown() {
        deactivateUsersEventButActivateUser(templateRequestBodyForTesting)

        val mockUsername = templateRequestBodyForTesting.username
        val mockPassword = templateRequestBodyForTesting.password

        assertThrows<UserAuthenticationException> {
            authenticationService.authenticateAndGenerateTokenPair(mockUsername, mockPassword)
        }
    }

    private fun deactivateUsersEventButActivateUser(templateRequestBodyForTesting: RegisterRequestBody) {
        val user = userRepository.getUserByUsername(templateRequestBodyForTesting.username)
        Assertions.assertNotNull(user)

        user!!.event.isActive = false
        user.isAccepted = true

        userRepository.save(user)
    }
}
