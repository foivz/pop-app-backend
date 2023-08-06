package hr.foi.pop.backend.utils

import hr.foi.pop.backend.models.event.Event
import hr.foi.pop.backend.models.invoice.Invoice
import hr.foi.pop.backend.models.packages.PackageEntity
import hr.foi.pop.backend.models.products.Product
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

        fun generateUserEntityWithStore(): User {
            return User().apply {
                this.id = 3
                this.firstName = "Tester"
                this.lastName = "Testermann"
                this.email = "tester@test.com"
                this.passwordHash = "937e8d5fbb48bd4949536cd65b8d35c426b80d2f830c5c308e2cdec422ae2244"
                this.username = "testerUsername"
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
                this.user = generateUserEntityWithStore()
                this.store = generateStoreEntity()
                this.total = 20
                this.discount = 350
            }
        }

        fun generateProductEntity(): Product {
            return Product().apply {
                id = 1
                name = "Product 1"
                description = "Cool Test Product"
                imageUrl = "./img/1/img_1_6_2_18:00:00_21-07-2023.png"
                price = 10
                quantity = 3
                store = generateStoreEntity()
            }
        }

        fun generateProductEntityWithNoImage(): Product {
            return Product().apply {
                id = 2
                name = "Product 2"
                description = "Cool Test Product 2"
                imageUrl = null
                price = 10
                quantity = 3
                store = generateStoreEntity()
            }
        }

        fun generatePackageEntityWithoutProducts(): PackageEntity {
            return PackageEntity().apply {
                id = 1
                name = "Package 1"
                description = "Cool Test Package"
                imageUrl = "./img/1/img_1_6_2_18:00:00_21-07-2023.png"
                discount = 10
                amount = 3
                products = setOf()
                store = generateStoreEntity()
            }
        }

        fun generatePackageEntityWithTwoProducts(): PackageEntity {
            return PackageEntity().apply {
                id = 1
                name = "Package 1"
                description = "Cool Test Package"
                imageUrl = "./img/1/img_1_6_2_18:00:00_21-07-2023.png"
                discount = 10
                amount = 3
                products = setOf(
                    generateProductEntity(),
                    generateProductEntityWithNoImage()
                )
                store = generateStoreEntity()
            }
        }
    }
}
