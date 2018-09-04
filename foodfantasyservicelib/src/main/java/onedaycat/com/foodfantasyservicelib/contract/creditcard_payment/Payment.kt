package onedaycat.com.foodfantasyservicelib.contract.creditcard_payment

import onedaycat.com.foodfantasyservicelib.entity.Order
import onedaycat.com.foodfantasyservicelib.entity.Transaction
import onedaycat.com.foodfantasyservicelib.entity.TransactionState
import onedaycat.com.foodfantasyservicelib.input.CreditCardType
import onedaycat.com.foodfantasyservicelib.util.clock.Clock
import onedaycat.com.foodfantasyservicelib.util.idgen.IdGen


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

class CreditCardMemoPayment: CreditCardPayment {
    override fun charge(order: Order, creditCard: CreditCard): Transaction? {
        return Transaction(
                IdGen.NewId(),
                order.id!!,
                TransactionState.CHARGE,
                order.totalPrice,
                Clock.NowUTC())
    }

    override fun refund(order: Order): Transaction? {
        return Transaction(
                IdGen.NewId(),
                order.id!!,
                TransactionState.REFUNDED,
                order.totalPrice,
                Clock.NowUTC())
    }

}
