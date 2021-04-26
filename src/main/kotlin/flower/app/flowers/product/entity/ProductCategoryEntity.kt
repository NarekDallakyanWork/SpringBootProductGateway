package flower.app.flowers.product.entity

import flower.app.flowers.base.entity.BaseEntity
import javax.persistence.Column
import javax.persistence.Entity

@Entity
data class ProductCategoryEntity(
    @Column(name = "category", nullable = false)
    val category: String
) : BaseEntity()
