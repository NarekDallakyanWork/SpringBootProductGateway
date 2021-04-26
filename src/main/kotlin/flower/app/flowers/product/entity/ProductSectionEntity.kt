package flower.app.flowers.product.entity

import flower.app.flowers.base.entity.BaseEntity
import javax.persistence.Column
import javax.persistence.Entity

@Entity
data class ProductSectionEntity(
    @Column(name = "price", nullable = false)
    val name: String
) : BaseEntity()
