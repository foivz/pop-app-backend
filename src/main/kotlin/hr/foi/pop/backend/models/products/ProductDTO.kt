package hr.foi.pop.backend.models.products

import hr.foi.pop.backend.models.store.StoreDTO

data class ProductDTO(
    val id: Int,
    val name: String,
    val description: String,
    val imageUrl: String,
    val price: Int,
    val quantity: Int,
    val store: StoreDTO,
)
