package onedaycat.com.foodfantasyservicelib.repository

import onedaycat.com.foodfantasyservicelib.model.Product
import onedaycat.com.foodfantasyservicelib.model.ProductList

interface ProductRepo {
    fun create(product: Product)
    fun update(product: Product)
    fun delete(productId: String)
    fun getAll(): ProductList?
    fun get(productId: String): Product?
}

class ProductMemo: ProductRepo {
    private val product: Product? = null

    override fun create(product: Product) {

    }

    override fun update(product: Product) {

    }

    override fun delete(productId: String) {

    }

    override fun getAll(): ProductList? {
        return ProductList(mutableListOf())
    }

    override fun get(productId: String): Product? {
        return product
    }

}