package hr.foi.pop.backend.models.role

import hr.foi.pop.backend.utils.MockEntitiesHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RoleMapperTest {

    val roleMapper = RoleMapper()

    @Test
    fun whenRoleDTOMapped_checkRoleAttributes_areEqual() {
        val role = MockEntitiesHelper.generateBuyerRoleEntity()

        val roleDto = roleMapper.mapDto(role)

        Assertions.assertTrue(roleDto.id == role.id && roleDto.name == role.name)
        Assertions.assertTrue(roleDto.id == 1 && roleDto.name == "buyer")
    }
}
