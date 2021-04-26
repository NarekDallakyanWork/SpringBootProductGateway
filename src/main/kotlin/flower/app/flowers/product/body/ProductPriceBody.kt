package flower.app.flowers.product.body

import flower.app.flowers.product.entity.Currency

class ProductPriceBody(
        val productId: Long,
        val currency: Currency,
        val price: Double
)
