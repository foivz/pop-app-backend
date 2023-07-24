package hr.foi.pop.backend.models.packages

import hr.foi.pop.backend.models.products.ProductMapper
import hr.foi.pop.backend.models.store.StoreMapper
import hr.foi.pop.backend.utils.GenericMapper

class PackageMapper : GenericMapper<PackageDTO, PackageEntity> {
    private val productMapper = ProductMapper()
    private val storeMapper = StoreMapper()

    override fun mapDto(e: PackageEntity): PackageDTO {
        return PackageDTO(
            e.id,
            e.name,
            e.description,
            e.imageUrl,
            e.discount,
            e.amount,
            storeMapper.mapDto(e.store),
            e.products.map {
                productMapper.mapDto(it)
            }
        )
    }

    override fun map(d: PackageDTO): PackageEntity {
        return PackageEntity().apply {
            id = d.id
            name = d.name
            description = d.description
            imageUrl = d.imageUrl
            discount = d.discount
            amount = d.amount
            store = storeMapper.map(d.store)
            products = d.products.map {
                productMapper.map(it)
            }.toSet()
        }
    }
}
