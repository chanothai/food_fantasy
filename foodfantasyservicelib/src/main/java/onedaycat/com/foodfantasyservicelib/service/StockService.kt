package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.entity.ProductStock
import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.contract.repository.StockRepo
import onedaycat.com.foodfantasyservicelib.error.NotFoundException
import onedaycat.com.foodfantasyservicelib.validate.StockValidate

class StockService(val stockRepo: StockRepo, val stockValidate: StockValidate) {

    private var productStock:ProductStock? = null

    fun addProductStock(input: AddProductStockInput): ProductStock? {
        try {
            stockValidate.inputPStock(input)

            productStock = stockRepo.get(input.productID)

            if (productStock == null) {
                productStock = ProductStock().createProductStock(input.productID, 0)
            }

            productStock!!.deposit(input.qty)

            //create or update product stock into stock repo
            stockRepo.upsert(productStock)

            return productStock
        }catch (e: Error) {
            e.printStackTrace()
        }

        return null
    }

    fun subProductStock(input: SubProductStockInput): ProductStock? {
        try {
            stockValidate.inputSubStock(input)

            productStock= stockRepo.get(input.productID)

            if (productStock == null) {
                throw Errors.ProductStockNotFound
            }

            //Withdraw qty from product stock
            val pStock = productStock!!.withDraw(input.qty)

            stockRepo.upsert(pStock)

            return pStock
        }catch (e: Error) {
            e.printStackTrace()
        }

        return null
    }
}