package onedaycat.com.food.fantasy.ui.signin

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import onedaycat.com.food.fantasy.R
import onedaycat.com.food.fantasy.mainfood.activity.MainActivity
import onedaycat.com.food.fantasy.service.CognitoService
import onedaycat.com.food.fantasy.ui.forgotpassword.ForgotPasswordActivity
import onedaycat.com.food.fantasy.ui.signup.SignUpActivity
import onedaycat.com.food.fantasy.util.DateUtils
import onedaycat.com.food.fantasy.util.SharedPreferenceHelper
import onedaycat.com.food.fantasy.util.ViewModelUtil
import onedaycat.com.foodfantasyservicelib.input.GetUserAuthenInput


class SignInActivity : AppCompatActivity() {

    private lateinit var signInViewModel: SignInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        initViewModel()
        clickSignIn()
        clickSignUp()
        clickForgotPassword()
    }



    private fun initViewModel() {
        signInViewModel = ViewModelProviders.of(this, ViewModelUtil.createViewModelFor(
                SignInViewModel(CognitoService())
        )).get(SignInViewModel::class.java)

        msgErrorObserver()
        userCognitoObserver()
    }

    private fun userCognitoObserver() {
        signInViewModel.userCognitoLiveData.observe(this, Observer {token->
            token?.let {
                SharedPreferenceHelper.setString(
                        SharedPreferenceHelper.usernameKey, it.username)
                SharedPreferenceHelper.setString(
                        SharedPreferenceHelper.tokenKey, it.accessToken)
                SharedPreferenceHelper.setString("" +
                        SharedPreferenceHelper.expireKey, DateUtils.toSimpleString(it.expired))

                startActivity(Intent(this, MainActivity::class.java))
            }
        })
    }

    private fun msgErrorObserver() {
        signInViewModel.msgErrorLiveData.observe(this, Observer { msg->
            msg?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun clickForgotPassword() {
        s_forgot_password.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun clickSignIn() {
        w_btn_signin.setOnClickListener {
            val input = GetUserAuthenInput(
                    w_username.text.toString(),
                    w_password.text.toString()
            )

            launch(UI) {
                signInViewModel.signInUser(input)
            }
        }
    }

    private fun clickSignUp() {
        w_btn_signup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}
