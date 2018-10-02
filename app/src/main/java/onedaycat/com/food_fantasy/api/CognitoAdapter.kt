package onedaycat.com.food_fantasy.api

import onedaycat.com.foodfantasyservicelib.entity.User
import onedaycat.com.foodfantasyservicelib.entity.UserAuth
import onedaycat.com.foodfantasyservicelib.entity.UserCognito
import onedaycat.com.foodfantasyservicelib.input.GetConfirmPasswordInput

interface CognitoAdapter {
    fun create(user: User)
    fun authenticate(user: User): UserCognito
    fun change(username: String)
    fun confirm(userAuth: UserAuth)
}