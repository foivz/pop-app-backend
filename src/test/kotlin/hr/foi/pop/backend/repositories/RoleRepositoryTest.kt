package hr.foi.pop.backend.repositories

import hr.foi.pop.backend.models.role.Role
import org.aspectj.lang.annotation.Before
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

    @Before("getRoleByName_CheckRoleId_IsCorrect")

    @Test
    fun getRoleByName_CheckRoleId_IsCorrect() {
        val buyerRoleId = 1
        
        val role: Role = roleRepository.getRoleById(buyerRoleId)
        assert(role.name == "buyer")

        val roleByName = roleRepository.getRoleByName("buyer")
        assert(roleByName.id == 1)
    }
}
