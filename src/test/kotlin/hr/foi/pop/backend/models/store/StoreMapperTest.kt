package hr.foi.pop.backend.models.store

import hr.foi.pop.backend.utils.MockEntitiesHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class StoreMapperTest {

    val storeMapper = StoreMapper()

    @Test
    fun whenStoreDTOMapped_checkStoreAttributes_areEqual() {
        val store = MockEntitiesHelper.generateStoreEntity()

        val storeDto = storeMapper.mapDto(store)

        Assertions.assertTrue(store.id == storeDto.id && store.storeName == storeDto.storeName)
    }
}
