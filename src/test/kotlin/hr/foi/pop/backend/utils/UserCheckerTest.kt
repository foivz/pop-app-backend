package hr.foi.pop.backend.utils

import hr.foi.pop.backend.exceptions.UserCheckException
import hr.foi.pop.backend.repositories.UserRepository
import hr.foi.pop.backend.request_bodies.RegisterRequestBody
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserCheckerTest(@Autowired userRepository: UserRepository) :
    UserChecker(RegisterRequestBody("", "", "", "", "", ""), userRepository) {

    @Test
    fun ifUserOk_WhenChecked_NothingHappens() {
        assertDoesNotThrow { super.validateUserProperties() }
    }

    @Test
    fun ifUserHasBadUsername_WhenChecked_ThrowsUserCheckException() {
        val usernameSmallerThan4Chars = "bad"
        super.user = super.user.copy(username = usernameSmallerThan4Chars)

        assertThrows<UserCheckException> { super.validateUsername() }
    }

    @Test
    fun ifUserHasUsernameAlreadyInUse_WhenChecked_ThrowsUserCheckException() {
        val firstUser = super.userRepository.findAll()[0]
        val mockUsernameInUse = firstUser.username
        super.user = super.user.copy(username = mockUsernameInUse)

        assertThrows<UserCheckException> { super.validateUsername() }
    }
}
