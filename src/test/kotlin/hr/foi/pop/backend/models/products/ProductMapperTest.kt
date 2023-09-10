package hr.foi.pop.backend.models.products

import hr.foi.pop.backend.utils.MockEntitiesHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ProductMapperTest {

    val productMapper = ProductMapper()

    @Test
    fun whenProductDTOMapped_checkProductAttributes_areEqual() {
        val product = MockEntitiesHelper.generateProductEntity()

        val productDto = productMapper.mapDto(product)

        Assertions.assertTrue(productDto.id == product.id && productDto.name == product.name)
    }

    @Test
    fun whenProductDTOMapped_checkForNullImage_isDefaultImagePath() {
        val product = MockEntitiesHelper.generateProductEntityWithNoImage()

        product.imageUrl = null
        val productDto = productMapper.mapDto(product)

        Assertions.assertTrue(productDto.imageUrl == "./img/default_product_image.png")
    }
}
