package hr.foi.pop.backend.services

import hr.foi.pop.backend.definitions.ActivateUserDefinitions
import hr.foi.pop.backend.exceptions.ActivateUserException
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

    @Test
    @Transactional
    fun givenUserWithStatusActive_onDeactivate_shouldDeactivateUser() {
        //given
        val deactivateUserDefinition: ActivateUserDefinitions = ActivateUserDefinitions.DEACTIVATE
        val userIdForUserWithActiveFlag: String = "1"

        //when
        val user: User = userService.activateOrDeactivateUser(userIdForUserWithActiveFlag, deactivateUserDefinition)

        //assert
        Assertions.assertEquals(false, user.isAccepted)
    }

    @Test
    @Transactional
    fun givenUserWithStatusDeactivate_onActivate_shouldActivateUser() {
        //given
        val activateUserDefinitions: ActivateUserDefinitions = ActivateUserDefinitions.ACTIVATE
        val userIdForUserWithDeactivateFlag: String = "4"

        //when
        val user: User = userService.activateOrDeactivateUser(userIdForUserWithDeactivateFlag, activateUserDefinitions)

        //assert
        Assertions.assertEquals(true, user.isAccepted)
    }

    @Test
    @Transactional
    fun givenAlreadyActivatedUser_onActivate_shouldThrowActivateUserException() {
        //given
        val activateUserDefinitions: ActivateUserDefinitions = ActivateUserDefinitions.ACTIVATE
        val userIdForUserWithActivateFlag: String = "1"

        //when - assert
        Assertions.assertThrows(ActivateUserException::class.java) {
            userService.activateOrDeactivateUser(userIdForUserWithActivateFlag, activateUserDefinitions)
        }
    }

    @Test
    @Transactional
    fun givenAlreadyDeactivatedUser_onDeactivate_shouldThrowActivateUserException() {
        //given
        val activateUserDefinitions: ActivateUserDefinitions = ActivateUserDefinitions.DEACTIVATE
        val userIdForUserWithActivateFlag: String = "4"

        //when - assert
        Assertions.assertThrows(ActivateUserException::class.java) {
            userService.activateOrDeactivateUser(userIdForUserWithActivateFlag, activateUserDefinitions)
        }
    }
}
