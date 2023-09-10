package hr.foi.pop.backend.services

import hr.foi.pop.backend.exceptions.BadRoleException
import hr.foi.pop.backend.exceptions.InvalidStoreNameException
import hr.foi.pop.backend.exceptions.UsedStoreNameException
import hr.foi.pop.backend.models.store.Store
import hr.foi.pop.backend.repositories.EventRepository
import hr.foi.pop.backend.repositories.StoreRepository
import hr.foi.pop.backend.utils.MockEntitiesHelper
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser

@SpringBootTest
@Transactional
class StoreServiceTest {
    @Autowired
    lateinit var storeService: StoreService

    @Autowired
    lateinit var storeRepository: StoreRepository

    @Autowired
    lateinit var eventRepository: EventRepository

    private val properAndUniqueStoreName = "${StoreServiceTest::class.simpleName} Store"

    @Test
    @WithMockUser(username = "StoreServiceTester", authorities = ["seller"])
    fun givenProperAndUniqueStoreName_whenUserIsSeller_returnNewStoreEntityObject() {
        val existsByStoreName = storeRepository.existsByStoreName(properAndUniqueStoreName)
        Assertions.assertFalse(existsByStoreName)

        val newStoreEntity: Store = storeService.createStore(properAndUniqueStoreName)

        assertStoreRetrieveableFromRepositoryById(newStoreEntity)
        assertStoreHasPropertyValuesCorrectlySet(properAndUniqueStoreName, newStoreEntity)
    }

    private fun assertStoreRetrieveableFromRepositoryById(newStoreEntity: Store) {
        val persistedStore = storeRepository.getStoreById(newStoreEntity.id)
        Assertions.assertEquals(persistedStore.id, newStoreEntity.id)
    }

    private fun assertStoreHasPropertyValuesCorrectlySet(properAndUniqueStoreName: String, newStoreEntity: Store) {
        Assertions.assertEquals(properAndUniqueStoreName, newStoreEntity.storeName)
        val currentEvent = eventRepository.getEventByIsActiveTrue()
        Assertions.assertEquals(currentEvent.id, newStoreEntity.event.id)
        Assertions.assertEquals(0, newStoreEntity.balance)
    }

    @Test
    @WithMockUser(username = "StoreServiceTester", authorities = ["buyer"])
    fun givenUserThatIsNotAllowedToCreateStores_onAttemptToCreateStore_throwBadRoleException() {
        val ex = assertThrows<BadRoleException> {
            storeService.createStore(properAndUniqueStoreName)
        }

        Assertions.assertEquals("User of type \"buyer\" cannot create stores!", ex.message)
    }

    @Test
    @WithMockUser(username = "StoreServiceTester", authorities = ["seller"])
    fun givenAnInvalidStoreName_onAttemptToCreateStore_throwInvalidStoreNameException() {
        val invalidStoreName = " "

        assertThrows<InvalidStoreNameException> {
            storeService.createStore(invalidStoreName)
        }
    }

    @Test
    @WithMockUser(username = "StoreServiceTester", authorities = ["seller"])
    fun givenAUsedStoreName_onAttemptToCreateStore_throwUsedStoreNameException() {
        val usedStoreName = "a_used_name"
        persistStoreWithName(usedStoreName)

        assertThrows<UsedStoreNameException> {
            storeService.createStore(usedStoreName)
        }
    }

    private fun persistStoreWithName(usedStoreName: String) {
        storeRepository.save(Store().apply {
            storeName = usedStoreName
            event = MockEntitiesHelper.generateEventEntity()
        })
        Assertions.assertTrue(storeRepository.existsByStoreName(usedStoreName))
    }
}
