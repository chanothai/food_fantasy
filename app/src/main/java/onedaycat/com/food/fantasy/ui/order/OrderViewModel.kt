package onedaycat.com.food.fantasy.ui.order

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import onedaycat.com.foodfantasyservicelib.entity.Order
import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.input.GetOrderInput
import onedaycat.com.foodfantasyservicelib.service.EcomService

class OrderViewModel(
        private val eComService: EcomService
): ViewModel() {

    private val _orders = MutableLiveData<OrdersModel>()

    val orders: LiveData<OrdersModel>
    get() = _orders

    private val _msgError = MutableLiveData<String>()
    val msgError: LiveData<String>
    get() = _msgError

    private fun <T> asyncTask(function: () -> T): Deferred<T> {
        return async(CommonPool) { function() }
    }

    suspend fun loadOrderHistory(input: GetOrderInput) {
        try {
            var orders: ArrayList<Order>? = null
            asyncTask { orders = eComService.orderService.getOrders(input) }.await()

            orders?.let {
                _orders.postValue(mapOrderModel(orders!!))
            }
        }catch (e: Error) {
            _msgError.postValue(e.message)
        }
    }

    private fun mapOrderModel(orders: ArrayList<Order>): OrdersModel {
        val orderModels = OrdersModel()
        for (order in orders) {
            val orderModel = OrderModel().apply {
                orderId = order.id
                totalPrice = order.totalPrice

                for (product in order.products) {
                    product.let {food ->
                        val orderProduct = OrderProductModel().apply {
                            productName = food.productName
                            totalPriceProduct = food.price!! * food.qty
                        }

                        this.orderProducts.products.add(orderProduct)
                    }
                }
            }

            orderModels.orderModels.add(orderModel)
        }

        return orderModels
    }
}