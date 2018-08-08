package onedaycat.com.foodfantasyservicelib.repository

import onedaycat.com.foodfantasyservicelib.model.User

interface UserRepo {
    fun create(user: User?)
    fun update(user: User?)
    fun get(userId: String): User?
}

class UserMemo: UserRepo {
    private val user: User? = null

    override fun create(user: User?) {
        try {
            if (user!!.equals("ball.onedaycat@gmail.com")) {
                throw Exception("User exist")
            }
        }catch (e:Exception) {
            e.printStackTrace()
        }
    }

    override fun update(user: User?) {

    }

    override fun get(userId: String): User? {
        return user
    }
}

