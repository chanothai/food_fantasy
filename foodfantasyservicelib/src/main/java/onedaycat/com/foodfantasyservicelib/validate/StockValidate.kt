package onedaycat.com.foodfantasyservicelib.validate

import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.service.AddProductStockInput
import onedaycat.com.foodfantasyservicelib.service.SubProductStockInput

interface StockValidate {
    fun inputPStock(input: AddProductStockInput)
    fun inputSubStock(input: SubProductStockInput)
}

class StockMemoValidate: StockValidate {
    override fun inputPStock(input: AddProductStockInput) {
        if (input.productID.isEmpty()
                || input.qty < 0
                || input.productID.isBlank()) {

            throw Errors.InvalidInputProductStock
        }
    }

    override fun inputSubStock(input: SubProductStockInput) {
        if (input.productID.isEmpty()
                || input.qty < 0
                || input.productID.isBlank()) {
            throw Errors.InvalidInputProductStock
        }
    }
}