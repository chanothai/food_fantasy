package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.model.Product
import onedaycat.com.foodfantasyservicelib.model.ProductList
import onedaycat.com.foodfantasyservicelib.repository.ProductRepo
import onedaycat.com.foodfantasyservicelib.validate.ProductValidate

class ProductService(val productRepo: ProductRepo, val check: ProductValidate) {

    fun createProduct(product: Product) {
        if (check.hasProduct(product)) {
            productRepo.create(product)
        }
    }

    fun updateProduct(product: Product) {
        if (check.hasProduct(product)) {
            productRepo.update(product)
        }
    }

    fun deleteProduct(productId: String) {
        if (check.validateId(productId)) {
            productRepo.delete(productId)
        }
    }

    fun getAllProduct(): ProductList? {
        return productRepo.getAll()
    }

    fun getProduct(productId: String): Product? {
        if (check.validateId(productId)) {
            val product = productRepo.get(productId)

            if (check.hasProduct(product)) return product
        }

        return null
    }
}