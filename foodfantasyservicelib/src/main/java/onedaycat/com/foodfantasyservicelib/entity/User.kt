package onedaycat.com.foodfantasyservicelib.entity

data class User(
        var id: String,
        val email:String,
        var name:String,
        var password: String,
        var createDate: String,
        var updateDate: String)
