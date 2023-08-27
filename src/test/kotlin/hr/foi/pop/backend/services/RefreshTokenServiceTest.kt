package hr.foi.pop.backend.services

import hr.foi.pop.backend.exceptions.RefreshTokenInvalidException
import hr.foi.pop.backend.models.refresh_token.RefreshToken
import hr.foi.pop.backend.repositories.RefreshTokenRepository
import hr.foi.pop.backend.repositories.UserRepository
import hr.foi.pop.backend.utils.MockEntitiesHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDateTime

@SpringBootTest
class RefreshTokenServiceTest {
    @Autowired
    lateinit var refreshTokenService: RefreshTokenService

    @Autowired
    lateinit var refreshTokenRepository: RefreshTokenRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    @Transactional
    fun givenTokenCreated_whenSavedAndRetrieved_dataMatchesOut() {
        val user = userRepository.save(MockEntitiesHelper.generateUserEntityWithStore(this::class))

        val createdRefreshToken = refreshTokenService.createNewRefreshTokenForUser(user)
        val retrievedRefreshTokenEntity = refreshTokenRepository.getRefreshTokenByOwner(user)

        Assertions.assertNotNull(retrievedRefreshTokenEntity)
        Assertions.assertEquals(user.id, retrievedRefreshTokenEntity!!.owner.id)
        Assertions.assertEquals(createdRefreshToken.token, retrievedRefreshTokenEntity.token)
        assertTokenCreationTimeIsCorrect(retrievedRefreshTokenEntity)
        Assertions.assertTrue(retrievedRefreshTokenEntity.dateCreated < retrievedRefreshTokenEntity.expirationDate)
    }

    private fun assertTokenCreationTimeIsCorrect(token: RefreshToken) {
        val currentTime = LocalDateTime.now()
        val timeDifference = Duration.between(currentTime, token.dateCreated).abs()

        val threshold = Duration.ofSeconds(2)
        val creationDateWithinThresholdOfNow = timeDifference < threshold

        Assertions.assertTrue(creationDateWithinThresholdOfNow)
    }

    @Test
    @Transactional
    fun givenNonExistentRefreshToken_whenUserTriesToUseIt_throwsException() {
        val mockRefreshToken = "---------------------------mock_token---------------------------"

        val thrownException = assertThrows<RefreshTokenInvalidException> {
            refreshTokenService.createNewRefreshTokenFromExistingRefreshToken(mockRefreshToken)
        }

        Assertions.assertEquals("Provided refresh token not recognized!", thrownException.message)
    }
}
