package onedaycat.com.food_fantasy.ui.signin

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import onedaycat.com.food_fantasy.service.CognitoService
import onedaycat.com.food_fantasy.util.DateUtils
import onedaycat.com.food_fantasy.util.SharedPreferenceHelper
import onedaycat.com.foodfantasyservicelib.entity.UserCognito
import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.input.GetUserAuthenInput
import java.lang.Exception

class SignInViewModel(
        private val cognitoService: CognitoService
): ViewModel() {

    private val _msgError = MutableLiveData<String>()
    val msgErrorLiveData: LiveData<String>
    get() = _msgError

    private val _userCognito = MutableLiveData<UserCognito>()
    val userCognitoLiveData: LiveData<UserCognito>
    get() = _userCognito

    private fun <T> asyncTask(function: () -> T): Deferred<T> {
        return async(CommonPool) { function() }
    }

    suspend fun signInUser(input: GetUserAuthenInput) {
        try {
            var userCognito: UserCognito? = null
            asyncTask {
                userCognito = cognitoService.userAuthService.signIn(input)
            }.await()

            userCognito?.let {
                _userCognito.postValue(it)
            }
        }catch (e: Exception) {
            _msgError.postValue(e.message)
        }
    }
}