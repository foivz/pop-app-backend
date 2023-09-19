package hr.foi.pop.backend.services.user_service

import hr.foi.pop.backend.exceptions.BadRoleException
import hr.foi.pop.backend.exceptions.UserNotAcceptedException
import hr.foi.pop.backend.models.user.User
import hr.foi.pop.backend.services.UserService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserRoleChangeTest {
    @Autowired
    lateinit var userService: UserService

    @Test
    fun givenActivatedBuyerWithStoreSelected_whenRequestedRoleChange_changeRole() {
        val validBuyerId = 2

        val user: User = userService.changeRole(validBuyerId, "seller")

        Assertions.assertEquals("seller", user.role.name)
    }

    @Test
    fun givenActivatedSellerWithStoreSelected_whenRequestedRoleChange_changeRole() {
        val validSellerId = 8

        val user: User = userService.changeRole(validSellerId, "buyer")

        Assertions.assertEquals("buyer", user.role.name)
    }

    @Test
    fun givenAdmin_onRoleRequestChange_throwBadRoleException() {
        val adminUserId = 1

        val ex = assertThrows<BadRoleException> { userService.changeRole(adminUserId, "buyer") }
        Assertions.assertEquals("Cannot change admin user!", ex.message)
    }

    @Test
    fun givenNonActivatedBuyerWithStoreSelected_whenRequestedRoleChange_throwUserNotAcceptedException() {
        val nonActivatedBuyerId = 4

        assertThrows<UserNotAcceptedException> { userService.changeRole(nonActivatedBuyerId, "buyer") }
    }

    @Test
    fun givenValidBuyer_whenRequestedRoleChangeToNonExistentRole_throwBadRoleException() {
        val validBuyerId = 2
        val nonExistentRoleName = "test role that doesn't exist"

        val ex = assertThrows<BadRoleException> { userService.changeRole(validBuyerId, nonExistentRoleName) }
        Assertions.assertEquals("Cannot give user \"dhuff\" role \"$nonExistentRoleName\"!", ex.message)
    }
}
