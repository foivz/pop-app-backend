package hr.foi.pop.backend.repositories

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
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
}
