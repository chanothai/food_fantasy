package onedaycat.com.foodfantasyservicelib.contract.repository

import onedaycat.com.foodfantasyservicelib.entity.ProductStock
import onedaycat.com.foodfantasyservicelib.entity.ProductStockWithPrice
import onedaycat.com.foodfantasyservicelib.error.Error

interface StockRepo {
    fun upsert(product: ProductStock?): Error?
    fun get(productId: String): Pair<ProductStock?, Error?>
    fun getByIDs(productIDs: MutableList<String>): MutableList<ProductStock?>
    fun getWithPrice(productId: String): ProductStockWithPrice?
}