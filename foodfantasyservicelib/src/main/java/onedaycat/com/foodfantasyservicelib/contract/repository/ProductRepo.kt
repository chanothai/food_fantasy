package onedaycat.com.foodfantasyservicelib.contract.repository

import onedaycat.com.foodfantasyservicelib.entity.Product
import onedaycat.com.foodfantasyservicelib.error.Error

data class ProductPaging(
        var products: MutableList<Product>,
        var next: String,
        var prev: String
)

interface ProductRepo {
    fun create(product: Product?)
    fun remove(id: String)
    fun getAllWithPaging(limit: Int): ProductPaging?
    fun get(id: String): Product?
}