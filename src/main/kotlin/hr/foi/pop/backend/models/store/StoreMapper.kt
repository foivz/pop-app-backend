package hr.foi.pop.backend.models.store

import hr.foi.pop.backend.utils.GenericMapper

class StoreMapper : GenericMapper<StoreDTO, Store> {
    override fun mapDto(e: Store): StoreDTO {
        return StoreDTO(e.id, e.event, e.storeName, e.balance, e.latitude, e.longitude)
    }

    override fun map(d: StoreDTO): Store {
        return Store().apply {
            id = d.id
            event = d.event
            storeName = d.storeName
            balance = d.balance
            latitude = d.latitude
            longitude = d.longitude
        }
    }
}
