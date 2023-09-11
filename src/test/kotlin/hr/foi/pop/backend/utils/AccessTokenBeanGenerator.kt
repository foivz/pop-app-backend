package hr.foi.pop.backend.utils

import hr.foi.pop.backend.models.user.User
import hr.foi.pop.backend.repositories.UserRepository
import hr.foi.pop.backend.services.AuthenticationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy

@Configuration
class AccessTokenBeanGenerator {
    companion object {
        const val ADMIN = "adminAccessToken"
        const val SELLER = "sellerAccessToken"
        const val BUYER = "buyerAccessToken"
    }

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var authenticationService: AuthenticationService

    @Bean
    @Qualifier(ADMIN)
    @Lazy
    fun getAdminAccessToken(): String {
        val mockAdminUserId = 1
        val user = getUserById(mockAdminUserId)
        return generateAccessToken(user)
    }

    @Bean
    @Qualifier(SELLER)
    @Lazy
    fun getSellerAccessToken(): String {
        val mockSellerUserId = 8
        val user = getUserById(mockSellerUserId)
        return generateAccessToken(user)
    }

    @Bean
    @Qualifier(BUYER)
    @Lazy
    fun getBuyerAccessToken(): String {
        val mockBuyerUserId = 2
        val user = getUserById(mockBuyerUserId)
        return generateAccessToken(user)
    }

    private fun getUserById(mockAdminUserId: Int) = userRepository.getReferenceById(mockAdminUserId)

    private fun generateAccessToken(user: User) = authenticationService
        .authenticateAndGenerateTokenPair(user.username, "test123")
        .accessToken

}
