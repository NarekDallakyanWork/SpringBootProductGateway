package flower.app.flowers.product.response

data class ProductResponse(
    val id: Long,
    val name: String,
    val category: ProductCategoryResponse?,
    val thumbnailImage: String,
    val media: List<ProductMediaResponse>,
    val prices: List<ProductPriceResponse>
)
