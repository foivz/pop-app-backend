package hr.foi.pop.backend.repositories

import hr.foi.pop.backend.models.user.User
import hr.foi.pop.backend.utils.MockEntitiesHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryTest {

    @Autowired
    lateinit var userRepository: UserRepository

    val mockUser = MockEntitiesHelper.generateUserEntityWithStore()

    @BeforeAll
    fun addMockUserToDatabase() {
        userRepository.save(mockUser)
        val mockUserUsername = mockUser.username
        val mockUserIsSaved = userRepository.existsByUsername(mockUserUsername)
        Assertions.assertTrue(mockUserIsSaved)
    }

    @Test
    fun findById_WhenGivenIdOfMockUser_ExpectMockUser() {
        val optionalUserObject: Optional<User> = userRepository.findById(mockUser.id)
        Assertions.assertTrue(optionalUserObject.isPresent)
        val user = optionalUserObject.get()
        Assertions.assertEquals(mockUser.firstName, user.firstName)
        Assertions.assertEquals(mockUser.lastName, user.lastName)
    }
}
