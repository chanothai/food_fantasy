package onedaycat.com.food_fantasy.mainfood

interface AddRemoveCallback {
    fun addItem(foodModel: FoodModel)
    fun removeItem(foodModel: FoodModel)
    fun goCart(foodModel: FoodModel)
}