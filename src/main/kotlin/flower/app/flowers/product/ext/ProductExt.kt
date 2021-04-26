package flower.app.flowers.product.ext

import flower.app.flowers.product.entity.ProductCategoryEntity
import flower.app.flowers.product.entity.ProductEntity
import flower.app.flowers.product.exception.ProductCategoryNotFoundException
import flower.app.flowers.product.exception.ProductMediaNotFountException
import flower.app.flowers.product.exception.ProductNotFountException
import flower.app.flowers.product.service.ProductCategoryRepository
import flower.app.flowers.product.service.ProductRepository
import flower.app.flowers.product.service.ProductService
import org.springframework.data.repository.findByIdOrNull
import java.nio.file.Path


fun ProductService.retrieveProduct(productId: Long, message: String = "Product id is required") {



}


fun ProductRepository.validateProduct(productId: Long, message: String = "Product id is required"): ProductEntity {
    return findByIdOrNull(productId)?.let { productEntity ->
        return@let productEntity
    } ?: run {
        throw ProductNotFountException(message)
    }
}

fun ProductCategoryRepository.validateCategory(
    categoryId: Long,
    message: String = "Category is required"
): ProductCategoryEntity {
    return findByIdOrNull(categoryId)?.let { categoryEntity ->
        return@let categoryEntity
    } ?: run {
        throw ProductCategoryNotFoundException(message)
    }
}

fun Path.validateMedia(message: String = "Media not found"): Path {
    if (!this.toFile().exists()) {
        throw ProductMediaNotFountException(message)
    }
    return this
}

