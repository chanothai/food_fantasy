package onedaycat.com.foodfantasyservicelib.validate

import onedaycat.com.foodfantasyservicelib.model.User

interface UserValidate {
    fun hasUser(user: User?): Boolean
    fun checkId(id: String): Boolean
}

class UserMemoryValidate: UserValidate {
    override fun hasUser(user: User?): Boolean {
        if (user != null) {
            if ((user.id.isNotEmpty() || user.id.isNotBlank())
                    && (user.firstName.isNotEmpty() || user.firstName.isNotBlank())
                    && (user.lastName.isNotEmpty() || user.lastName.isNotBlank())
                    && (user.email.isNotEmpty() || user.email.isNotBlank())
                    && (user.password.isNotEmpty() || user.password.isNotBlank()))
            {
                return true
            }else {
                throw NullPointerException("Require some field in User")
            }
        }else {
            throw NullPointerException("Require user")
        }
    }

    override fun checkId(id: String): Boolean {
        if (id.isNotEmpty() || id.isNotBlank()) {
            return true
        }else {
            throw NullPointerException("Require user id")
        }
    }
}