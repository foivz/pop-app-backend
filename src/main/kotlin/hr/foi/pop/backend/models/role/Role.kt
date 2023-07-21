package hr.foi.pop.backend.models.role

import jakarta.persistence.*

@Entity
@Table(name = "roles")
class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_role")
    var id: Int? = null

    @Column(name = "role_name")
    var name: String? = null
}
