package hr.foi.pop.backend.models.packages

import hr.foi.pop.backend.models.products.ProductDTO
import hr.foi.pop.backend.models.store.StoreDTO

data class PackageDTO(
    val id: Int,
    val name: String,
    val description: String,
    val imageUrl: String?,
    val discount: Int,
    val amount: Int?,
    val store: StoreDTO,
    val products: List<ProductDTO>
)
