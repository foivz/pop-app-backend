package hr.foi.pop.backend.services.user_service

import hr.foi.pop.backend.exceptions.ChangeUserStatusException
import hr.foi.pop.backend.models.user.User
import hr.foi.pop.backend.services.UserService
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@Transactional
class UserActivationTest {
    @Autowired
    private lateinit var userService: UserService

    @Test
    fun givenUserWithStatusActive_onDeactivate_shouldDeactivateUser() {
        val userIdForUserWithActiveFlag = 1

        val user: User = userService.deactivateUser(userIdForUserWithActiveFlag)

        Assertions.assertEquals(false, user.isAccepted)
    }

    @Test
    fun givenUserWithStatusDeactivate_onActivate_shouldActivateUser() {
        val userIdForUserWithDeactivateFlag = 4

        val user: User = userService.activateUser(userIdForUserWithDeactivateFlag)

        Assertions.assertEquals(true, user.isAccepted)
    }

    @Test
    fun givenAlreadyActivatedUser_onActivate_shouldThrowActivateUserException() {
        val userIdForAcceptedUser = 1

        Assertions.assertThrows(ChangeUserStatusException::class.java) {
            userService.activateUser(userIdForAcceptedUser)
        }
    }

    @Test
    fun givenAlreadyDeactivatedUser_onDeactivate_shouldThrowActivateUserException() {
        val userIdForAcceptedUser = 4

        Assertions.assertThrows(ChangeUserStatusException::class.java) {
            userService.deactivateUser(userIdForAcceptedUser)
        }
    }
}
