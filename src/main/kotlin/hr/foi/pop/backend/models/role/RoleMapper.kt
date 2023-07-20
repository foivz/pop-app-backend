package hr.foi.pop.backend.models.role

import hr.foi.pop.backend.utils.GenericMapper

class RoleMapper : GenericMapper<RoleDTO, Role> {
    override fun mapDto(e: Role): RoleDTO {
        return RoleDTO(e.id!!, e.name!!)
    }

    override fun map(d: RoleDTO): Role {
        return Role().apply {
            id = d.id
            name = d.name
        }
    }
}