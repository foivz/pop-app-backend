package hr.foi.pop.backend.models.products

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ProductMapperTest {

    val productMapper = ProductMapper()

    @Test
    fun whenProductDTOMapped_CheckProductAttributes_AreEqual() {
        val product = Product().apply {
            id = 1
            name = "Product 1"
            description = "Cool Test Product"
            imageUrl = "./img/1/img_1_6_2_18:00:00_21-07-2023.png"
            price = 10
            quantity = 3
        }

        val productDto = productMapper.mapDto(product)

        Assertions.assertTrue(productDto.id == product.id && productDto.name == product.name)
    }

    @Test
    fun whenProductDTOMapped_CheckForNullImage_IsDefaultImagePath() {
        val product = Product().apply {
            id = 1
            name = "Product 2"
            description = "Cool Test Product 2"
            imageUrl = null
            price = 10
            quantity = 3
        }

        product.imageUrl = null
        val productDto = productMapper.mapDto(product)

        Assertions.assertTrue(productDto.imageUrl == "./img/default_product_image.png")
    }
}
