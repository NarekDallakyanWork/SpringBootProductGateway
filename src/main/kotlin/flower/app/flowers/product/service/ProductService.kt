package flower.app.flowers.product.service

import flower.app.flowers.base.service.CrudService
import flower.app.flowers.product.body.*
import flower.app.flowers.product.response.*

interface ProductService : CrudService<ProductBody, ProductResponse> {
    fun addPriceToProduct(priceBody: ProductPriceBody): ProductPriceResponse
    fun addMediaToProduct(mediaBody: ProductMediaBody): ProductMediaResponse
    fun createCategory(categoryBody: ProductCategoryBody): ProductCategoryResponse
    fun createSection(body: ProductSectionBody): ProductSectionResponse
}
