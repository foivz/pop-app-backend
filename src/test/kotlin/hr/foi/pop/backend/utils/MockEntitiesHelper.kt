package hr.foi.pop.backend.utils

import hr.foi.pop.backend.models.event.Event
import hr.foi.pop.backend.models.invoice.Invoice
import hr.foi.pop.backend.models.role.Role
import hr.foi.pop.backend.models.store.Store
import hr.foi.pop.backend.models.user.User
import java.time.LocalDateTime

class MockEntitiesHelper {
    companion object {
        fun generateEventEntity(): Event {
            return Event().apply {
                this.id = 1
                this.name = "Event 1"
                this.dateCreated = LocalDateTime.now()
                this.isActive = true
            }
        }

        fun generateRoleEntity(): Role {
            return Role().apply {
                this.id = 1
                this.name = "buyer"
            }
        }

        fun generateUserEntity(): User {
            return User().apply {
                this.id = 3
                this.name = "Dayton"
                this.surname = "Huff"
                this.email = "dhuff@pop.app"
                this.passwordSalt = "d125a874318684c7d491a20acbd3b879"
                this.passwordHash = "937e8d5fbb48bd4949536cd65b8d35c426b80d2f830c5c308e2cdec422ae2244"
                this.username = "dhuff"
                this.isAccepted = true
                this.balance = 300
                this.role = generateRoleEntity()
                this.event = generateEventEntity()
                this.store = generateStoreEntity()
                this.dateOfRegister = LocalDateTime.now()
            }
        }

        fun generateStoreEntity(): Store {
            return Store().apply {
                this.id = 1
                this.storeName = "Store 1"
                this.event = generateEventEntity()
                this.balance = 0
            }
        }

        fun generateInvoiceEntity(): Invoice {
            return Invoice().apply {
                this.id = 3
                this.code = "INV003"
                this.dateIssue = LocalDateTime.now()
                this.user = generateUserEntity()
                this.store = generateStoreEntity()
                this.total = 20
                this.discount = 350
            }
        }
    }
}
