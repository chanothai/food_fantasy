package onedaycat.com.foodfantasyservicelib.validate

import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.service.AddProductStockInput
import onedaycat.com.foodfantasyservicelib.service.SubProductStockInput

interface StockValidate {
    fun inputPStock(input: AddProductStockInput): Error?
    fun inputSubStock(input: SubProductStockInput): Error?
}

class StockMemoValidate: StockValidate {
    override fun inputPStock(input: AddProductStockInput): Error? {
        if ((input.productID.isNotEmpty() && input.qty >= 0)
                || (input.productID.isNotBlank() && input.qty >= 0)) {
            return null
        }

        return Errors.InvalidInputProductStock
    }

    override fun inputSubStock(input: SubProductStockInput): Error? {
        if ((input.productID.isNotEmpty() && input.qty >= 0)
                || (input.productID.isNotBlank() && input.qty >= 0)) {
            return null
        }

        return Errors.InvalidInputProductStock
    }
}