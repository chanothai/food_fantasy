package onedaycat.com.food_fantasy.ui.cart

interface OnActionCartListener {
    fun onAddCart(cartModel: CartModel)
    fun onRemoveCart(cartModel: CartModel)
    fun onTextChange(text: String, position: Int)
}