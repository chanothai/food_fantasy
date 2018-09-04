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
import onedaycat.com.food_fantasy.store.FoodCartLiveStore
import onedaycat.com.food_fantasy.store.FoodCartStore
import onedaycat.com.foodfantasyservicelib.contract.repository.ProductFireStore
import onedaycat.com.foodfantasyservicelib.contract.repository.ProductPaging
import onedaycat.com.foodfantasyservicelib.entity.Cart
import onedaycat.com.foodfantasyservicelib.entity.Product
import onedaycat.com.foodfantasyservicelib.input.GetProductsInput
import onedaycat.com.foodfantasyservicelib.service.EcomService
import onedaycat.com.foodfantasyservicelib.service.ProductService
import onedaycat.com.foodfantasyservicelib.validate.ProductMemoValidate

class FoodViewModel(
        private val foodCartLiveStore: FoodCartLiveStore,
        private val eComService: EcomService): ViewModel() {
    private var foodList = arrayListOf<FoodModel>()

    private val _foodData = MutableLiveData<FoodListModel>()

    val foodData: LiveData<FoodListModel>
    get() = _foodData

    fun loadProducts(input: GetProductsInput) {
        try {
            var productPaging: ProductPaging? = null
            launch(UI) {
                async(CommonPool) {
                    productPaging = eComService.productService.getProducts(input)!!
                    return@async
                }.await()

                if (foodList.size == 0) {
                    _foodData.value = addFoodModel(productPaging!!)
                    return@launch
                }

                if (foodList.size == productPaging!!.products.size) {

                    for ((i, food) in foodList.withIndex()) {
                        if (productPaging!!.products[i].id != food.foodId) {
                            foodList[i] = food
                        }
                    }

                    _foodData.value = addFoodModel(productPaging!!)
                    return@launch
                }


                addFoodModel(productPaging!!)
                _foodData.value = addFoodModel(productPaging!!)
            }
        }catch (e: Error) {
            _foodData.value = FoodListModel(arrayListOf())
        }
    }

    private fun addFoodModel(productPaging: ProductPaging): FoodListModel{
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
        val carts = foodCartLiveStore.liveData.value?.cartList

        if (carts?.size == 0) {
            setStatusWithCartEmpty()
            return
        }

        if (carts?.size == _foodData.value?.foodList!!.size) {
            return
        }

        setStatus(carts!!)
    }

    private fun setStatus(carts: ArrayList<CartModel>) {
        for ( food in _foodData.value?.foodList!!) {
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
}