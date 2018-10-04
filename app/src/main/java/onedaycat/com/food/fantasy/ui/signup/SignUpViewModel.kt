package onedaycat.com.food.fantasy.ui.signup

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import onedaycat.com.food.fantasy.service.CognitoService
import onedaycat.com.foodfantasyservicelib.entity.User
import onedaycat.com.foodfantasyservicelib.input.CreateUserInput

class SignUpViewModel(
        private val cognitoService: CognitoService
): ViewModel() {

    private val _msgError = MutableLiveData<String>()
    val msgErrorLiveData:LiveData<String>
    get() = _msgError

    private val _userLiveData = MutableLiveData<User>()
    val userLiveData: LiveData<User>
    get() = _userLiveData

    private fun <T> asyncTask(function: () -> T): Deferred<T> {
        return async(CommonPool) { function() }
    }

    suspend fun signUp(input: CreateUserInput) {
        try {
            var user: User? = null
            asyncTask {
                user = cognitoService.userAuthService.signUp(input)
            }.await()

            user?.let {
                _userLiveData.postValue(user)
            }
        }catch (e: Exception) {
            _msgError.postValue(e.message)
        }
    }
}