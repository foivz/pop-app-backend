package hr.foi.pop.backend.services.user_service

import hr.foi.pop.backend.repositories.UserRepository
import hr.foi.pop.backend.services.UserService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class UserSetBalanceTest {
    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun givenBuyerId_whenBalanceSet_userBalanceChanged() {
        val validBuyerId = 2
        val newBalance = 500

        userService.setBalance(validBuyerId, newBalance)

        val buyer = userRepository.getReferenceById(validBuyerId)

        Assertions.assertEquals(newBalance, buyer.balance)
    }
}
