package onedaycat.com.food_fantasy.mainfood

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import onedaycat.com.foodfantasyservicelib.contract.repository.ProductFireStore
import onedaycat.com.foodfantasyservicelib.contract.repository.ProductPaging
import onedaycat.com.foodfantasyservicelib.input.GetProductsInput
import onedaycat.com.foodfantasyservicelib.service.ProductService
import onedaycat.com.foodfantasyservicelib.validate.ProductMemoValidate

class FoodViewModel: ViewModel() {
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

                for (product in productPaging!!.products) {
                    val foodModel = FoodModel(
                            foodId = product.id!!,
                            foodName = product.name!!,
                            foodDesc = product.desc!!,
                            foodPrice = product.price!!,
                            foodIMG = product.image!!
                    )

                    foodList.add(foodModel)
                }

                _foodData.value = FoodListModel(foodList)
            }
        }catch (e: Error) {
            e.printStackTrace()

            _foodData.value = FoodListModel(foodList)
        }
    }


}