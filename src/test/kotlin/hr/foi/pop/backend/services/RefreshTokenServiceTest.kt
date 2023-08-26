package hr.foi.pop.backend.services

import hr.foi.pop.backend.models.refresh_token.RefreshToken
import hr.foi.pop.backend.repositories.RefreshTokenRepository
import hr.foi.pop.backend.utils.MockEntitiesHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
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

    @Test
    @Transactional
    fun givenTokenCreated_whenSavedAndRetrieved_dataMatchesOut() {
        val user = MockEntitiesHelper.generateUserEntityWithStore()

        val createdRefreshTokenString = refreshTokenService.createNewRefreshTokenForUser(user)
        val retrievedRefreshTokenEntity = refreshTokenRepository.getRefreshTokenByOwner(user)

        Assertions.assertEquals(user.id, retrievedRefreshTokenEntity.owner.id)
        Assertions.assertEquals(createdRefreshTokenString, retrievedRefreshTokenEntity.token)
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
}
