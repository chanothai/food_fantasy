package onedaycat.com.foodfantasyservicelib.validate

import onedaycat.com.foodfantasyservicelib.entity.Product
import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.service.CreateProductInput
import onedaycat.com.foodfantasyservicelib.service.GetProductInput
import onedaycat.com.foodfantasyservicelib.service.GetProductsInput

interface ProductValidate {
    fun inputId(id: String): Error?
    fun inputProduct(input: CreateProductInput?): Error?
    fun inputLimitPaging(input: GetProductsInput): Error?
}

class ProductMemoValidate: ProductValidate {

    override fun inputId(id: String): Error? {
        if (id.isNotBlank() || id.isNotEmpty()) {
            return null
        }

        return Errors.InvalidInput
    }

    override fun inputProduct(input: CreateProductInput?): Error? {
        if (input != null) {
            if (input.price >= 0
                    && (input.name.isNotEmpty() || input.name.isNotBlank()))
            {
                return null
            }

            return Errors.InvalidInputProduct
        }

        return Errors.InvalidInput
    }

    override fun inputLimitPaging(input: GetProductsInput): Error? {
        if (input.limit >= 1) {
            return null
        }

        return Errors.InvalidInputLimitPaging
    }
}