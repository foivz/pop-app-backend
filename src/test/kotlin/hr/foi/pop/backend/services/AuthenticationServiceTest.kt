package hr.foi.pop.backend.services

import hr.foi.pop.backend.exceptions.UserAuthenticationException
import hr.foi.pop.backend.exceptions.UserNotAcceptedException
import hr.foi.pop.backend.repositories.UserRepository
import hr.foi.pop.backend.request_bodies.RegisterRequestBody
import hr.foi.pop.backend.utils.MockObjectsHelper
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class AuthenticationServiceTest {

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
    fun whenUserWantsToLogInWithCorrectPasswordButIsNotActivated_OnLogin_ThrowException() {
        val notAcceptedUser = userRepository.getUserByUsername(templateRequestBodyForTesting.username)
        Assertions.assertNotNull(notAcceptedUser)
        Assertions.assertFalse(notAcceptedUser!!.isAccepted)

        val correctUsername = templateRequestBodyForTesting.username
        val correctPassword = templateRequestBodyForTesting.password

        Assertions.assertThrows(UserNotAcceptedException::class.java) {
            authenticationService.authenticateAndGenerateJWT(correctUsername, correctPassword)
        }
    }

    @Test
    fun whenUserWantsToLogInWithCorrectPassword_OnLogin_GetValidJWT() {
        acceptUser(templateRequestBodyForTesting.username)

        val correctUsername = templateRequestBodyForTesting.username
        val correctPassword = templateRequestBodyForTesting.password

        val jwt: String = authenticationService.authenticateAndGenerateJWT(correctUsername, correctPassword)
        assertJwtLooksFine(jwt)
    }

    private fun acceptUser(username: String) {
        val user = userRepository.getUserByUsername(username)
        Assertions.assertNotNull(user)

        user!!.isAccepted = true
        userRepository.save(user)
    }

    private fun assertJwtLooksFine(jwt: String) {
        Assertions.assertTrue(jwt.length > 10)
        val jwtParts = jwt.split('.')
        Assertions.assertEquals(3, jwtParts.size, "JWT doesn't have 3 parts!")
        Assertions.assertEquals(20, jwtParts[0].length, "JWT's first part incorrect in size!")
        Assertions.assertFalse(jwtParts[1].length <= 10, "JWT's second part too short!")
        Assertions.assertFalse(jwtParts[2].length <= 20, "JWT's third part too short!")
    }

    @Test
    fun whenUserWantsToLogInWithIncorrectPassword_OnLoginAttempt_ThrowException() {
        val correctUsername = templateRequestBodyForTesting.username
        val badPassword = "bad password"

        assertExceptionGetsThrownForBadLogin(correctUsername, badPassword)
    }

    @Test
    fun whenNonExistentUserTriesToLogIn_OnLoginAttempt_ThrowException() {
        val nonExistentUsername = "nonexistent tester user"
        val password = "password of non existent user"

        assertExceptionGetsThrownForBadLogin(nonExistentUsername, password)
    }

    private fun assertExceptionGetsThrownForBadLogin(username: String, password: String) {
        val thrownException = assertThrows<UserAuthenticationException> {
            authenticationService.authenticateAndGenerateJWT(
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
    fun whenUserFromAnotherEventTriesToLogIn_OnLoginAttempt_InactiveEventExceptionThrown() {
        deactivateUsersEventButActivateUser(templateRequestBodyForTesting)

        val mockUsername = templateRequestBodyForTesting.username
        val mockPassword = templateRequestBodyForTesting.password

        assertThrows<UserAuthenticationException> {
            authenticationService.authenticateAndGenerateJWT(mockUsername, mockPassword)
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
