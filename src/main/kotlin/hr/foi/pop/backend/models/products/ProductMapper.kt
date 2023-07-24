package hr.foi.pop.backend.models.products

import hr.foi.pop.backend.models.store.StoreMapper
import hr.foi.pop.backend.utils.GenericMapper

class ProductMapper : GenericMapper<ProductDTO, Product> {
    private val storeMapper = StoreMapper()

    override fun mapDto(e: Product): ProductDTO {
        val imgUrl = e.imageUrl ?: "./img/default_product_image.png"
        return ProductDTO(e.id, e.name, e.description, imgUrl, e.price, e.quantity, storeMapper.mapDto(e.store))
    }

    override fun map(d: ProductDTO): Product {
        return Product().apply {
            id = d.id
            name = d.name
            description = d.description
            imageUrl = d.imageUrl
            price = d.price
            quantity = d.quantity
            store = storeMapper.map(d.store)
        }
    }
}
