package flower.app.flowers.base.error

import flower.app.flowers.product.exception.*
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(ProductMediaNotFountException::class)
    fun handleProductMediaNotFoundException(error: ProductMediaNotFountException): ErrorResponse {
        return ErrorResponse(error.message)
    }

    @ExceptionHandler(ProductCategoryNotFoundException::class)
    fun handleProductCategoryNotFoundException(error: ProductCategoryNotFoundException): ErrorResponse {
        return ErrorResponse(error.message)
    }

    @ExceptionHandler(ProductNotFountException::class)
    fun handleProductNotFoundException(error: ProductNotFountException): ErrorResponse {
        return ErrorResponse(error.message)
    }

    @ExceptionHandler(ProductValidationException::class)
    fun handleProductValidationException(error: ProductValidationException): ErrorResponse {
        return ErrorResponse(error.message)
    }

    @ExceptionHandler(ProductUniqueException::class)
    fun handleProductUniqueException(error: ProductUniqueException): ErrorResponse {
        return ErrorResponse(error.message)
    }
}

class ErrorResponse(
    val errorMessage: String
)
