package hr.foi.pop.backend.models.user

import hr.foi.pop.backend.exceptions.UserBuilderException
import hr.foi.pop.backend.repositories.EventRepository
import hr.foi.pop.backend.utils.MockEntitiesHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootTest
class UserBuilderTest {
    private val mockedUser = MockEntitiesHelper.generateBuyerUserEntityWithStore(this::class)

    @Autowired
    lateinit var eventRepository: EventRepository

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Test
    fun givenCorrectUserInfo_whenBuiltViaBuilder_propertiesEqual() {
        val userBuilder = getBuilderForMockUser()
        val user = userBuilder.build()

        Assertions.assertEquals(mockedUser.username, user.username)
        Assertions.assertEquals(mockedUser.firstName, user.firstName)
        Assertions.assertEquals(mockedUser.lastName, user.lastName)
        Assertions.assertEquals(mockedUser.email, user.email)
        Assertions.assertEquals(mockedUser.role, user.role)
        assertIsBCryptHash(user.passwordHash)
    }

    private fun getBuilderForMockUser(): UserBuilder =
        UserBuilder()
            .setUsername(mockedUser.username)
            .setFirstName(mockedUser.firstName)
            .setLastName(mockedUser.lastName)
            .setEmail(mockedUser.email)
            .setRole(mockedUser.role)
            .setCurrentEvent(eventRepository.getEventByIsActiveTrue())
            .setPassword("readable_password", passwordEncoder)

    private fun assertIsBCryptHash(password: String) {
        Assertions.assertTrue(password.startsWith("\$2a\$10\$"))
    }

    @Test
    fun givenBadUserInfo_onAttemptToBuildViaBuilder_throwsUserBuilderException() {
        val userBuilder = getBuilderForMockUser()

        userBuilder.setUsername("")
        userBuilder.setLastName("")

        val thrownException = assertThrows<UserBuilderException> {
            userBuilder.build()
        }
        Assertions.assertEquals("User has no last name, username set!", thrownException.message)
    }

    @Test
    fun givenEmptyBuilder_onAttemptToBuild_throwsUserBuilderExceptionAfterFirstNameAttribute() {
        val thrownException = assertThrows<UserBuilderException> {
            UserBuilder().build()
        }
        Assertions.assertEquals("User has no firstName set!", thrownException.message)
    }

    @Test
    fun givenBuilderWithEmptyStrings_onAttemptToBuild_throwsUserBuilderExceptionWithAllAttributesListed() {
        val thrownException = assertThrows<UserBuilderException> {
            UserBuilder()
                .setFirstName("")
                .setLastName("")
                .setPassword("", passwordEncoder)
                .setUsername("")
                .setEmail("")
                .setPassword("", passwordEncoder)
                .build()
        }
        Assertions.assertEquals(
            "User has no first name, last name, email, username, password, event, role set!",
            thrownException.message
        )
    }
}
