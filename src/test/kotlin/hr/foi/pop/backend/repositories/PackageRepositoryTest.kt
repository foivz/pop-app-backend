package hr.foi.pop.backend.repositories

import hr.foi.pop.backend.models.packages.PackageEntity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PackageRepositoryTest {

    @Autowired
    lateinit var packageRepository: PackageRepository

    @Test
    fun getPackageById_CheckPackageName_IsTrue() {
        val packageEntity: PackageEntity = packageRepository.getPackageById(3)

        Assertions.assertTrue(packageEntity.name == "Package 3")
    }

    @Test
    fun getPackageById_CheckContainedProducts_AreEqual() {
        val packageEntity: PackageEntity = packageRepository.getPackageById(1)

        val products = packageEntity.products

        Assertions.assertTrue(products.size == 3)
        Assertions.assertNotNull(products.find {
            it.id == 1
        })
        Assertions.assertNotNull(products.find {
            it.id == 2
        })
        Assertions.assertNotNull(products.find {
            it.id == 3
        })
    }


    @Test
    fun getPackagesById_CheckPackageStore_IsCorrect() {
        val packageFromStore1: PackageEntity = packageRepository.getPackageById(1)
        val packageFromStore2: PackageEntity = packageRepository.getPackageById(3)
        val packageFromStore3: PackageEntity = packageRepository.getPackageById(5)

        Assertions.assertTrue(packageFromStore1.store.id == 1)
        Assertions.assertTrue(packageFromStore2.store.id == 2)
        Assertions.assertTrue(packageFromStore3.store.id == 3)
    }
}
