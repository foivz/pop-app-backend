package hr.foi.pop.backend.repositories

import hr.foi.pop.backend.models.refresh_token.RefreshToken
import hr.foi.pop.backend.utils.MockEntitiesHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@SpringBootTest
class RefreshTokenRepositoryTest {
    @Autowired
    lateinit var refreshTokenRepository: RefreshTokenRepository

    @Test
    @Transactional
    fun givenTokenForUserExists_whenGottenByOwner_thenTokenRetrieved() {
        val mockUser = MockEntitiesHelper.generateUserEntityWithStore()
        val mockRefreshToken = RefreshToken().apply {
            owner = mockUser
            token = "---------------------------mock_token---------------------------"
            dateCreated = LocalDateTime.now()
            expirationDate = LocalDateTime.now()
        }
        refreshTokenRepository.save(mockRefreshToken)

        val retrievedToken = refreshTokenRepository.getRefreshTokenByOwner(mockUser)

        Assertions.assertEquals(mockRefreshToken.token, retrievedToken.token)
    }
}
