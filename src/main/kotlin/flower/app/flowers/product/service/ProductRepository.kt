package flower.app.flowers.product.service

import flower.app.flowers.product.entity.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.stereotype.Repository

@NoRepositoryBean
interface BaseRepository<Entity, Id> : JpaRepository<Entity, Id>

@Repository
interface ProductSectionRepository : BaseRepository<ProductSectionEntity, Long> {

    fun findAllByName(name: String): List<ProductSectionEntity>
}

@Repository
interface ProductRepository : BaseRepository<ProductEntity, Long>

@Repository
interface ProductPriceRepository : BaseRepository<ProductPriceEntity, Long> {

    fun findAllByProductEntity(productEntity: ProductEntity): List<ProductPriceEntity>
}

@Repository
interface ProductMediaRepository : BaseRepository<ProductMediaEntity, Long> {

    fun findAllByProductEntity(productEntity: ProductEntity): List<ProductMediaEntity>
}

@Repository
interface ProductCategoryRepository : BaseRepository<ProductCategoryEntity, Long> {

    fun findAllByCategory(category: String): List<ProductCategoryEntity>
}
