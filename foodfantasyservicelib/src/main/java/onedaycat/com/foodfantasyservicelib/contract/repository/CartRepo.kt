package onedaycat.com.foodfantasyservicelib.contract.repository

import onedaycat.com.foodfantasyservicelib.entity.Cart

interface CartRepo {
    fun upsert(cart: Cart?)
    fun getByUserID(userId: String): Cart?
}