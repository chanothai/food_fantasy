package onedaycat.com.foodfantasyservicelib.contract.repository

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import onedaycat.com.foodfantasyservicelib.entity.User
import onedaycat.com.foodfantasyservicelib.error.NotFoundException
import onedaycat.com.foodfantasyservicelib.util.clock.Clock
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserRepoTest {
    private lateinit var userRepo: UserFireStore
    private lateinit var input: User
    private lateinit var ctx: Context

    @Before
    fun setup() {
        ctx = InstrumentationRegistry.getContext()

        userRepo = UserFireStore()

        val now = Clock.NowUTC()

        input = User(
                "123456789",
                "chanothai@onedaycat.com",
                "ball",
                "password",
                now,
                now
        )

        Clock.setFreezeTimes(now)
    }

    @Test
    fun createProductSuccess() {
        userRepo.create(input)
    }

    @Test
    fun getEmailExistSuccess() {
        val user = userRepo.getByEmail(input.email)
        Assert.assertNotNull(user)
    }

    @Test(expected = NotFoundException::class)
    fun getEmailAndNotFoundUser(){
        val user = userRepo.getByEmail(" ")
        Assert.assertNull(user)
    }

    @Test
    fun getUserSuccess() {
        val user = userRepo.get(input.id)
        Assert.assertNotNull(user)
    }

    @Test(expected = NotFoundException::class)
    fun getUserFailed() {
        userRepo.get("12313132")
    }
}