package onedaycat.com.food_fantasy.mainfood

import android.arch.lifecycle.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import onedaycat.com.food_fantasy.ui.cart.CartModel
import onedaycat.com.food_fantasy.store.CartStore
import onedaycat.com.food_fantasy.store.FoodCartLiveStore
import onedaycat.com.foodfantasyservicelib.contract.creditcard_payment.CreditCard
import onedaycat.com.foodfantasyservicelib.contract.repository.ProductPaging
import onedaycat.com.foodfantasyservicelib.entity.Cart
import onedaycat.com.foodfantasyservicelib.entity.Order
import onedaycat.com.foodfantasyservicelib.input.*
import onedaycat.com.foodfantasyservicelib.service.EcomService

class FoodViewModel(
        private val foodCartLiveStore: FoodCartLiveStore,
        private val eComService: EcomService
) : ViewModel() {
    private var foodList = arrayListOf<FoodModel>()

    var _totalPrice = MutableLiveData<Int>()
    val totalPrice: LiveData<Int>
        get() = _totalPrice

    val cartStore: LiveData<CartStore> = Transformations.map(foodCartLiveStore.liveData) { cartStore ->
        cartStore
    }

    val _foodData = MutableLiveData<FoodListModel>()
    val foodData: LiveData<FoodListModel>
        get() = _foodData

    private val _pay = MutableLiveData<Order>()
    val pay: LiveData<Order>
        get() = _pay

    private val _msgError = MutableLiveData<String>()
    val msgError: LiveData<String>
        get() = _msgError


    private fun <T> asyncTask(function: () -> T): Deferred<T> {
        return async(CommonPool) { function() }
    }

    suspend fun loadProducts(input: GetProductsInput) {
        try {
            var productPaging: ProductPaging? = null

            asyncTask { productPaging = eComService.productService.getProducts(input)!! }.await()

            productPaging?.let { foodPaging ->
                foodList.let { foods ->
                    when {
                        foods.size == 0 -> {
                            val s = addFoodModel(foodPaging)
                            _foodData.postValue(s)
                        }
                    }
                }
            }
        }catch (e: onedaycat.com.foodfantasyservicelib.error.Error) {
            _msgError.postValue(e.message)
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

    private fun addCartToStore(cart:Cart?) {
        cart?.let {
            val cartStore = cartStore.value?.let { cartStore ->
                var qty = 0
                for (product in it.products) {
                    qty += product.qty
                }

                cartStore.counter = qty

                var totalPrice = 0
                val cartsModel = arrayListOf<CartModel>()
                for (item in it.products) {
                    CartModel().apply {
                        cartPId = item.productId
                        cartName = item.productName
                        cartQTY = item.qty
                        cartPrice = item.price!!
                        cartTotalPrice = item.price!! * item.qty

                        totalPrice += cartTotalPrice
                    }.also {cartModel->
                        cartsModel.add(cartModel)
                    }
                }

                _totalPrice.value = totalPrice
                cartStore.foodCart?.cartList = cartsModel
                cartStore
            }

            foodCartLiveStore.liveData.postValue(cartStore)
        }
    }

    suspend fun loadCart(input: GetCartInput) {
        try {
            var cart: Cart? = null
            asyncTask { cart = eComService.cartService.getCartWithUserID(input) }.await()

            cart?.let {
                addCartToStore(it)
            }
        }catch (e:onedaycat.com.foodfantasyservicelib.error.Error) {
            _msgError.value = e.message
        }
    }

    private val _cartLiveData = MutableLiveData<Cart>()
    val cartLiveData: LiveData<Cart>
    get() = _cartLiveData

    fun addAllProductCart(input: AddCartsToCartInput) {
        try {
            var cart:Cart? = null
            launch(UI) {
                async(CommonPool) {
                    cart = eComService.cartService.addProducrCarts(input)
                    return@async
                }.await()

                cart?.let {
                    _cartLiveData.value = cart
                }
            }
        }catch (e:onedaycat.com.foodfantasyservicelib.error.Error) {
            _msgError.value = e.toString()
        }
    }

    fun addProductToCart(input: AddToCartInput, foodModel: FoodModel) {
        try {
            var cart: Cart? = null
            launch(UI) {
                async(CommonPool) {
                    cart = eComService.cartService.addProductCart(input)
                    return@async
                }.await()

                addCartToStore(cart)
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

    fun initTotalPrice(foodModel: FoodModel) {
        var qty = 1
        cartStore.value?.foodCart?.cartList?.let { cartsModel->
            val index = cartsModel.indexOfFirst {
                it.cartPId == foodModel.foodId
            }

            if (index != -1) {
                qty = cartsModel[index].cartQTY
            }
        }

        _foodSumModel.value = FoodSumModel().apply {
            this.qty = qty
            this.price = foodModel.foodPrice
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
                val index = carts.indexOfFirst {
                    it.cartPId == cart.cartPId
                }

                if (index != -1) {
                    if (cart.isCart) {
                        cartStore.counter += 1
                        carts[index].cartTotalPrice = cart.cartTotalPrice + cart.cartPrice

                        totalPrice.value?.let { it->
                            var totalPrice = it
                            totalPrice += cart.cartPrice
                            totalPrice
                        }?.also {
                            _totalPrice.value = it
                        }

                    }else{
                        cartStore.counter -= 1
                        carts[index].cartTotalPrice = cart.cartTotalPrice - cart.cartPrice

                        totalPrice.value?.let { it->
                            var totalPrice = it
                            totalPrice -= cart.cartPrice
                            totalPrice
                        }?.also {
                            _totalPrice.value = it
                        }
                    }
                }

                carts
            }

            cartStore
        }
    }

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
            _msgError.value = e.toString()
        }
    }

    fun deleteOrder() {
        _pay.value = null
    }

    private var creditCard = CreditCard(
            CreditCardType.CreditCardMasterCard,
            "",
            "",
            "",
            ""
    )

    fun createErrorMessage(msg: String) {
        _msgError.value = msg
    }

    fun createCreditCart(text: String, position: Int): CreditCard {
        return creditCard.apply {
            when(position){
                0 -> cardNumber = text
                1 -> name = text
                2 -> expiredData = text
                3 -> cvv = text
            }
        }
    }
}