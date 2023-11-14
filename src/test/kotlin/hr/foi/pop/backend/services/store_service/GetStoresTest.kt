package hr.foi.pop.backend.services.store_service

import hr.foi.pop.backend.exceptions.BadRoleException
import hr.foi.pop.backend.models.store.Store
import hr.foi.pop.backend.services.StoreService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class GetStoresTest {

    @Autowired
    private lateinit var storeService: StoreService

    @Test
    @WithMockUser(username = "StoreGetterTester", authorities = ["buyer"])
    fun givenBuyerUser_whenStoresFetched_receiveAllStores() {
        val stores: List<Store> = storeService.getStores()
        assert(stores.isNotEmpty())
        assert(stores[0].storeName.isNotEmpty())
    }

    @Test
    @WithMockUser(username = "InvalidStoreGetterTester", authorities = ["seller"])
    fun givenSellerUser_onAttemptToFetchStores_throwBadRoleException() {
        assertThrows<BadRoleException> { storeService.getStores() }
    }
}
