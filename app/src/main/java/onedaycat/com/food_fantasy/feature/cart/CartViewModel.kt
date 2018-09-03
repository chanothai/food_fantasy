package onedaycat.com.food_fantasy.feature.cart

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
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
import onedaycat.com.foodfantasyservicelib.input.GetProductStocksInput
import onedaycat.com.foodfantasyservicelib.input.RemoveFromCartInput
import onedaycat.com.foodfantasyservicelib.service.CartService
import onedaycat.com.foodfantasyservicelib.service.StockService
import java.util.*

class CartViewModel(
        private val foodCartLiveStore: FoodCartLiveStore,
        private val stockService: StockService,
        private val cartService: CartService): ViewModel() {
    private var foodCartStore: FoodCartStore? = null

    private var _cart = MutableLiveData<CartModel>()
    private var _foodCart = MutableLiveData<FoodCartStore>()
    private var _cartSize = MutableLiveData<Int>()

    val foodCart: LiveData<FoodCartStore>
    get() = _foodCart

    val cart: LiveData<CartModel>
    get() = _cart

    val cartSize: LiveData<Int>
    get() = _cartSize

    fun mapCart() {
        if (foodCartLiveStore.liveData.value != null) {
            launch(UI) {
                async(CommonPool) {
                    foodCartStore = getNeedFoodCart(foodCartStore = foodCartLiveStore.liveData.value!!)
                }.await()

                _foodCart.value = foodCartStore
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
            val listProductStock = stockService.getProductStock(input)

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
                    cartEntity = cartService.addProductCart(input)
                }.await()

                CartStore.counter = cartEntity?.products!!.size
                _cart.value = cartItem
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
                    cartEntity = cartService.removeFromeCart(input)
                }.await()

                cartItem.status = false

                CartStore.counter = cartEntity?.products!!.size
                _cart.value = cartItem
            }
        }catch (e: Error) {
            _cart.value = CartModel()
        }
    }


}