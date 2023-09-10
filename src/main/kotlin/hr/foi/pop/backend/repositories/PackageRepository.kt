package hr.foi.pop.backend.repositories

import hr.foi.pop.backend.models.packages.PackageEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PackageRepository : JpaRepository<PackageEntity, Int> {
    fun getPackageById(id: Int): PackageEntity
}
