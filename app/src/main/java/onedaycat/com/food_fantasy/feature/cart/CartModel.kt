package onedaycat.com.food_fantasy.feature.cart

data class CartModel(
        var cartPId: String? = null,
        var cartName: String? = null,
        var cartPrice: Int = 0,
        var cartTotalPrice: Int = 0,
        var cartImg: String? = null,
        var cartQTY: Int = 0,
        var cartQTYLimit: Int = 0,
        var status: Boolean = false,
        var cartProductAmount: Int = 0
)



