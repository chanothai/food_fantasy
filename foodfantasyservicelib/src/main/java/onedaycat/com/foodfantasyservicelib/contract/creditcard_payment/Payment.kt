package onedaycat.com.foodfantasyservicelib.contract.creditcard_payment

import com.google.firebase.firestore.FirebaseFirestore
import onedaycat.com.foodfantasyservicelib.entity.Order
import onedaycat.com.foodfantasyservicelib.entity.Transaction
import onedaycat.com.foodfantasyservicelib.input.CreditCardType


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

class CreditCardPaymentFireStore: CreditCardPayment {
    private val colCCPayment = "CreditCardPayments"
    private val db = FirebaseFirestore.getInstance()

    override fun charge(order: Order, creditCard: CreditCard): Transaction? {
        return null
    }

    override fun refund(order: Order): Transaction? {
        return null
    }

}
