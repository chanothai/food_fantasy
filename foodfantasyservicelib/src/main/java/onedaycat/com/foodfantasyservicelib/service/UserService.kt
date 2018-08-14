package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.entity.User
import onedaycat.com.foodfantasyservicelib.error.*
import onedaycat.com.foodfantasyservicelib.contract.repository.UserRepo
import onedaycat.com.foodfantasyservicelib.util.clock.Clock
import onedaycat.com.foodfantasyservicelib.util.idgen.IdGen
import onedaycat.com.foodfantasyservicelib.validate.UserValidate

class UserService(val userRepo: UserRepo, val userValidate: UserValidate) {
    fun createUser(input: CreateUserInput): Pair<User?, Error?> {
        if (!userValidate.inputUser(input)) {
            return Pair(null, Errors.InvalidInput)
        }

        var (user , error) = userRepo.getByEmail(input.email)

        if (error != null) {
            return Pair(null, error)
        }

        if (user != null) {
            return Pair(user, Errors.EmailExist)
        }

        user = User(
                IdGen.NewId(),
                input.email,
                input.name,
                input.password,
                Clock.NowUTC(),
                Clock.NowUTC())

        error = userRepo.create(user)

        if (error != null) {
            return Pair(null, error)
        }

        return Pair(user, null)
    }

    fun getUser(input: GetUserInput): Pair<User?,Error?> {
        if (!userValidate.inputId(input.userId)) {
            return Pair(null, Errors.InvalidInput)
        }

        return userRepo.get(input.userId)
    }
}