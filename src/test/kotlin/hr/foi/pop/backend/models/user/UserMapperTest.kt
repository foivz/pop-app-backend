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
        val user = MockEntitiesHelper.generateUserEntity()

        val userDto = userMapper.mapDto(user)

        Assertions.assertTrue(
            user.id == userDto.id &&
                    user.name == userDto.name &&
                    user.surname == userDto.surname &&
                    user.email == userDto.email &&
                    user.username == userDto.username &&
                    user.dateOfRegister == userDto.dateOfRegister &&
                    userDto.role.id == 1
        )
    }
}
