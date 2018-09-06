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
import onedaycat.com.food_fantasy.store.FoodCartStore
import onedaycat.com.foodfantasyservicelib.contract.repository.ProductFireStore
import onedaycat.com.foodfantasyservicelib.contract.repository.ProductPaging
import onedaycat.com.foodfantasyservicelib.entity.Cart
import onedaycat.com.foodfantasyservicelib.entity.Product
import onedaycat.com.foodfantasyservicelib.input.AddToCartInput
import onedaycat.com.foodfantasyservicelib.input.GetProductsInput
import onedaycat.com.foodfantasyservicelib.input.RemoveFromCartInput
import onedaycat.com.foodfantasyservicelib.service.EcomService
import onedaycat.com.foodfantasyservicelib.service.ProductService
import onedaycat.com.foodfantasyservicelib.validate.ProductMemoValidate

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

    fun updateFoodStatus() {
        val carts = cartStore.value?.foodCart?.cartList

        carts?.let {
            if (it.size == 0) {
                setStatusWithCartEmpty()
                return
            }
            it
        }?.also {
            if (it.size == _foodData.value!!.foodList.size) {
                return@also
            }
        }?.run {
            setStatus(this)
        }
    }

    private fun setStatus(carts: ArrayList<CartModel>) {
        for (food in _foodData.value?.foodList!!) {
            food.isAddToCart = false

            for (cart in carts) {
                if (food.foodId == cart.cartPId) {
                    food.isAddToCart = cart.status
                    break
                }
            }
        }
    }

    private fun setStatusWithCartEmpty() {
        if (_foodData.value == null) {
            return
        }

        for (food in _foodData.value?.foodList!!) {
            food.isAddToCart = false
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

                cart?.let { cart ->
                    foodCartLiveStore.liveData.value = cartStore.value?.let { cartStore ->
                        cartStore.counter = cart.products.size

                        val indexCart = cart.products.indexOfFirst {
                            it?.productId == foodModel.foodId
                        }

                        if (indexCart != -1) {
                            cartStore.foodCart?.cartList?.add(mapFoodToCart(foodModel))
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

    private fun mapFoodToCart(foodModel: FoodModel): CartModel {
        return CartModel().apply {
            cartPId = foodModel.foodId
            cartName = foodModel.foodName
            cartQTY = 1
            cartPrice = foodModel.foodPrice
            cartImg = foodModel.foodIMG
            cartTotalPrice = foodModel.foodPrice
            status = foodModel.isAddToCart
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
                            cartStore.foodCart?.cartList?.remove(mapFoodToCart(foodModel))
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
}