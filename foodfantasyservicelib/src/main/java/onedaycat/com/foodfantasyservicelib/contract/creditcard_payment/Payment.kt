package onedaycat.com.foodfantasyservicelib.contract.creditcard_payment

import onedaycat.com.foodfantasyservicelib.entity.Order
import onedaycat.com.foodfantasyservicelib.entity.Transaction
import onedaycat.com.foodfantasyservicelib.service.CreditCardType


data class CreditCard(
        var type: CreditCardType,
        var name: String,
        var cardNumber: String,
        var ccv: String,
        var expiredData: String,
        var expireYear: String)

interface CreditCardPayment {
    fun charge(order: Order, creditCard: CreditCard): Transaction?
    fun refund(order: Order): Transaction?
}
