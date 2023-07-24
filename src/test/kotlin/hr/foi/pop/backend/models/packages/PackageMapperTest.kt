package hr.foi.pop.backend.models.packages

import hr.foi.pop.backend.utils.MockEntitiesHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PackageMapperTest {

    val packageMapper = PackageMapper()

    @Test
    fun whenPackageDTOMapped_CheckPackageAttributes_AreEqual() {
        val packageEntity = MockEntitiesHelper.generatePackageEntityWithoutProducts()

        val packageDto = packageMapper.mapDto(packageEntity)

        Assertions.assertTrue(packageDto.id == packageEntity.id && packageDto.name == packageEntity.name)
    }

    @Test
    fun whenPackageDTOMapped_CheckProducts_AreEqual() {
        val packageEntity = MockEntitiesHelper.generatePackageEntityWithTwoProducts()

        val packageDto = packageMapper.mapDto(packageEntity)

        val productsDTOs = packageDto.products
        Assertions.assertEquals(1, productsDTOs[0].id)
        Assertions.assertEquals("Product 1", productsDTOs[0].name)
        Assertions.assertEquals(2, productsDTOs[1].id)
        Assertions.assertEquals("Product 2", productsDTOs[1].name)
    }

}
