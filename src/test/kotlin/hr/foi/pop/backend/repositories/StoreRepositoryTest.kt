package hr.foi.pop.backend.repositories

import hr.foi.pop.backend.utils.MockEntitiesHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class StoreRepositoryTest {
    @Autowired
    lateinit var storeRepository: StoreRepository

    @Test
    fun findAllAndCount_whereEventIsActive_shouldReturn2Elements() {
        val activeEvents: Int = storeRepository.findAll().count {
            it.event.isActive
        }
        Assertions.assertEquals(2, activeEvents)
    }

    @Test
    fun givenStoreExists_whenRetrievedById_shouldReturnStore() {
        val store = MockEntitiesHelper.generateStoreEntity()

        storeRepository.save(store)

        val retrievedStore = storeRepository.getStoreById(store.id)
        Assertions.assertNotNull(retrievedStore)
    }
}
