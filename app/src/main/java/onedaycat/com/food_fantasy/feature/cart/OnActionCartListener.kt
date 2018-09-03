package onedaycat.com.food_fantasy.feature.cart

interface OnActionCartListener {
    fun onRemoveCart(cartModel: CartModel)
    fun onTextWatcherTotalPrice(totalPrice: Int)
    fun onActionIME(cartModel: CartModel)
}