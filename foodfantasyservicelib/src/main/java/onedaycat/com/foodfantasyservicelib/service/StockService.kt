package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.entity.ProductStock
import onedaycat.com.foodfantasyservicelib.contract.repository.StockRepo
import onedaycat.com.foodfantasyservicelib.error.NotFoundException
import onedaycat.com.foodfantasyservicelib.input.AddProductStockInput
import onedaycat.com.foodfantasyservicelib.input.SubProductStockInput
import onedaycat.com.foodfantasyservicelib.validate.StockValidate

class StockService(
        private val stockRepo: StockRepo,
        private val stockValidate: StockValidate) {

    private var productStock:ProductStock? = null

    fun addProductStock(input: AddProductStockInput): ProductStock? {
        try {
            stockValidate.inputPStock(input)

            productStock = stockRepo.get(input.productID)
            productStock!!.deposit(input.qty)

        }catch (e: NotFoundException) {
            productStock = ProductStock().createProductStock(input.productID, 0)
        }

        stockRepo.upsert(productStock)

        return productStock
    }

    fun subProductStock(input: SubProductStockInput): ProductStock? {
        stockValidate.inputSubStock(input)

        productStock= stockRepo.get(input.productID)

        val pStock = productStock!!.withDraw(input.qty)

        stockRepo.upsert(pStock)

        return pStock
    }
}