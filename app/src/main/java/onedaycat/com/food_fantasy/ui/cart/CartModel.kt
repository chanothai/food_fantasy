package onedaycat.com.food_fantasy.ui.cart

data class CartModel(
        var cartPId: String? = null,
        var cartName: String? = null,
        var cartPrice: Int = 0,
        var cartTotalPrice: Int = 0,
        var cartQTY: Int = 0,
        var isCart: Boolean = false
)



