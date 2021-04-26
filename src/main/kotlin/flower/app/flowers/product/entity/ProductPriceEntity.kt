package flower.app.flowers.product.entity

import flower.app.flowers.base.entity.BaseEntity
import javax.persistence.*

@Entity
data class ProductPriceEntity(
        @Column(name = "currency", nullable = false)
        val currency: Currency,
        @Column(name = "price", nullable = false)
        val price: Double,
        // Mappings
        @ManyToOne
        @JoinColumn(name = "product_id", referencedColumnName = "id")
        val productEntity: ProductEntity
) : BaseEntity()
