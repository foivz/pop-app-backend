package hr.foi.pop.backend.controllers.store_controller

import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.models.user.User
import hr.foi.pop.backend.repositories.UserRepository
import hr.foi.pop.backend.services.AuthenticationService
import hr.foi.pop.backend.utils.JsonMockRequestGenerator
import hr.foi.pop.backend.utils.MockEntitiesHelper
import hr.foi.pop.backend.utils.MockMvcBuilderManager
import jakarta.transaction.Transactional
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class StoreControllerCreationTest {

    @Autowired
    lateinit var context: WebApplicationContext

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var authenticationService: AuthenticationService

    lateinit var mockActivatedSeller: User

    lateinit var accessToken: String

    private val mockUsersPassword = "test123"

    private val mockNewStoreName = "SuperStore"

    lateinit var mvc: MockMvc

    @BeforeAll
    fun setup() {
        mvc = MockMvcBuilderManager.getMockMvc(context, StoreControllerCreationTest::class)

        val generatedMockUser =
            MockEntitiesHelper.generateBuyerUserEntityWithoutStore(
                StoreControllerCreationTest::class,
                mockUsersPassword
            )
        generatedMockUser.apply {
            role = MockEntitiesHelper.generateSellerRoleEntity()
        }
        mockActivatedSeller = userRepository.save(generatedMockUser)

        val validTokenPair =
            authenticationService.authenticateAndGenerateTokenPair(generatedMockUser.username, mockUsersPassword)
        accessToken = validTokenPair.accessToken
    }

    @Test
    fun givenAcceptedUserWithRoleSeller_whenPostRequestSent_thenStatus200() {
        Assertions.assertTrue(mockActivatedSeller.isAccepted)
        Assertions.assertEquals("seller", mockActivatedSeller.role.name)

        val requestBody = mapOf("store_name" to mockNewStoreName)
        val request = createAuthorizedRequestWithBody(requestBody, accessToken)

        mvc.perform(request)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("success").value(true))
            .andExpect(jsonPath("message").value(Matchers.matchesPattern("Store \"$mockNewStoreName\" created with ID \\d+.")))
            .andExpect(jsonPath("data[0].id").isNumber)
            .andExpect(jsonPath("data[0].store_name").value(mockNewStoreName))
    }

    @Test
    fun givenBadRequestBody_whenPostRequestSent_thenStatus400() {
        val badRequestBody = mapOf("badAttribute" to "nonsenseValue")

        val request = createAuthorizedRequestWithBody(badRequestBody, accessToken)

        mvc.perform(request).andExpect(status().isBadRequest)
    }

    private fun createAuthorizedRequestWithBody(requestBody: Any, accessToken: String): MockHttpServletRequestBuilder {
        val request = JsonMockRequestGenerator(STORE_ROUTE).getRequestWithJsonBody(requestBody)
        request.header("Authorization", "Bearer $accessToken")
        return request
    }

    @Test
    fun givenBadStoreName_whenProperRequestSent_thenStatus400() {
        val requestBodyWithBadStoreName = mapOf("store_name" to "  ")

        val request = createAuthorizedRequestWithBody(requestBodyWithBadStoreName, accessToken)

        mvc.perform(request)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("success").value(false))
            .andExpect(jsonPath("message").value("Could not create a store with provided name!"))
            .andExpect(jsonPath("error_code").value(ApplicationErrorType.ERR_STORE_NAME_INVALID.code))
            .andExpect(jsonPath("error_message").value(ApplicationErrorType.ERR_STORE_NAME_INVALID.name))
    }

    @Test
    fun givenAcceptedUserWithRoleBuyer_whenPostRequestSent_thenStatus403() {
        val mockBuyerUser = getPersistedMockBuyerUser()

        val accessTokenOfMockBuyerUser =
            authenticationService.authenticateAndGenerateTokenPair(
                mockBuyerUser.username,
                mockUsersPassword
            ).accessToken

        val requestBody = mapOf("store_name" to mockNewStoreName)
        val request = createAuthorizedRequestWithBody(requestBody, accessTokenOfMockBuyerUser)

        mvc.perform(request)
            .andExpect(status().isForbidden)
            .andExpect(jsonPath("success").value(false))
            .andExpect(jsonPath("message").value("User of type \"buyer\" cannot create stores!"))
            .andExpect(jsonPath("error_code").value(ApplicationErrorType.ERR_ROLE_NOT_APPLICABLE.code))
            .andExpect(jsonPath("error_message").value(ApplicationErrorType.ERR_ROLE_NOT_APPLICABLE.name))
    }

    private fun getPersistedMockBuyerUser(): User {
        val mockBuyerUser =
            MockEntitiesHelper.generateBuyerUserEntityWithoutStore(
                StoreControllerCreationTest::class,
                mockUsersPassword
            )
        mockBuyerUser.apply { username = "StoreControllerCreationTesterBuyerUser" }
        userRepository.save(mockBuyerUser)
        Assertions.assertEquals("buyer", mockBuyerUser.role.name)
        return mockBuyerUser
    }

    @Test
    fun givenProperUserWithStoreAlreadyAttached_onAttemptToCreateStoreWithPostRequest_return400() {
        val mockSellerUserWithStore = getPersistedMockSellerUserWithStore()

        val accessTokenOfMockBuyerUser =
            authenticationService.authenticateAndGenerateTokenPair(
                mockSellerUserWithStore.username,
                mockUsersPassword
            ).accessToken

        val requestBody = mapOf("store_name" to "NewStoreImNotAllowedToHave")

        val request = createAuthorizedRequestWithBody(requestBody, accessTokenOfMockBuyerUser)

        mvc.perform(request)
            .andExpect(status().isConflict)
            .andExpect(jsonPath("success").value(false))
            .andExpect(jsonPath("message").value("User already has a store!"))
            .andExpect(jsonPath("error_code").value(ApplicationErrorType.ERR_SELLER_ALREADY_HAS_STORE.code))
            .andExpect(jsonPath("error_message").value(ApplicationErrorType.ERR_SELLER_ALREADY_HAS_STORE.name))
    }

    private fun getPersistedMockSellerUserWithStore(): User {
        val mockSellerUserWithStore =
            MockEntitiesHelper
                .generateBuyerUserEntityWithStore(
                    StoreControllerCreationTest::class, mockUsersPassword
                )

        mockSellerUserWithStore.apply {
            username = "StoreControllerCreationTesterSellerWithStore"
            role = MockEntitiesHelper.generateSellerRoleEntity()
        }

        userRepository.save(mockSellerUserWithStore)
        Assertions.assertEquals("seller", mockSellerUserWithStore.role.name)
        Assertions.assertNotNull(mockSellerUserWithStore.store)

        return mockSellerUserWithStore
    }
}
