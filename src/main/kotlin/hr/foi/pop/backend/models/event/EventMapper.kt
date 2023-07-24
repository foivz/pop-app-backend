package hr.foi.pop.backend.models.event

import hr.foi.pop.backend.utils.GenericMapper

class EventMapper : GenericMapper<EventDTO, Event> {
    override fun mapDto(e: Event): EventDTO {
        return EventDTO(e.id, e.name, e.dateCreated, e.isActive)
    }

    override fun map(d: EventDTO): Event {
        return Event().apply {
            id = d.id
            name = d.name
            dateCreated = d.dateCreated
            isActive = d.isActive
        }
    }

}
