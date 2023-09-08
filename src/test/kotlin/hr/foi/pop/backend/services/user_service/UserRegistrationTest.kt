package hr.foi.pop.backend.services.user_service

import hr.foi.pop.backend.exceptions.UserCheckException
import hr.foi.pop.backend.models.user.User
import hr.foi.pop.backend.repositories.EventRepository
import hr.foi.pop.backend.repositories.UserRepository
import hr.foi.pop.backend.services.UserService
import hr.foi.pop.backend.utils.MockObjectsHelper
import jakarta.transaction.Transactional
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Duration
import java.time.LocalDateTime

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class UserRegistrationTest {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var eventRepository: EventRepository

    private val templateRequestBodyForTesting =
        MockObjectsHelper.getMockRegisterRequestBody("registration-tester", "test@registration.com")

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
    fun givenUserWithBadUsername_OnRegister_ThrowUserCheckException() {
        val userWithBadUsername = templateRequestBodyForTesting.copy(username = "bad")
        assertThrows<UserCheckException> { userService.registerUser(userWithBadUsername) }
    }
}
