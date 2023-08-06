package hr.foi.pop.backend.models.user

import hr.foi.pop.backend.exceptions.UserBuilderException
import hr.foi.pop.backend.repositories.EventRepository
import hr.foi.pop.backend.utils.MockEntitiesHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserBuilderTest {
    private val userBuilder = UserBuilder()
    private val mockedUser = MockEntitiesHelper.generateUserEntityWithStore()

    @Autowired
    lateinit var eventRepository: EventRepository

    @Test
    fun givenCorrectUserInfo_WhenBuiltViaBuilder_PropertiesEqual() {
        val userBuilder = getBuilderForMockUser()
        val user = userBuilder.build()

        Assertions.assertEquals(mockedUser.username, user.username)
        Assertions.assertEquals(mockedUser.name, user.name)
        Assertions.assertEquals(mockedUser.surname, user.surname)
        Assertions.assertEquals(mockedUser.email, user.email)
        Assertions.assertEquals(mockedUser.role, user.role)
        assertIsBCryptHash(user.passwordHash)
    }

    private fun getBuilderForMockUser(): UserBuilder =
        userBuilder
            .setUsername(mockedUser.username)
            .setFirstName(mockedUser.name)
            .setLastName(mockedUser.surname)
            .setEmail(mockedUser.email)
            .setRole(mockedUser.role)
            .setCurrentEvent(eventRepository.getEventByIsActiveTrue())
            .setPassword("readable_password")

    private fun assertIsBCryptHash(password: String) {
        Assertions.assertTrue(password.startsWith("\$2a\$10\$"))
    }

    @Test
    fun givenBadUserInfo_OnAttemptToBuildViaBuilder_ThrowsUserBuilderException() {
        val userBuilder = getBuilderForMockUser()

        userBuilder.setUsername("")
        userBuilder.setLastName("")

        val thrownException = assertThrows<UserBuilderException> {
            userBuilder.build()
        }
        assert(thrownException.message == "User has no last name, username set!")
    }
}
