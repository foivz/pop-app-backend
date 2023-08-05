package hr.foi.pop.backend.services

import hr.foi.pop.backend.exceptions.UserCheckException
import hr.foi.pop.backend.models.user.User
import hr.foi.pop.backend.repositories.EventRepository
import hr.foi.pop.backend.repositories.UserRepository
import hr.foi.pop.backend.request_bodies.RegisterRequestBody
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
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
class UserServiceTest {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var eventRepository: EventRepository

    @Test
    @Transactional
    fun whenCorrectUserPassedIn_onRegister_NewRecordPersisted() {
        Assertions.assertFalse(userRepository.existsByUsername(templateRequestBodyForTesting.username))

        userService.registerUser(templateRequestBodyForTesting)

        Assertions.assertTrue(userRepository.existsByUsername(templateRequestBodyForTesting.username))
    }

    @Test
    @Transactional
    fun whenCorrectUserPassedIn_onRegister_InfoValid() {
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

        Assertions.assertEquals(currentEvent.id, user.event?.id)
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
