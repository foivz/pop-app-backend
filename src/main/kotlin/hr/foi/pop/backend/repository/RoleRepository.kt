package hr.foi.pop.backend.repository

import hr.foi.pop.backend.models.role.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : JpaRepository<Role, Int> {
    fun getRoleById(id: Int): Role
}