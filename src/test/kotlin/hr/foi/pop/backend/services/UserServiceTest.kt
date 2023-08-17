package hr.foi.pop.backend.services

import hr.foi.pop.backend.exceptions.UserAuthenticationException
import hr.foi.pop.backend.exceptions.UserCheckException
import hr.foi.pop.backend.models.user.User
import hr.foi.pop.backend.repositories.EventRepository
import hr.foi.pop.backend.repositories.UserRepository
import hr.foi.pop.backend.request_bodies.RegisterRequestBody
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Duration
import java.time.LocalDateTime

val templateRequestBodyForTesting = RegisterRequestBody(
    "Tester",
    "Testermann",
    "usercheckertest",
    "tester@usercheckertest.com",
    "test123",
    "buyer"
)

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var eventRepository: EventRepository

    @BeforeAll
    fun givenValidUserInformation_OnRegister_NewRecordPersistedWithCorrectData() {
        Assertions.assertFalse(userRepository.existsByUsername(templateRequestBodyForTesting.username))

        userService.registerUser(templateRequestBodyForTesting)
        val user = userRepository.getUserByUsername(templateRequestBodyForTesting.username)

        Assertions.assertNotNull(user)
        user?.let {
            assertUserBelongsToCurrentEvent(user)
            assertUserRegisteredJustNow(user)
            assertUserHasNoBalance(user)
            assertUserNotAccepted(user)
        }
    }

    private fun assertUserBelongsToCurrentEvent(user: User) {
        val currentEvent = eventRepository.getEventByIsActiveTrue()

        Assertions.assertEquals(currentEvent.id, user.event.id)
    }

    private fun assertUserRegisteredJustNow(user: User) {
        val currentTime = LocalDateTime.now()
        val timeDifference = Duration.between(currentTime, user.dateOfRegister).abs()

        val threshold = Duration.ofSeconds(2)
        val creationDateWithinThresholdOfNow = timeDifference < threshold

        assert(creationDateWithinThresholdOfNow)
    }

    private fun assertUserHasNoBalance(user: User) {
        Assertions.assertEquals(0, user.balance)
    }

    private fun assertUserNotAccepted(user: User) {
        Assertions.assertFalse(user.isAccepted)
    }

    @Test
    fun whenUserWantsToLogInWithCorrectPasswordButIsNotActivated_OnLogin_ThrowException() {
        Assertions.assertTrue(userRepository.existsByUsername(templateRequestBodyForTesting.username))
        val correctUsername = templateRequestBodyForTesting.username
        val correctPassword = templateRequestBodyForTesting.password
        Assertions.assertThrows(UserNotAcceptedException::class.java) {
            userService.authenticateAndGenerateJWT(correctUsername, correctPassword)
        }
    }

    @Test
    fun whenUserWantsToLogInWithCorrectPassword_OnLogin_GetValidJWT() {
        Assertions.assertTrue(userRepository.existsByUsername(templateRequestBodyForTesting.username))
        val correctUsername = templateRequestBodyForTesting.username
        val correctPassword = templateRequestBodyForTesting.password
        val jwt: String = userService.authenticateAndGenerateJWT(correctUsername, correctPassword)
        assertJwtLooksFine(jwt)
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
            userService.authenticateAndGenerateJWT(
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
    fun givenUserWithBadUsername_OnRegister_ThrowUserCheckException() {
        val userWithBadUsername = templateRequestBodyForTesting.copy(username = "bad")
        assertThrows<UserCheckException> { userService.registerUser(userWithBadUsername) }
    }
}
