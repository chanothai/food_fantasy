package onedaycat.com.food_fantasy.ui.signin

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.util.Log
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import onedaycat.com.food_fantasy.util.CognitoUserHelper
import onedaycat.com.foodfantasyservicelib.error.BadRequestException
import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.input.CreateUserInput
import onedaycat.com.foodfantasyservicelib.service.EcomService

class SignInViewModel(
        private val eComService: EcomService
): ViewModel() {

    private val _token = MutableLiveData<String>()
    val token: LiveData<String>
    get() = _token

    private val _msgError = MutableLiveData<String>()
    val msgError: LiveData<String>
    get() = _msgError

    private fun <T> asyncTask(function: () -> T): Deferred<T> {
        return async(CommonPool) { function() }
    }

    fun signInUser(context: Context, input: CreateUserInput) {
        try {
            val authentication = object : AuthenticationHandler {
                override fun onSuccess(userSession: CognitoUserSession?, newDevice: CognitoDevice?) {
                    userSession?.let {
                        _token.value = it.idToken?.jwtToken
                    }
                }

                override fun onFailure(exception: java.lang.Exception?) {
                    exception?.let {
                        throw BadRequestException(555, exception.message.toString())
                    }
                }

                override fun getAuthenticationDetails(authenticationContinuation: AuthenticationContinuation?, userId: String?) {
                    val authDetail = AuthenticationDetails(input.email, input.password, null)

                    authenticationContinuation?.setAuthenticationDetails(authDetail)
                    authenticationContinuation?.continueTask()
                }

                override fun authenticationChallenge(continuation: ChallengeContinuation?) {

                }

                override fun getMFACode(continuation: MultiFactorAuthenticationContinuation?) {

                }
            }
            authentication.let {
                CognitoUserHelper.cognitoUser(context).user.getSessionInBackground(it)
            }
        }catch (e: Error) {
            _msgError.value = e.message
        }
    }
}