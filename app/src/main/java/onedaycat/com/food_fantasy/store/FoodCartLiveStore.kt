package onedaycat.com.food_fantasy.store

import android.arch.lifecycle.MutableLiveData
import onedaycat.com.food_fantasy.mainfood.FoodModel

object FoodCartLiveStores {
    var liveData = MutableLiveData<CartStore>()

    init {
        CartStore.let {
            liveData.postValue(CartStore)
        }
    }
}