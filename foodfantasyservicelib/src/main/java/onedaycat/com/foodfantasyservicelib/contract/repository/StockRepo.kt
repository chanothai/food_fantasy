package onedaycat.com.foodfantasyservicelib.contract.repository

import onedaycat.com.foodfantasyservicelib.entity.ProductStock
import onedaycat.com.foodfantasyservicelib.entity.ProductStockWithPrice
import onedaycat.com.foodfantasyservicelib.error.Error

interface StockRepo {
    fun upsert(product: ProductStock?)
    fun get(productId: String): ProductStock?
    fun getByIDs(productIDs: MutableList<String>): MutableList<ProductStock?>
    fun getWithPrice(productId: String): ProductStockWithPrice?
}