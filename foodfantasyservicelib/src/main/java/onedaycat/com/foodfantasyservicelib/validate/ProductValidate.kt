package onedaycat.com.foodfantasyservicelib.validate

import onedaycat.com.foodfantasyservicelib.model.Product

interface ProductValidate {
    fun validateId(id: String): Boolean
    fun hasProduct(product: Product?): Boolean
}

class ProductMemoValidate: ProductValidate {
    override fun validateId(id: String): Boolean {
        if (id.isNotBlank() || id.isNotEmpty()) {
            return true
        }

        return false
    }

    override fun hasProduct(product: Product?): Boolean {
        if (product != null) {
            if (product.price >= 0
                    && (product.name.isNotEmpty() || product.name.isNotBlank()))
            {
                return true
            }
        }

        return false
    }
}