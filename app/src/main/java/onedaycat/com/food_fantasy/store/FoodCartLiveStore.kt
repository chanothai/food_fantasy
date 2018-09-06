package onedaycat.com.food_fantasy.store

import android.arch.lifecycle.MutableLiveData
import onedaycat.com.food_fantasy.mainfood.FoodModel

class FoodCartLiveStore(
        cartStore: CartStore? = null
) {

    var liveData = MutableLiveData<CartStore>()

    init {
        liveData.value = cartStore
    }
}