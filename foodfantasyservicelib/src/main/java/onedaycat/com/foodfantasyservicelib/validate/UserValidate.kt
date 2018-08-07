package onedaycat.com.foodfantasyservicelib.validate

import onedaycat.com.foodfantasyservicelib.model.User

interface UserValidate {
    fun hasUser(user: User?): Boolean
    fun checkId(id: String): Boolean
}

class UserMemoryValidate: UserValidate {
    override fun hasUser(user: User?): Boolean {
        try{
            if (user != null) {
                return conditionUser(user)
            }else {
                throw Exception("Require user")
            }
        }catch (e:Exception) {
            e.printStackTrace()
        }

        return false
    }

    private fun conditionUser(user:User) :Boolean{
        if ((user.id.isNotEmpty() || user.id.isNotBlank())
                && (user.firstName.isNotEmpty() || user.firstName.isNotBlank())
                && (user.lastName.isNotEmpty() || user.lastName.isNotBlank())
                && (user.email.isNotEmpty() || user.email.isNotBlank())
                && (user.password.isNotEmpty() || user.password.isNotBlank()))
        {
            return true
        }else {
            throw Exception("Require some field in User")
        }
    }

    override fun checkId(id: String): Boolean {
        try {
            if (id.isNotEmpty() || id.isNotBlank()) {
                return true
            }else {
                throw Exception("Require user id")
            }
        }catch (e:Exception) {
            e.printStackTrace()
        }

        return false
    }
}