package onedaycat.com.foodfantasyservicelib.contract.repository

import onedaycat.com.foodfantasyservicelib.entity.Product
import onedaycat.com.foodfantasyservicelib.error.Error

data class ProductPaging(
        var products: MutableList<Product>,
        var next: String,
        var prev: String
)

interface ProductRepo {
    fun create(product: Product?): Error?
    fun remove(id: String): Error?
    fun getAllWithPaging(limit: Int): Pair<ProductPaging?, Error?>
    fun get(id: String): Pair<Product?, Error?>
}

class ProductMemo: ProductRepo {
    private val error: Error? = null
    private val product: Product? = null
    private val productPaging: ProductPaging? = null

    override fun create(product: Product?): Error? {
        return error
    }

    override fun remove(id: String): Error? {
        return error
    }

    override fun getAllWithPaging(limit: Int): Pair<ProductPaging?, Error?> {
        return Pair(productPaging, error)
    }

    override fun get(id: String): Pair<Product?, Error?> {
        return Pair(product, error)
    }

}