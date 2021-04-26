package flower.app.flowers.product.entity

import flower.app.flowers.base.entity.BaseEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
data class ProductMediaEntity(
        @Column(name = "media", nullable = false)
        val media: String,
        @Column(name = "media_mime_type", nullable = false)
        val mediaMimeType: String,
        // Mapping
        @ManyToOne
        @JoinColumn(name = "product_id", referencedColumnName = "id")
        val productEntity: ProductEntity
) : BaseEntity()
