package hr.foi.pop.backend.models.packages

import hr.foi.pop.backend.models.products.Product
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PackageMapperTest {

    val packageMapper = PackageMapper()

    @Test
    fun whenPackageDTOMapped_CheckPackageAttributes_AreEqual() {
        val packageEntity = PackageEntity().apply {
            id = 1
            name = "Package 1"
            description = "Cool Test Package"
            imageUrl = "./img/1/img_1_6_2_18:00:00_21-07-2023.png"
            discount = 10
            amount = 3
            products = setOf()
        }

        val packageDto = packageMapper.mapDto(packageEntity)

        Assertions.assertTrue(packageDto.id == packageEntity.id && packageDto.name == packageEntity.name)
    }

    @Test
    fun whenPackageDTOMapped_CheckProducts_AreEqual() {
        val packageEntity = PackageEntity().apply {
            id = 1
            name = "Package 1"
            description = "Cool Test Package"
            imageUrl = "./img/1/img_1_6_2_18:00:00_21-07-2023.png"
            discount = 10
            amount = 3
            products = setOf(Product().apply {
                this.id = 1
                this.name = "Product 1"
                description = "Cool Test Package"
                imageUrl = "./img/1/img_1_6_2_18:00:00_21-07-2023.png"
                price = 10
                quantity = 3
            }, Product().apply {
                this.id = 2
                this.name = "Product 2"
                description = "Cool Test Package"
                imageUrl = "./img/1/img_1_6_2_18:00:00_21-07-2023.png"
                price = 10
                quantity = 3
            })
        }

        val packageDto = packageMapper.mapDto(packageEntity)

        val productsDTOs = packageDto.products
        Assertions.assertEquals(1, productsDTOs[0].id)
        Assertions.assertEquals("Product 1", productsDTOs[0].name)
        Assertions.assertEquals(2, productsDTOs[1].id)
        Assertions.assertEquals("Product 2", productsDTOs[1].name)
    }

}
