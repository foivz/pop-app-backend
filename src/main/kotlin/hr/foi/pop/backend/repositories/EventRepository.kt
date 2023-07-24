package hr.foi.pop.backend.repositories

import hr.foi.pop.backend.models.event.Event
import org.springframework.data.jpa.repository.JpaRepository

interface EventRepository : JpaRepository<Event, Int>
