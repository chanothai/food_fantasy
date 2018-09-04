package onedaycat.com.food_fantasy.feature.order

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import onedaycat.com.foodfantasyservicelib.entity.Order
import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.input.GetOrderInput
import onedaycat.com.foodfantasyservicelib.service.EcomService
import onedaycat.com.foodfantasyservicelib.service.OrderService

class OrderViewModel(
        private val eComService: EcomService
): ViewModel() {

    private val _orders = MutableLiveData<ArrayList<Order>>()

    val orders: LiveData<ArrayList<Order>>
    get() = _orders

    fun loadOrderHistory(input: GetOrderInput) {
        try {
            var orders: ArrayList<Order>? = null
            launch(UI) {
                async(CommonPool) {
                    orders = eComService.orderService.getOrders(input)
                    return@async
                }.await()

                _orders.value = orders
                return@launch
            }
        }catch (e: Error) {
            _orders.value = arrayListOf()
        }
    }
}