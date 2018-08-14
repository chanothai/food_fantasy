package onedaycat.com.foodfantasyservicelib.contract.repository

import onedaycat.com.foodfantasyservicelib.entity.User
import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.error.Errors

interface UserRepo {
    fun create(user: User?): Error?
    fun getByEmail(email: String): Pair<User?, Error?>
    fun get(userId: String): Pair<User?, Error?>
}

class UserMemo: UserRepo {
    private var user: User? = null
    private var error:Error? = null
    private var response: Pair<User?, Error?> = Pair(user, Errors.EmailExist)

    override fun create(user: User?): Error? {
        return error
    }

    override fun getByEmail(email: String): Pair<User?, Error?> {
        return response
    }

    override fun get(userId: String): Pair<User?, Error?> {
        return response
    }
}

