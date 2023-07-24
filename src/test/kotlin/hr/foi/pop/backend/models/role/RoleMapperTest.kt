package hr.foi.pop.backend.models.role

import hr.foi.pop.backend.utils.MockEntitiesHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RoleMapperTest {

    val roleMapper = RoleMapper()

    @Test
    fun whenRoleDTOMapped_CheckRoleAttributes_AreEqual() {
        val role = MockEntitiesHelper.generateRoleEntity()

        val roleDto = roleMapper.mapDto(role)

        Assertions.assertTrue(roleDto.id == role.id && roleDto.name == role.name)
        Assertions.assertTrue(roleDto.id == 1 && roleDto.name == "buyer")
    }
}
