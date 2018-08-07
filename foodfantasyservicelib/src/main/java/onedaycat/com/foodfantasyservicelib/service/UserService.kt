package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.model.User
import onedaycat.com.foodfantasyservicelib.repository.UserRepo
import onedaycat.com.foodfantasyservicelib.validate.UserValidate

class UserService(val userRepo: UserRepo, val check: UserValidate) {

    fun createUser(user: User?) {
        if (check.hasUser(user)) {
            userRepo.create(user)
        }
    }

    fun updateUser(user: User?) {
        if (check.hasUser(user)) {
            userRepo.update(user)
        }
    }

    fun getUser(userId: String): User? {
        var user: User? = null

        if (check.checkId(userId)) {
            user = userRepo.get(userId)
        }

        return user
    }
}