package flower.app.flowers.product.entity

import flower.app.flowers.base.entity.BaseEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.OneToMany

@Entity
data class ProductEntity(
    @Column(name = "name", nullable = false)
    val name: String,
    @Column(name = "categoryId", nullable = false)
    val categoryId: Long,
    @Column(name = "thumbnailImage", nullable = false)
    val thumbnailImage: String,
    @OneToMany
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    var files: List<ProductMediaEntity> = emptyList()
) : BaseEntity()
