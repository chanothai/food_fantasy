package onedaycat.com.foodfantasyservicelib.entity

import onedaycat.com.foodfantasyservicelib.error.BadRequestException
import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.error.Errors

enum class OrderStatus {
    OrderStatusPaid {
        val orderStatus = "PAID"
    },
    OrderStatusPending {
        val orderStatus = "PENDING"
    },
    OrderStatusRefunded {
        val orderStatus = "REFUNDED"
    }
}

data class Order(
        var id: String? = null,
        var userId: String? = null,
        var products: MutableList<ProductQTY?> = mutableListOf(),
        var totalPrice: Int = 0,
        var createDate: String? = null,
        var status: OrderStatus? = null)
{
    var order: Order = this
    private fun validateChangeStatus(tx: Transaction) {
        if (order.products.size < 1) throw Errors.EmptyProductInOrder
        if (order.totalPrice < 0) throw Errors.InvalidOrderTotalPrice
        if (order.id != tx.orderID) throw Errors.OrderAndTxNotMatched
        if (tx.amount < 1) throw Errors.InvalidTxAmount
    }

    //Paid update order status to paid
    fun paid(tx: Transaction) {
        order.validateChangeStatus(tx)

        if (tx.status != TransactionState.CHANGE) throw Errors.TxStatusNotCharged
        if (order.status != OrderStatus.OrderStatusPending) throw Errors.OrderStatusNotPending

        order.status = OrderStatus.OrderStatusPaid
    }

    //Refund update order status to refund
    fun refund(tx: Transaction) {
        order.validateChangeStatus(tx)

        if (tx.status != TransactionState.REFUNDED) throw Errors.OrderStatusNotPaid
        if (order.status != OrderStatus.OrderStatusPaid) throw Errors.OrderStatusNotPaid

        order.status = OrderStatus.OrderStatusRefunded
    }

    //AddProduct add product into order and set new total price
    fun addProduct(pQTY: ProductQTY, product:Product) {
        if (pQTY.productId != product.id) {
            throw Errors.ProductNotMatched
        }

        order.products.add(pQTY)

        val sum = pQTY.qty.times(product.price!!)
        order.totalPrice = sum
    }

    fun productIDs(): MutableList<String> {
        val arrProductId = mutableListOf<String>()
        if (order.products.size == 0) {
            throw Errors.ProductNotFound
        }

        for (product in order.products) {
            arrProductId.add(product!!.productId!!)
        }

        return arrProductId
    }

    fun getProduct(pstockId: String): ProductQTY? {
        if (products.size == 0) {
            throw Errors.ProductNotFound
        }
        for ((i, pstock) in products.withIndex()) {
            if (pstockId == pstock!!.productId) {
                return order.products[i]
            }
        }

        throw Errors.ProductNotFound
    }
}