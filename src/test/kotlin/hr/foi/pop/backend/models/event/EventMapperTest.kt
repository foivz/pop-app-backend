package hr.foi.pop.backend.models.event

import hr.foi.pop.backend.utils.MockEntitiesHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class EventMapperTest {
    val eventMapper = EventMapper()

    @Test
    fun whenEventDTOMapped_CheckEventAttributes_AreEqual() {
        val event = MockEntitiesHelper.generateEventEntity()

        val eventDTO = eventMapper.mapDto(event)

        Assertions.assertTrue(
            event.id == eventDTO.id &&
                    event.name == eventDTO.name &&
                    event.dateCreated == eventDTO.dateCreated &&
                    event.isActive == eventDTO.isActive
        )
    }
}
