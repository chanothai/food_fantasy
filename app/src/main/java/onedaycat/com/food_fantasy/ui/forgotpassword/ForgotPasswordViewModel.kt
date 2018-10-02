package onedaycat.com.food_fantasy.ui.forgotpassword

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import onedaycat.com.food_fantasy.service.CognitoService
import onedaycat.com.foodfantasyservicelib.entity.UserAuth
import onedaycat.com.foodfantasyservicelib.input.GetConfirmPasswordInput
import onedaycat.com.foodfantasyservicelib.input.GetUsernameInput
import java.lang.Exception

class ForgotPasswordViewModel(
        private val cognitoService: CognitoService
): ViewModel() {

    private val _usernameLiveData = MutableLiveData<String>()
    val usernameLiveData: LiveData<String>
    get() = _usernameLiveData

    private val _msgErrorLiveData = MutableLiveData<String>()
    val msgErrorLiveData: LiveData<String>
    get() = _msgErrorLiveData

    private val _userAuthLiveData = MutableLiveData<UserAuth>()
    val userAuthLiveData: LiveData<UserAuth>
    get() = _userAuthLiveData

    private fun <T> asyncTask(function: () -> T): Deferred<T> {
        return async(CommonPool) { function() }
    }

    suspend fun forgotPassword(input: GetUsernameInput) {
        try {
            var username: String? = null
            asyncTask {
                username = cognitoService.userAuthService.forgotPassword(input)
            }.await()

            _usernameLiveData.postValue(username)
        }catch (e: Exception) {
            _msgErrorLiveData.postValue(e.message)
        }
    }

    suspend fun confirmPassword(input: GetConfirmPasswordInput) {
        try {
            var userAuth: UserAuth? = null
            asyncTask {
                userAuth = cognitoService.userAuthService.changePassword(input)
            }.await()

            _userAuthLiveData.postValue(userAuth)

        }catch (e: Exception) {
            _msgErrorLiveData.postValue(e.message)
        }
    }
}