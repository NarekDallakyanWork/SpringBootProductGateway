package flower.app.flowers.product.exception

class ProductNotFountException(override val message: String): Exception(message)

class ProductCategoryNotFoundException(override val message: String): Exception(message)

class ProductMediaNotFountException(override val message: String): Exception(message)

class ProductUniqueException(override val message: String): Exception(message)


class ProductValidationException(override val message: String): Exception(message)
