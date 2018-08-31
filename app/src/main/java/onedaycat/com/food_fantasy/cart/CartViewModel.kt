package onedaycat.com.food_fantasy.cart

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import onedaycat.com.food_fantasy.mainfood.FoodCartLiveStore
import onedaycat.com.food_fantasy.mainfood.FoodCartStore

class CartViewModel(
        foodCartLiveStore: FoodCartLiveStore
): ViewModel() {

    val foodCart: LiveData<FoodCartStore> = Transformations.map(foodCartLiveStore.liveData) {
        getNeedFoodCart(it)
    }

    private fun getNeedFoodCart(foodCartStore: FoodCartStore): FoodCartStore? {
        return foodCartStore
    }
}