package hr.foi.pop.backend.utils

import hr.foi.pop.backend.exceptions.UserCheckException
import hr.foi.pop.backend.models.user.User
import hr.foi.pop.backend.models.user.UserMapper
import hr.foi.pop.backend.repositories.UserRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

private val mapper = UserMapper()

@SpringBootTest
class UserCheckerTest(@Autowired userRepository: UserRepository) :
    UserChecker(mapper.mapDto(MockEntitiesHelper.generateUserEntity()), userRepository) {

    @Test
    fun ifUserOk_WhenChecked_NothingHappens() {
        assertDoesNotThrow { super.validateUserProperties() }
    }

    @Test
    fun ifUserHasBadUsername_WhenChecked_ThrowsUserCheckException() {
        changeUserProperty { user ->
            val usernameSmallerThan4Chars = "bad"
            user.username = usernameSmallerThan4Chars
        }

        assertThrows<UserCheckException> { super.validateUsername() }
    }

    @Test
    fun ifUserHasUsernameAlreadyInUse_WhenChecked_ThrowsUserCheckException() {
        val firstUser = super.userRepository.findAll()[0]

        changeUserProperty { user ->
            val mockUsernameInUse = firstUser.username
            user.username = mockUsernameInUse
        }

        assertThrows<UserCheckException> { super.validateUsername() }
    }

    private fun changeUserProperty(changeProperty: (user: User) -> Unit) {
        val userEntity = mapper.map(super.user)
        changeProperty(userEntity)
        val userDto = UserMapper().mapDto(userEntity)
        super.user = userDto
    }
}
