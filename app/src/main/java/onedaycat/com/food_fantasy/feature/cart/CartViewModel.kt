package onedaycat.com.food_fantasy.feature.cart

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.widget.Toast
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import onedaycat.com.food_fantasy.store.CartStore
import onedaycat.com.food_fantasy.store.FoodCartLiveStore
import onedaycat.com.food_fantasy.store.FoodCartStore
import onedaycat.com.foodfantasyservicelib.entity.Cart
import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.input.AddToCartInput
import onedaycat.com.foodfantasyservicelib.input.DeleteCartInput
import onedaycat.com.foodfantasyservicelib.input.GetProductStocksInput
import onedaycat.com.foodfantasyservicelib.input.RemoveFromCartInput
import onedaycat.com.foodfantasyservicelib.service.CartService
import onedaycat.com.foodfantasyservicelib.service.EcomService
import onedaycat.com.foodfantasyservicelib.service.StockService
import java.util.*

class CartViewModel(
        private val foodCartLiveStore: FoodCartLiveStore,
        private val eComService: EcomService): ViewModel() {

    private var foodCartStore: FoodCartStore? = null

    private var _cart = MutableLiveData<CartModel>()
    private var _foodCart = MutableLiveData<FoodCartStore>()

    val foodCart: LiveData<FoodCartStore>
    get() = _foodCart

    val cart: LiveData<CartModel>
    get() = _cart

    fun mapPStockToCart() {
        if (foodCartLiveStore.liveData.value != null) {
            launch(UI) {
                async(CommonPool) {
                    with(foodCartLiveStore.liveData.value?.foodCart) {
                        foodCartStore = getNeedFoodCart(this!!)
                    }
                    return@async
                }.await()

                _foodCart.value = foodCartStore

                return@launch
            }
        }
    }

    private fun getNeedFoodCart(foodCartStore: FoodCartStore): FoodCartStore? {
        val ids: ArrayList<String> = arrayListOf()

        for (cart in foodCartStore.cartList!!) {
            ids.add(cart.cartPId!!)
        }

        return loadProductStock(GetProductStocksInput(ids), foodCartStore)
    }

    private fun loadProductStock(input: GetProductStocksInput, foodCartStore: FoodCartStore): FoodCartStore? {
        val newFoodCart: FoodCartStore?
        try {
            val listProductStock = eComService.stockService.getProductStock(input)

            if (foodCartStore.cartList!!.size > 0) {
                for ((i, pStock) in listProductStock.withIndex()) {
                    foodCartStore.cartList!![i].cartQTYLimit = pStock?.qty!!
                }
            }

            return foodCartStore
        }catch (e: Error) {
            newFoodCart = FoodCartStore()
        }

        return newFoodCart
    }


    fun addProductToCart(input: AddToCartInput, cartItem: CartModel) {
        try {
            var cartEntity: Cart? = null
            launch(UI) {
                async(CommonPool) {
                    cartEntity = eComService.cartService.addProductCart(input)
                    return@async
                }.await()

                //must update code
                CartStore.counter = cartEntity?.products!!.size
                _cart.value = cartItem

                return@launch
            }
        }catch (e: Error) {
            _cart.value = CartModel()
        }
    }

    fun removeProductToCart(input: RemoveFromCartInput, cartItem: CartModel) {
        try {
            var cartEntity: Cart? = null
            launch(UI) {
                async(CommonPool) {
                    cartEntity = eComService.cartService.removeFromeCart(input)
                    return@async
                }.await()

                CartStore.counter = cartEntity?.products!!.size

                val carts = _foodCart.value?.cartList
                carts?.remove(cartItem)

                _foodCart.value = FoodCartStore(input.userID, carts)
            }
        }catch (e: Error) {
            throw e
        }
    }

    fun clearProductCart() {
        _foodCart.value?.cartList = arrayListOf()
        CartStore.counter = 0
    }
}