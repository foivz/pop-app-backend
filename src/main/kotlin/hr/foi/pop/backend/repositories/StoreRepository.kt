package hr.foi.pop.backend.repositories

import hr.foi.pop.backend.models.store.Store
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StoreRepository : JpaRepository<Store, Int> {
    fun getStoreById(id: Int): Store
    fun existsByStoreName(storeName: String): Boolean
    fun getStoreByStoreName(storeName: String): Store
}
