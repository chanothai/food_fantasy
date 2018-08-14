package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.entity.ProductStock
import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.contract.repository.StockRepo
import onedaycat.com.foodfantasyservicelib.validate.StockValidate

class StockService(val stockRepo: StockRepo, val stockValidate: StockValidate) {

    fun addProductStock(input: AddProductStockInput): Pair<ProductStock?, Error?> {
        val error = stockValidate.inputPStock(input)
        if (error != null) {
            return Pair(null, error)
        }

        var (productStock, err) = stockRepo.get(input.productID)
        if (err != null && !Errors.isNotFound(err)) {
            return Pair(null, err)
        }

        if (Errors.isNotFound(err)) {
            productStock = ProductStock().createProductStock(input.productID, 0)
        }

        productStock!!.deposit(input.qty)

        //create or update product stock into stock repo
        err = stockRepo.upsert(productStock)
        if (err != null) {
            return Pair(null, err)
        }

        return Pair(productStock, null)
    }

    fun subProductStock(input: SubProductStockInput): Pair<ProductStock?, Error?> {
        val error = stockValidate.inputSubStock(input)
        if (error != null) {
            return Pair(null, error)
        }

        //get product stock
        var (productStock, err) = stockRepo.get(input.productID)

        if (err != null) {
            return Pair(null, err)
        }

        //Withdraw qty from product stock
        val (pStock, e) = productStock!!.withDraw(input.qty)

        if (e != null) {
            return Pair(null, e)
        }

        //save or update
        err = stockRepo.upsert(pStock)
        if (err != null) {
            return Pair(null, err)
        }

        return Pair(pStock, null)
    }
}