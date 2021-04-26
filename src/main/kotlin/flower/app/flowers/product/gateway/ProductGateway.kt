package flower.app.flowers.product.gateway

import flower.app.flowers.io.impl.FileService
import flower.app.flowers.product.body.*
import flower.app.flowers.product.response.*
import flower.app.flowers.product.service.ProductService
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/product")
class ProductGateway(
    private val productService: ProductService
) {

    @PostMapping("/create-section")
    fun createSection(@RequestBody body: ProductSectionBody): ProductSectionResponse {
        return productService.createSection(body)
    }

    @PostMapping("/create-category")
    fun createCategory(@RequestBody body: ProductCategoryBody): ProductCategoryResponse {
        return productService.createCategory(body)
    }

    @PostMapping("/create-product")
    fun createProduct(@RequestBody body: ProductBody): ProductResponse {
        return productService.createProduct(body)
    }

    @PostMapping("/add-media")
    fun addMediaToProduct(@RequestBody mediaBody: ProductMediaBody): ProductMediaResponse {
        return productService.addMediaToProduct(mediaBody)
    }

    @PutMapping("/add-price")
    fun addPriceToProduct(@RequestBody priceBody: ProductPriceBody): ProductPriceResponse? {
        return productService.addPriceToProduct(priceBody)
    }

    @GetMapping("/single/{productId}")
    @ResponseBody
    fun getSingleProduct(@PathVariable("productId") productId: Long): ProductResponse {
        return productService.find(productId)
    }
}
