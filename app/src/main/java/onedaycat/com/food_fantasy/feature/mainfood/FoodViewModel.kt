package onedaycat.com.food_fantasy.mainfood

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import onedaycat.com.food_fantasy.store.FoodCartLiveStore
import onedaycat.com.food_fantasy.store.FoodCartStore
import onedaycat.com.foodfantasyservicelib.contract.repository.ProductFireStore
import onedaycat.com.foodfantasyservicelib.contract.repository.ProductPaging
import onedaycat.com.foodfantasyservicelib.entity.Product
import onedaycat.com.foodfantasyservicelib.input.GetProductsInput
import onedaycat.com.foodfantasyservicelib.service.ProductService
import onedaycat.com.foodfantasyservicelib.validate.ProductMemoValidate

class FoodViewModel(
        private val foodCartLiveStore: FoodCartLiveStore
): ViewModel() {
    private val productService = ProductService(ProductFireStore(), ProductMemoValidate())
    private var foodList = arrayListOf<FoodModel>()

    private val _foodData = MutableLiveData<FoodListModel>()

    val foodData: LiveData<FoodListModel>
    get() = _foodData

    fun loadProducts(input: GetProductsInput) {
        try {
            var productPaging: ProductPaging? = null
            launch(UI) {
                async(CommonPool) {
                    productPaging = productService.getProducts(input)!!
                }.await()

                if (foodList.size == 0) {
                    addFoodModel(productPaging!!)
                }else {
                    if (foodList.size == productPaging!!.products.size) {

                        for ((i, food) in foodList.withIndex()) {
                            if (productPaging!!.products[i].id != food.foodId) {
                                foodList[i] = food
                            }
                        }
                    }else {
                        addFoodModel(productPaging!!)
                    }
                }

                _foodData.value = FoodListModel(foodList)
            }
        }catch (e: Error) {
            e.printStackTrace()

            _foodData.value = FoodListModel(foodList)
        }
    }

    private fun addFoodModel(productPaging: ProductPaging) {
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
    }

    fun updateFoodCart() {
        val foodCartStore = foodCartLiveStore.liveData.value
        if (_foodData.value != null) {
            for ( food in _foodData.value?.foodList!!) {
                food.isAddToCart = false

                if (foodCartStore?.cartList?.size!! > 0) {
                    for (cart in foodCartStore.cartList!!) {
                        if (food.foodId == cart.cartPId) {
                            food.isAddToCart = cart.status
                            break
                        }
                    }
                }
            }
        }

    }
}