package flower.app.flowers.base.service

interface CrudService<Body, Response> {
    fun createProduct(body: Body): Response

    fun find(id: Long): Response

    fun findAll(pageSize: Int, sortBy: String, sortOrder: String): List<Response>
}
