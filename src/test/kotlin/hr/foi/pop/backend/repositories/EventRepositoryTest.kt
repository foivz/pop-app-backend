package hr.foi.pop.backend.repositories

import hr.foi.pop.backend.models.event.Event
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class EventRepositoryTest {
    @Autowired
    lateinit var eventRepository: EventRepository

    @Test
    fun findAllAndFilter_activeEvents_shouldReturnSingleEvent() {
        val eventList: List<Event> = eventRepository.findAll().filter {
            it.isActive
        }

        Assertions.assertTrue(eventList.count() == 1)
    }
}
