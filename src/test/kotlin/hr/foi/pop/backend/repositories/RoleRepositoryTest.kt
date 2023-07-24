package hr.foi.pop.backend.repositories

import hr.foi.pop.backend.models.role.Role
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RoleRepositoryTest {

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Test
    fun getRoleById_CheckRoleName_IsCorrect() {
        val role: Role = roleRepository.getRoleById(3)

        Assertions.assertTrue(role.name == "admin")
    }

}