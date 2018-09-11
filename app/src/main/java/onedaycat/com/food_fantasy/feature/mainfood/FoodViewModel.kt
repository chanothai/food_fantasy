package onedaycat.com.food_fantasy.mainfood

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import onedaycat.com.food_fantasy.feature.cart.CartModel
import onedaycat.com.food_fantasy.store.CartStore
import onedaycat.com.food_fantasy.store.FoodCartLiveStore
import onedaycat.com.foodfantasyservicelib.contract.repository.ProductPaging
import onedaycat.com.foodfantasyservicelib.entity.Cart
import onedaycat.com.foodfantasyservicelib.entity.Order
import onedaycat.com.foodfantasyservicelib.input.AddToCartInput
import onedaycat.com.foodfantasyservicelib.input.ChargeInput
import onedaycat.com.foodfantasyservicelib.input.GetProductsInput
import onedaycat.com.foodfantasyservicelib.input.RemoveFromCartInput
import onedaycat.com.foodfantasyservicelib.service.EcomService

class FoodViewModel(
        private val foodCartLiveStore: FoodCartLiveStore,
        private val eComService: EcomService
) : ViewModel() {
    private var foodList = arrayListOf<FoodModel>()


    private val _foodData = MutableLiveData<FoodListModel>()
    private val _msgError = MutableLiveData<String>()

    val cartStore: LiveData<CartStore> = Transformations.map(foodCartLiveStore.liveData) { cartStore ->
        cartStore
    }

    val foodData: LiveData<FoodListModel>
        get() = _foodData

    val msgError: LiveData<String>
        get() = _msgError

    fun loadProducts(input: GetProductsInput) {
        try {
            var productPaging: ProductPaging? = null
            launch(UI) {
                async(CommonPool) {
                    productPaging = eComService.productService.getProducts(input)!!
                    return@async
                }.await()

                productPaging?.let { foodPaging ->
                    foodList.let { foods ->
                        when {
                            foods.size == 0 -> _foodData.value = addFoodModel(foodPaging)
                            else ->  _foodData.value = _foodData.value.let { it }
                        }
                    }
                }
            }
        } catch (e: Error) {
            _foodData.value = FoodListModel(arrayListOf())
        }
    }

    private fun addFoodModel(productPaging: ProductPaging): FoodListModel {
        for (product in productPaging.products) {
            val foodModel = FoodModel(
                    foodId = product.id!!,
                    foodName = product.name!!,
                    foodDesc = product.desc!!,
                    foodPrice = product.price!!,
                    foodIMG = product.image!!
            )

            foodList.add(foodModel)
        }

        return FoodListModel(foodList)
    }

    fun addProductToCart(input: AddToCartInput, foodModel: FoodModel) {
        try {
            var cart: Cart? = null
            launch(UI) {
                async(CommonPool) {
                    cart = eComService.cartService.addProductCart(input)
                    return@async
                }.await()

                cart?.let { cart ->
                    foodCartLiveStore.liveData.value = cartStore.value?.let { cartStore ->
                        cartStore.counter = cart.products.size

                        val indexCart = cart.products.indexOfFirst {
                            it?.productId == foodModel.foodId
                        }

                        if (indexCart != -1) {
                            cartStore.foodCart?.cartList?.add(mapFoodToCart(foodModel, input.qty))
                        }

                        cartStore
                    }
                }

                _foodData.value?.let { foodData ->
                    for ((i, food) in foodData.foodList.withIndex()) {

                        if (food.foodId == foodModel.foodId) {
                            foodData.foodList[i] = foodModel
                            break
                        }
                    }

                    foodData

                }?.also {
                    _foodData.value = it
                }
            }
        } catch (e: Error) {
            _msgError.value = e.toString()
        }
    }

    private fun mapFoodToCart(foodModel: FoodModel, qty: Int): CartModel {
        return CartModel().apply {
            cartPId = foodModel.foodId
            cartName = foodModel.foodName
            cartQTY = qty
            cartPrice = foodModel.foodPrice
            cartImg = foodModel.foodIMG
            cartTotalPrice = foodModel.foodPrice
            isCart = foodModel.isAddToCart
        }
    }

    fun removeProductToCart(input: RemoveFromCartInput, foodModel: FoodModel) {
        try {
            var cart: Cart? = null
            launch(UI) {
                async(CommonPool) {
                    cart = eComService.cartService.removeFromeCart(input)
                    return@async
                }.await()

                cart?.let { cart ->
                    foodCartLiveStore.liveData.value = cartStore.value?.let { cartStore ->
                         cartStore.counter = cart.products.size

                        val indexCart = cart.products.indexOfFirst {
                            it?.productId == foodModel.foodId
                        }

                        if (indexCart < 0) {
                            cartStore.foodCart?.cartList?.remove(mapFoodToCart(foodModel, input.qty))
                        }

                        cartStore
                    }
                }

                _foodData.value?.let { foodCartModel ->
                    val indexOf = foodCartModel.foodList.indexOfFirst {
                        it.foodId == foodModel.foodId
                    }

                    if (indexOf != -1) {
                        foodCartModel.foodList[indexOf].isAddToCart = false
                    }

                    foodCartModel
                }?.also {
                    _foodData.value = it
                }
            }
        } catch (e: Error) {
            _msgError.value = e.toString()
        }
    }

    fun deleteCart() {
        cartStore.value?.foodCart?.cartList = arrayListOf()
        cartStore.value?.counter = 0

        foodCartLiveStore.liveData.value = cartStore.value
    }

    private val _foodSumModel = MutableLiveData<FoodSumModel>()
    val foodSumModel: LiveData<FoodSumModel>
    get() = _foodSumModel

    fun initTotalPrice(qty: Int, price: Int) {
        _foodSumModel.value = FoodSumModel().apply {
            this.qty = qty
            this.price = price
            this.totalPrice = price * qty
        }
    }

    fun foodDetailSumTotalPrice(isAddItem: Boolean) {
        val sum = _foodSumModel.value?.let {
            if (isAddItem) {
                it.qty += 1
            }else{
                it.qty = minusItem(it.qty)
            }

            it.totalPrice = it.price * it.qty

            it
        }

        _foodSumModel.value = sum
    }

    private fun minusItem(qty: Int):Int {
        val result = qty - 1
        if (result == 0) {
            return 1
        }

        return result
    }


    private var _totalPrice = MutableLiveData<Int>()
    val totalPrice: LiveData<Int>
        get() = _totalPrice

    fun cartSumTotalPrice() {
        foodCartLiveStore.liveData.value = cartStore.value?.let {cartStore->
            cartStore.foodCart?.cartList?.let {carts->
                var totalPrice = 0
                for (cart in carts) {
                    cart.cartTotalPrice = cart.cartPrice * cart.cartQTY

                    totalPrice += cart.cartTotalPrice
                }

                _totalPrice.value = totalPrice

                carts
            }

            cartStore
        }
    }

    fun updateCartItem(cart: CartModel) {
        foodCartLiveStore.liveData.value = cartStore.value?.let {cartStore->
            cartStore.foodCart?.cartList?.let {carts->
                val index = carts.indexOf(cart)

                if (cart.isCart) {
                    carts[index].cartTotalPrice = cart.cartTotalPrice + cart.cartPrice

                    totalPrice.value?.let { it->
                        var totalPrice = it
                        totalPrice += cart.cartPrice
                        totalPrice
                    }?.also {
                        _totalPrice.value = it
                    }

                }else{
                    carts[index].cartTotalPrice = cart.cartTotalPrice - cart.cartPrice

                    totalPrice.value?.let { it->
                        var totalPrice = it
                        totalPrice -= cart.cartPrice
                        totalPrice
                    }?.also {
                        _totalPrice.value = it
                    }
                }

                carts
            }

            cartStore
        }
    }

    private val _pay = MutableLiveData<Order>()
    val pay: LiveData<Order>
    get() = _pay

    fun payment(input: ChargeInput) {
        try {
            var order: Order? = null
            launch(UI) {
                async(CommonPool) {
                    order = eComService.paymentService.charge(input)
                    return@async
                }.await()

                _pay.value = order
                return@launch
            }
        }catch (e: onedaycat.com.foodfantasyservicelib.error.Error) {
            _pay.value = Order()
        }
    }

    fun deleteOrder() {
        _pay.value = null
    }
}