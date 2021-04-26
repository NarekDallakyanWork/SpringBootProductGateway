package flower.app.flowers.product.mapper

import flower.app.flowers.io.MediaInfo
import flower.app.flowers.io.impl.FileService
import flower.app.flowers.product.entity.ProductCategoryEntity
import flower.app.flowers.product.entity.ProductEntity
import flower.app.flowers.product.entity.ProductMediaEntity
import flower.app.flowers.product.entity.ProductPriceEntity
import flower.app.flowers.product.response.ProductCategoryResponse
import flower.app.flowers.product.response.ProductMediaResponse
import flower.app.flowers.product.response.ProductPriceResponse
import flower.app.flowers.product.response.ProductResponse
import org.springframework.stereotype.Component

@Component
class ProductMapper {

    fun toProductResponse(
            productEntity: ProductEntity,
            productCategoryEntity: ProductCategoryEntity?,
            productMediaEntityList: List<ProductMediaEntity>,
            productPriceEntityList: List<ProductPriceEntity>,
            fileService: FileService
    ): ProductResponse {

        val mediaList: ArrayList<ProductMediaResponse> = ArrayList()

        productMediaEntityList.forEach {
            val mediaInfo = ProductMediaResponse(
                    it.id, MediaInfo(it.media, it.mediaMimeType, fileService.load(it.media)?.toFile()?.length() ?: 0L)
            )
            mediaList.add(mediaInfo)
        }
        val categoryResponse = productCategoryEntity?.let {
            ProductCategoryResponse(
                    it.id, it.category
            )
        } ?: run {
            null
        }

        val priceList: ArrayList<ProductPriceResponse> = ArrayList()
        productPriceEntityList.forEach {
            val item = ProductPriceResponse(
                    it.id, it.currency, it.price
            )
            priceList.add(item)
        }

        return ProductResponse(
                productEntity.id,
                productEntity.name,
                categoryResponse,
                productEntity.thumbnailImage,
                mediaList,
                priceList
        )

    }
}
