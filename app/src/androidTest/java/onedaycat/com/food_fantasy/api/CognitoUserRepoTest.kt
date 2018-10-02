package onedaycat.com.food_fantasy.api

import android.support.test.runner.AndroidJUnit4
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import onedaycat.com.foodfantasyservicelib.entity.User
import onedaycat.com.foodfantasyservicelib.entity.UserAuth
import onedaycat.com.foodfantasyservicelib.util.clock.Clock
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class CognitoUserRepoTest {
    private lateinit var cognitoUserRepo: CognitoUserRepo

    @Before
    fun setup() {
        cognitoUserRepo = CognitoUserRepo()
    }

    @Test
    fun signUp_Success() {
        val time = Clock.NowUTC()
        val user = User(
                "u1",
                "chanothai@onedaycat.com",
                "chanothai duangrahwa",
                "password22",
                "Male",
                time,
                time
        )

        cognitoUserRepo.create(user)
    }

    @Test(expected = Exception::class)
    fun signUp_Failed() {
        val time = Clock.NowUTC()
        val user = User(
                "u1",
                "chanothai@onedaycat.com",
                "chanothai duangrahwa",
                "password22",
                "Male",
                time,
                time
        )

        cognitoUserRepo.create(user)
    }

    @Test
    fun signIn_Success() {
        val user = User(
                "u1",
                "chanothai@onedaycat.com",
                "chanothai duangrahwa",
                "password",
                "Male",
                "",
                ""
        )

        cognitoUserRepo.authenticate(user)
    }

    @Test(expected = Exception::class)
    fun signIn_Failed() {
        val user = User(
                "u1",
                "chanothai@onedaycat.com",
                "chanothai duangrahwa",
                "password22",
                "Male",
                "",
                ""
        )

        runBlocking {
            async(CommonPool) {
                cognitoUserRepo.authenticate(user)
                return@async
            }.await()
        }
    }

    @Test
    fun checkUsername_ForgotPassword_Success() {
        val username = "omo.chanothai@gmail.com"
        cognitoUserRepo.change(username)
    }

    @Test(expected = Exception::class)
    fun checkUsername_ForgotPassword_Failed() {
        val username = "ball@gmail.com"

        cognitoUserRepo.change(username)
    }

    @Test(expected = Exception::class)
    fun confirmPassword_Failed() {
        val userAuth = UserAuth(
                "omo.chanothai@gmail.com",
                "password"
        ).apply {
            this.newPassword = "password22"
            this.code = "123"
        }

        cognitoUserRepo.confirm(userAuth)
    }
}