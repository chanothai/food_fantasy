package onedaycat.com.food_fantasy.feature.payment

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import onedaycat.com.foodfantasyservicelib.entity.Order
import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.input.ChargeInput
import onedaycat.com.foodfantasyservicelib.input.DeleteCartInput
import onedaycat.com.foodfantasyservicelib.service.EcomService
import onedaycat.com.foodfantasyservicelib.service.PaymentService

class PaymentViewModel(
        private val eComService: EcomService
): ViewModel() {

    private val _order = MutableLiveData<Order>()

    val order: LiveData<Order>
    get() = _order

    fun payment(input: ChargeInput) {
        try {
            var order: Order? = null
            launch(UI) {
                async(CommonPool) {
                    order = eComService.paymentService.charge(input)
                    return@async
                }.await()

                _order.value = order
                return@launch
            }

        }catch (e: Error) {
            _order.value = Order()
        }
    }

    fun deleteProductCart(input: DeleteCartInput) {
        try {
            launch(UI) {
                async(CommonPool) {
                    eComService.cartService.deleteProductCart(input)

                    return@async
                }.await()

                return@launch
            }
        }catch (e:Error) {
            throw e
        }
    }
}