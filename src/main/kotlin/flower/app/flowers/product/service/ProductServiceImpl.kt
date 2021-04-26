package flower.app.flowers.product.service

import flower.app.flowers.io.MediaInfo
import flower.app.flowers.io.impl.FileService
import flower.app.flowers.product.body.*
import flower.app.flowers.product.entity.*
import flower.app.flowers.product.exception.ProductNotFountException
import flower.app.flowers.product.exception.ProductUniqueException
import flower.app.flowers.product.exception.ProductValidationException
import flower.app.flowers.product.ext.validateCategory
import flower.app.flowers.product.ext.validateMedia
import flower.app.flowers.product.ext.validateProduct
import flower.app.flowers.product.mapper.ProductMapper
import flower.app.flowers.product.response.*
import org.apache.commons.io.FilenameUtils
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val productPriceRepository: ProductPriceRepository,
    private val productMediaRepository: ProductMediaRepository,
    private val productCategoryRepository: ProductCategoryRepository,
    private val productSectionRepository: ProductSectionRepository,
    private val fileService: FileService,
    private val productMapper: ProductMapper
) : ProductService {

    override fun addPriceToProduct(priceBody: ProductPriceBody): ProductPriceResponse {

        val productEntity = productRepository.validateProduct(priceBody.productId)
        val categoryEntity = productCategoryRepository.validateCategory(productEntity.categoryId)

        val productPriceEntity = productPriceRepository.save(
            ProductPriceEntity(
                priceBody.currency,
                priceBody.price,
                productEntity
            )
        )

        return ProductPriceResponse(
            productPriceEntity.id,
            productPriceEntity.currency, productPriceEntity.price
        )
    }

    override fun addMediaToProduct(mediaBody: ProductMediaBody): ProductMediaResponse {

        val productEntity = productRepository.validateProduct(mediaBody.productId)
        val categoryEntity = productCategoryRepository.validateCategory(productEntity.categoryId)

        val file = fileService.load(mediaBody.fileId)?.toFile() ?: throw NullPointerException()
        val mediaInfo = MediaInfo(file.name, FilenameUtils.getExtension(file.name), file.length())

        val productMediaEntity = productMediaRepository.save(
            ProductMediaEntity(mediaInfo.id, mediaInfo.mimeType, productEntity)
        )

        return ProductMediaResponse(
            productMediaEntity.id,
            mediaInfo
        )
    }

    override fun createCategory(categoryBody: ProductCategoryBody): ProductCategoryResponse {

        val categories = productCategoryRepository.findAllByCategory(categoryBody.category)
        if (categories.isNotEmpty()) {
            throw NullPointerException("Category is exists")
        }

        val categoryEntity = productCategoryRepository.save(ProductCategoryEntity(categoryBody.category))
        return ProductCategoryResponse(categoryEntity.id, categoryEntity.category)
    }

    override fun createSection(body: ProductSectionBody): ProductSectionResponse {

        val sections = productSectionRepository.findAllByName(body.sectionName)

        if (body.sectionName.isEmpty() || body.sectionName.isBlank()) {
            throw ProductValidationException("section name is required")
        }

        if (sections.isNotEmpty()) {
            throw ProductUniqueException("Section is exists")
        }

        val sectionEntity = productSectionRepository.save(ProductSectionEntity(body.sectionName))

        return ProductSectionResponse(sectionEntity.id, sectionEntity.name)
    }

    override fun createProduct(body: ProductBody): ProductResponse {

        // Validate Category
        val categoryEntity: ProductCategoryEntity = productCategoryRepository.validateCategory(body.categoryId)

        fileService.load(body.thumbnailImage)?.validateMedia()

        val productEntity = ProductEntity(
            body.name, categoryEntity.id, body.thumbnailImage,
        )

        val newProductEntity = productRepository.save(productEntity)

        return ProductResponse(
            newProductEntity.id,
            newProductEntity.name,
            ProductCategoryResponse(categoryEntity.id, categoryEntity.category),
            newProductEntity.thumbnailImage,
            emptyList(), emptyList()
        )
    }

    override fun find(id: Long): ProductResponse {

        val productEntity = productRepository.findByIdOrNull(id)?.let { productEntity ->
            return@let productEntity
        } ?: run {
            throw ProductNotFountException("Product not found")
        }
        val categoryEntity: ProductCategoryEntity? = productCategoryRepository.findByIdOrNull(productEntity.categoryId)
        val productMediaList: List<ProductMediaEntity> = productMediaRepository.findAllByProductEntity(productEntity)
        val productPriceList: List<ProductPriceEntity> = productPriceRepository.findAllByProductEntity(productEntity)

        return productMapper.toProductResponse(
            productEntity,
            categoryEntity,
            productMediaList,
            productPriceList,
            fileService
        )
    }

    override fun findAll(pageSize: Int, sortBy: String, sortOrder: String): List<ProductResponse> {
        TODO("Not yet implemented")
    }
}
