package onedaycat.com.food_fantasy.store

import android.arch.lifecycle.MutableLiveData
import onedaycat.com.food_fantasy.mainfood.FoodModel

class FoodCartLiveStore(
        foodCartStore: FoodCartStore? = null
) {

    var liveData = MutableLiveData<FoodCartStore>()

    init {
        liveData.value = foodCartStore
    }
}