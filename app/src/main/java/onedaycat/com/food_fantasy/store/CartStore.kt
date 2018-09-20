package onedaycat.com.food_fantasy.store

import onedaycat.com.food_fantasy.ui.cart.CartModel

object CartStore {

    @JvmStatic var foodCart: FoodCartStore? = null

    @JvmStatic var counter:Int = 0
}

data class FoodCartStore(
        var userId: String? = null,
        var cartList: ArrayList<CartModel>? = arrayListOf()
)