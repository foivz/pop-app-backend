package hr.foi.pop.backend.repositories

import hr.foi.pop.backend.models.user.User
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun findById_whenGivenIdOfOne_shouldReturnCatherineVelasquez() {
        val user: Optional<User> = userRepository.findById(1)
        Assertions.assertTrue(user.isPresent && user.get().firstName == "Catherine" && user.get().lastName == "Velasquez")
    }
}
