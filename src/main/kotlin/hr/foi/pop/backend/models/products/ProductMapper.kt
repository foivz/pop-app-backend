package hr.foi.pop.backend.models.products

import hr.foi.pop.backend.utils.GenericMapper

class ProductMapper : GenericMapper<ProductDTO, Product> {
    override fun mapDto(e: Product): ProductDTO {
        return ProductDTO(e.id, e.name, e.description, e.imageUrl, e.price, e.quantity)
    }

    override fun map(d: ProductDTO): Product {
        return Product().apply {
            id = d.id
            name = d.name
            description = d.description
            imageUrl = d.imageUrl
            price = d.price
            quantity = d.quantity
        }
    }
}
