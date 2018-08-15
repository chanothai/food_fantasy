package onedaycat.com.foodfantasyservicelib.contract.repository

import onedaycat.com.foodfantasyservicelib.entity.User
import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.error.Errors

interface UserRepo {
    fun create(user: User?)
    fun getByEmail(email: String): User?
    fun get(userId: String): User?
}

