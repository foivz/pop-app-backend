package hr.foi.pop.backend.models.user

import hr.foi.pop.backend.utils.MockEntitiesHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserMapperTest {
    val userMapper = UserMapper()

    @Test
    fun whenUserDTOMapped_CheckUserAttributes_AreEqual() {
        val user = MockEntitiesHelper.generateUserEntityWithStore()

        val userDto = userMapper.mapDto(user)

        Assertions.assertEquals(user.id, userDto.id)
        Assertions.assertEquals(user.name, userDto.name)
        Assertions.assertEquals(user.surname, userDto.surname)
        Assertions.assertEquals(user.email, userDto.email)
        Assertions.assertEquals(user.username, userDto.username)
        Assertions.assertEquals(user.dateOfRegister, userDto.dateOfRegister)
        Assertions.assertEquals(user.event.id, userDto.event)
        Assertions.assertEquals(user.role.id, userDto.role)
        Assertions.assertEquals(user.store!!.id, userDto.store)
    }
}
