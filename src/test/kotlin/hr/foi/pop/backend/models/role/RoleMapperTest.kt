package hr.foi.pop.backend.models.role

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RoleMapperTest {

    val roleMapper = RoleMapper()

    @Test
    fun whenRoleDTOMapped_CheckRoleAttributes_AreEqual( ) {
        val role = Role().apply {
            id = 1
            name = "buyer"
        }

        val roleDto = roleMapper.mapDto(role)

        Assertions.assertTrue(roleDto.id == role.id && roleDto.name == role.name)
    }
}