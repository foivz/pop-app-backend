package hr.foi.pop.backend.services.user_service

import hr.foi.pop.backend.exceptions.*
import hr.foi.pop.backend.repositories.StoreRepository
import hr.foi.pop.backend.repositories.UserRepository
import hr.foi.pop.backend.services.UserService
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser

@SpringBootTest
@Transactional
class UserStoreAssigningTest {
    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var storeRepository: StoreRepository

    @Test
    @WithMockUser(username = "UserServiceTester", authorities = ["admin"])
    fun givenValidBuyerIdAndExistingStoreName_whenAssignedStore_buyerIsAddedToTheStore() {
        val validBuyerId = getValidBuyerIdWithoutStore()
        val existingStoreName = getExistingStoreName()

        userService.assignStore(validBuyerId, existingStoreName)
        val updatedUser = userRepository.getReferenceById(validBuyerId)

        Assertions.assertNotNull(updatedUser.store)
        Assertions.assertEquals(existingStoreName, updatedUser.store!!.storeName)
    }

    private fun getExistingStoreName(): String {
        val existingStoreName = "Store 1"
        Assertions.assertTrue(storeRepository.existsByStoreName(existingStoreName))
        return existingStoreName
    }

    private fun getValidBuyerIdWithoutStore(): Int {
        val validBuyerId = 2
        val validBuyer = userRepository.getReferenceById(validBuyerId)
        Assertions.assertEquals("buyer", validBuyer.role.name)
        Assertions.assertNull(validBuyer.store)
        return validBuyer.id
    }

    @Test
    fun givenValidBuyerIdAndNonExistentStoreName_onStoreAssignAttempt_throwStoreNotFoundException() {
        val validBuyerId = getValidBuyerIdWithoutStore()
        val nonExistentStoreName = "test - store that doesn't exist"

        val ex = assertThrows<StoreNotFoundException> { userService.assignStore(validBuyerId, nonExistentStoreName) }
        Assertions.assertEquals("Store \"$nonExistentStoreName\" not found!", ex.message)
    }

    @Test
    fun givenNonExistentUserId_onStoreAssignAttempt_throwUserNotFoundException() {
        val nonExistentUserId = 9999
        Assertions.assertFalse(userRepository.existsById(nonExistentUserId))
        val storeName = "some store"

        val ex = assertThrows<UserNotFoundException> { userService.assignStore(nonExistentUserId, storeName) }
        Assertions.assertEquals("User with ID $nonExistentUserId not found.", ex.message)
    }

    @Test
    fun givenNonActivatedUserId_onStoreAssignAttempt_throwUserNotAcceptedException() {
        val notAcceptedBuyerId = getNotAcceptedBuyerId()
        val storeName = "some store"

        assertThrows<UserNotAcceptedException> { userService.assignStore(notAcceptedBuyerId, storeName) }
    }

    private fun getNotAcceptedBuyerId(): Int {
        val notAcceptedBuyerId = 4
        val notAcceptedBuyer = userRepository.getReferenceById(notAcceptedBuyerId)
        Assertions.assertEquals("buyer", notAcceptedBuyer.role.name)
        Assertions.assertFalse(notAcceptedBuyer.isAccepted)

        return notAcceptedBuyer.id
    }

    @Test
    fun givenSellerId_onStoreAssignAttempt_throwUserHasStoreException() {
        val sellerUserId: Int = getSellerUserId()
        val storeName = getExistingStoreName()

        val ex = assertThrows<BadRoleException> { userService.assignStore(sellerUserId, storeName) }
        Assertions.assertEquals("Cannot assign store to seller!", ex.message)
    }

    private fun getSellerUserId(): Int {
        val sellerId = 8
        val seller = userRepository.getReferenceById(sellerId)
        Assertions.assertEquals("seller", seller.role.name)
        Assertions.assertTrue(seller.isAccepted)

        return sellerId
    }

    @Test
    fun givenBuyerIdOfBuyerWithStore_onStoreAssignAttempt_throwUserHasStoreException() {
        val buyerUserId: Int = getValidBuyerIdWithoutStore()
        val storeName = getExistingStoreName()

        userService.assignStore(buyerUserId, storeName)

        val ex = assertThrows<UserHasStoreException> { userService.assignStore(buyerUserId, storeName) }
        Assertions.assertEquals("User \"dhuff\" already belongs to store \"$storeName\"!", ex.message)
    }
}
