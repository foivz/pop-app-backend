package hr.foi.pop.backend.services

import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.exceptions.BadRoleException
import hr.foi.pop.backend.models.products.Product
import hr.foi.pop.backend.repositories.ProductRepository
import hr.foi.pop.backend.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class ProductService {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var productRepository: ProductRepository

    fun getProducts(): List<Product> {
        val principal = SecurityContextHolder.getContext().authentication.principal as UserDetails
        val foundProducts = mutableListOf<Product>()

        if (!principal.authorities.stream().anyMatch { a -> a.authority == "seller" }) {
            throw BadRoleException("Only 'seller' can fetch products!", ApplicationErrorType.ERR_ROLE_NOT_APPLICABLE)
        }

        userRepository.getUserByUsername(principal.username)?.let { user ->

            foundProducts.addAll(productRepository.getProductsByStore(user.store!!))
        }

        return foundProducts
    }
}
