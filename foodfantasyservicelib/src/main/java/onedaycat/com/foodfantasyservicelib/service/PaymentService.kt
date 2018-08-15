package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.contract.creditcart_payment.CreditCardPayment
import onedaycat.com.foodfantasyservicelib.entity.Order
import onedaycat.com.foodfantasyservicelib.entity.OrderStatus
import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.contract.repository.CartRepo
import onedaycat.com.foodfantasyservicelib.contract.repository.OrderRepo
import onedaycat.com.foodfantasyservicelib.contract.repository.PaymentRepo
import onedaycat.com.foodfantasyservicelib.contract.repository.StockRepo
import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.util.clock.Clock
import onedaycat.com.foodfantasyservicelib.util.idgen.IdGen
import onedaycat.com.foodfantasyservicelib.validate.PaymentValidate

class PaymentService(private val orderRepo: OrderRepo,
                     private val ccPayment: CreditCardPayment,
                     private val stockRepo: StockRepo,
                     private val cartRepo: CartRepo,
                     private val paymentRepo: PaymentRepo,
                     private val paymentValidate: PaymentValidate) {

    fun charge(input: ChargeInput): Order? {
        try {
            paymentValidate.inputCharge(input)

            //get cart of user
            val cart = cartRepo.getByUserID(input.userID)

            //get all product stock
            val pstocks = stockRepo.getByIDs(cart!!.productIDs())

            //withdraw product stock into stock
            for (pstock in pstocks) {
                //get product qty every product in cart
                val pQTY = cart.getPQTY(pstock!!.productID)

                if (pQTY != null) {
                    pstock.withDraw(pQTY.qty)
                }
            }

            //create pending order with product from cart
            val order = Order(
                    id = IdGen.NewId(),
                    userId = input.userID,
                    products = cart.toProductQTYList(),
                    totalPrice = cart.totalPrice(),
                    createDate = Clock.NowUTC(),
                    status = OrderStatus.OrderStatusPending
            )

            //charge credit card
            val tx = ccPayment.charge(order, input.creditCard)

            //update order status to paid
            order.paid(tx!!)

            //create order into repository
            paymentRepo.savePayment(order, tx, pstocks)

            return order
        }catch (e: Error) {
            e.printStackTrace()
        }

        return null
    }

    fun refund(input: RefundInput): Order? {
        try {
            paymentValidate.inputRefund(input)

            val order = orderRepo.get(input.orderID)

            if (order.userId != input.userID) {
                throw Errors.NotOrderOwner
            }

            val pstocks = stockRepo.getByIDs(order.productIDs())

            //deposit product stock
            for ( pstock in pstocks) {
                pstock!!.deposit(order.getProduct(pstock.productID)!!.qty)
            }

            //refund no payment
            val tx = ccPayment.refund(order)

            //make order status to be refunded
            order.refund(tx!!)

            //update order
            paymentRepo.savePayment(order, tx, pstocks)

            return order
        }catch (e: Error) {
            e.printStackTrace()
        }

        return null
    }
}