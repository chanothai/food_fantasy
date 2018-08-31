package onedaycat.com.food_fantasy.mainfood

import android.arch.lifecycle.MutableLiveData

class FoodCartLiveStore(
        private val foodCartStore: FoodCartStore? = null
) {

    var liveData = MutableLiveData<FoodCartStore>()

    init {
        liveData.value = foodCartStore
    }
}


object CartStore {
    lateinit var foodCart: FoodCartStore
}



data class FoodCartStore(
        var userId: String? = null,
        var foodList: ArrayList<FoodModel>? = arrayListOf()
)