package hr.foi.pop.backend.services

import hr.foi.pop.backend.exceptions.BadRoleException
import hr.foi.pop.backend.exceptions.InvalidStoreNameException
import hr.foi.pop.backend.exceptions.UsedStoreNameException
import hr.foi.pop.backend.exceptions.UserHasStoreException
import hr.foi.pop.backend.models.store.Store
import hr.foi.pop.backend.models.user.User
import hr.foi.pop.backend.repositories.EventRepository
import hr.foi.pop.backend.repositories.StoreRepository
import hr.foi.pop.backend.repositories.UserRepository
import hr.foi.pop.backend.utils.MockEntitiesHelper
import jakarta.transaction.Transactional
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StoreServiceTest {
    @Autowired
    lateinit var storeService: StoreService

    @Autowired
    lateinit var storeRepository: StoreRepository

    @Autowired
    lateinit var eventRepository: EventRepository

    @Autowired
    lateinit var userRepository: UserRepository

    private val properAndUniqueStoreName = "${StoreServiceTest::class.simpleName} Store"

    lateinit var mockUser: User

    @BeforeAll
    fun setup() {
        mockUser = MockEntitiesHelper.generateBuyerUserEntityWithoutStore(StoreServiceTest::class)
        mockUser.apply { username = "StoreServiceTester" }
        userRepository.save(mockUser)
    }

    @Test
    @WithMockUser(username = "StoreServiceTester", authorities = ["seller"])
    fun givenProperAndUniqueStoreName_whenUserIsSeller_returnNewStoreEntityObject() {
        assertStoreDoesntExist(properAndUniqueStoreName)

        val newStoreEntity: Store = storeService.createStore(properAndUniqueStoreName)

        assertStoreRetrieveableFromRepositoryById(newStoreEntity)
        assertStoreHasPropertyValuesCorrectlySet(properAndUniqueStoreName, newStoreEntity)
    }

    private fun assertStoreDoesntExist(properAndUniqueStoreName: String) {
        val existsByStoreName = storeRepository.existsByStoreName(properAndUniqueStoreName)
        Assertions.assertFalse(existsByStoreName)
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

    private fun persistStoreWithName(usedStoreName: String): Store {
        val createdStore = storeRepository.save(Store().apply {
            storeName = usedStoreName
            event = MockEntitiesHelper.generateEventEntity()
        })
        Assertions.assertTrue(storeRepository.existsByStoreName(usedStoreName))

        return createdStore
    }

    @Test
    @WithMockUser(username = "evilTesterUserWhoWantsTwoStores", authorities = ["seller"])
    fun givenUserWithStore_onAttemptToCreateAnotherStore_throwUserHasStoreException() {
        assertMockUserHasStore()

        assertThrows<UserHasStoreException> {
            storeService.createStore("a new store")
        }
    }

    private fun assertMockUserHasStore() {
        val newStoreEntity: Store = persistStoreWithName("alreadyExistingStore")
        val mockUserWithStore = mockUser.apply {
            username = "evilTesterUserWhoWantsTwoStores"
            store = storeRepository.save(newStoreEntity)
        }
        val savedUser = userRepository.save(mockUserWithStore)
        Assertions.assertNotNull(savedUser.store)
    }
}
