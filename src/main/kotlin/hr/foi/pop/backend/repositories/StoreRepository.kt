package hr.foi.pop.backend.repositories

import hr.foi.pop.backend.models.store.Store
import org.springframework.data.jpa.repository.JpaRepository

interface StoreRepository : JpaRepository<Store, Int>
