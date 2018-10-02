package onedaycat.com.food_fantasy.oauth

import onedaycat.com.foodfantasyservicelib.entity.Token

interface OauthAdapter {
    fun validateToken(): Token
}