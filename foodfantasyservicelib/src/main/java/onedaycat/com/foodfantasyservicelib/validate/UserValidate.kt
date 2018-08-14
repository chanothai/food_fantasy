package onedaycat.com.foodfantasyservicelib.validate

import onedaycat.com.foodfantasyservicelib.entity.User
import onedaycat.com.foodfantasyservicelib.service.CreateUserInput

interface UserValidate {
    fun inputUser(input: CreateUserInput?): Boolean
    fun inputId(id: String): Boolean
}

class UserMemoryValidate: UserValidate {
    override fun inputUser(input: CreateUserInput?): Boolean {
        if (input != null) {
            val result = conditionUser(input)
            return result
        }

        return false
    }

    private fun conditionUser(input:CreateUserInput) :Boolean{
        if ((input.name.isNotEmpty() || input.name.isNotBlank())
                && (input.email.isNotEmpty() || input.email.isNotBlank())
                && (input.password.isNotEmpty() || input.password.isNotBlank()))
        {
            return true
        }

        return false
    }

    override fun inputId(id: String): Boolean {
        if (id.isNotEmpty() || id.isNotBlank()) {
            return true
        }

        return false
    }
}