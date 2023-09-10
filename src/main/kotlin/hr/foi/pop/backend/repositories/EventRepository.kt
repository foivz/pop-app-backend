package hr.foi.pop.backend.repositories

import hr.foi.pop.backend.models.event.Event
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EventRepository : JpaRepository<Event, Int> {
    fun getEventByIsActiveTrue(): Event
    fun getEventById(id: Int): Event
}
