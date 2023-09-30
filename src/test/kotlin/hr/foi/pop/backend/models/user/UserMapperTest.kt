package hr.foi.pop.backend.models.user

import hr.foi.pop.backend.utils.MockEntitiesHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserMapperTest {
    val userMapper = UserMapper()

    @Test
    fun whenUserDTOMapped_checkUserAttributes_areEqual() {
        val user = MockEntitiesHelper.generateBuyerUserEntityWithStore(this::class)

        val userDto = userMapper.mapDto(user)

        Assertions.assertEquals(user.id, userDto.id)
        Assertions.assertEquals(user.firstName, userDto.firstName)
        Assertions.assertEquals(user.lastName, userDto.lastName)
        Assertions.assertEquals(user.email, userDto.email)
        Assertions.assertEquals(user.username, userDto.username)
        Assertions.assertEquals(user.dateOfRegister, userDto.dateOfRegister)
        Assertions.assertEquals(user.event.id, userDto.event)
        Assertions.assertEquals(user.role.name, userDto.role)
        Assertions.assertNotNull(user.store)
        val storeDto = userDto.store
        Assertions.assertNotNull(storeDto)
        Assertions.assertEquals(user.store!!.id, storeDto!!.storeId)
        Assertions.assertEquals(user.store!!.storeName, storeDto.storeName)
    }
}
