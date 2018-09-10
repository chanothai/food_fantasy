package onedaycat.com.food_fantasy.feature.cart

interface OnActionCartListener {
    fun onAddCart(cartModel: CartModel)
    fun onRemoveCart(cartModel: CartModel)
}