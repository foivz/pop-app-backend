package hr.foi.pop.backend.services.user_service

import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.exceptions.BadAmountException
import hr.foi.pop.backend.exceptions.UserNotFoundException
import hr.foi.pop.backend.repositories.UserRepository
import hr.foi.pop.backend.services.UserService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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

    @Test
    fun givenIdOfNonExistentUser_onAttemptToSetBalance_throwUserNotFoundException() {
        val invalidUserId = 999
        val newBalance = 500

        assertThrows<UserNotFoundException> { userService.setBalance(invalidUserId, newBalance) }
    }

    @Test
    fun givenBuyerIdAndTooLargeAmount_onAttemptToSetBalance_throwBadAmountException() {
        val validBuyerId = 2
        val maxBalanceAmount = 999999

        userService.setBalance(validBuyerId, maxBalanceAmount)

        val tooBigBalanceAmount = maxBalanceAmount + 1

        val ex = assertThrows<BadAmountException> { userService.setBalance(validBuyerId, tooBigBalanceAmount) }
        Assertions.assertEquals(ApplicationErrorType.ERR_AMOUNT_TOO_LARGE, ex.error)
    }

    @Test
    fun givenBuyerIdAnNegativeAmount_onAttemptToSetBalance_throwBadAmountException() {
        val validBuyerId = 2
        val negativeBalanceAmount = -1

        val ex = assertThrows<BadAmountException> { userService.setBalance(validBuyerId, negativeBalanceAmount) }
        Assertions.assertEquals(ApplicationErrorType.ERR_AMOUNT_NEGATIVE, ex.error)
    }
}
