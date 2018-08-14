package onedaycat.com.foodfantasyservicelib.contract.repository

import onedaycat.com.foodfantasyservicelib.entity.Order
import onedaycat.com.foodfantasyservicelib.entity.ProductStock
import onedaycat.com.foodfantasyservicelib.entity.Transaction

interface PaymentRepo {
    fun savePayment(order: Order, transaction: Transaction, productStocks: MutableList<ProductStock?>)
}
