package onedaycat.com.food_fantasy.mainfood

data class FoodModel(
        var foodId: String,
        var foodName: String,
        var foodDesc: String,
        var foodPrice: Int,
        var foodIMG: String
)

data class FoodListModel(
        var foodList: ArrayList<FoodModel>
)