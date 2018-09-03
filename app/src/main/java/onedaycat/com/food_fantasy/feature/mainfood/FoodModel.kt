package onedaycat.com.food_fantasy.mainfood

data class FoodModel(
        var foodId: String = "",
        var foodName: String = "",
        var foodDesc: String = "",
        var foodPrice: Int = 0,
        var foodIMG: String = "",
        var isAddToCart: Boolean = false
)

data class FoodListModel(
        var foodList: ArrayList<FoodModel>
)