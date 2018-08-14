package onedaycat.com.foodfantasyservicelib.contract.repository

import onedaycat.com.foodfantasyservicelib.entity.Order
import onedaycat.com.foodfantasyservicelib.error.Errors

interface OrderRepo {
    fun create(order: Order)
    fun update(order: Order)
    fun get(id: String): Order
}

class OrderMemo: OrderRepo {
    override fun create(order: Order) {

    }

    override fun update(order: Order) {

    }

    override fun get(id: String): Order {
        throw Errors.OrderStatusNotPaid
    }

}