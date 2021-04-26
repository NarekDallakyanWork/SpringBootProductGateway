package flower.app.flowers.product.response

import flower.app.flowers.product.entity.Currency

data class ProductPriceResponse(
    val id: Long,
    val currency: Currency,
    val price: Double
)
