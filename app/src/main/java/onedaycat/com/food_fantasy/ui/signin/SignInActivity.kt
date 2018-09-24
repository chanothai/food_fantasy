package onedaycat.com.food_fantasy.ui.signin

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import onedaycat.com.food_fantasy.R
import onedaycat.com.food_fantasy.mainfood.activity.MainActivity
import onedaycat.com.food_fantasy.util.ViewModelUtil
import onedaycat.com.foodfantasyservicelib.input.CreateUserInput
import onedaycat.com.foodfantasyservicelib.service.EcomService
import java.lang.Exception

class SignInActivity : AppCompatActivity() {

    private val TAG = "AWS"
    private var password: String? = null
    private var username: String? = null

    private lateinit var editUser: EditText
    private lateinit var editPass: EditText

    private lateinit var signInViewModel: SignInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        bindView()
        initViewModel()
        clickSignIn()
    }

    private fun initViewModel() {
        signInViewModel = ViewModelProviders.of(this, ViewModelUtil.createViewModelFor(SignInViewModel(EcomService())))
                .get(SignInViewModel::class.java)

        msgErrorObserver()
        tokenObserver()
    }

    private fun bindView() {
        editUser = findViewById(R.id.w_username)
        editPass = findViewById(R.id.w_password)

        editUser.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                username = char.toString()
            }
        })

        editPass.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                password = char.toString()
            }
        })
    }

    private fun tokenObserver() {
        signInViewModel.token.observe(this, Observer {token->
            token?.let {
                startActivity(Intent(this, MainActivity::class.java))
            }
        })
    }

    private fun msgErrorObserver() {
        signInViewModel.msgError.observe(this, Observer {msg->
            msg?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun clickSignIn() {
        w_btn_signin.setOnClickListener {
            username?.let {user->
                password?.let {pass->

                    if (user.isEmpty() || pass.isEmpty()) {
                        Toast.makeText(this, "Please put username and password.", Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }

                    pass
                }?.also {pass->
                    val input = CreateUserInput(
                            user,
                            "",
                            pass
                    )

                    launch(UI) {
                        signInViewModel.signInUser(this@SignInActivity, input)
                    }
                }
            }
        }
    }
}
