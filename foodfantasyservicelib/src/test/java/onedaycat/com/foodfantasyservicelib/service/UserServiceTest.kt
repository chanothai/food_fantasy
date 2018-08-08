package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.model.User
import onedaycat.com.foodfantasyservicelib.repository.UserMemo
import onedaycat.com.foodfantasyservicelib.repository.UserRepo
import onedaycat.com.foodfantasyservicelib.validate.UserMemoryValidate
import onedaycat.com.foodfantasyservicelib.validate.UserValidate
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*

class UserServiceTest {
    @Mock
    private lateinit var userService: UserService
    private lateinit var userRepo: UserRepo
    private lateinit var userValidate: UserMemoryValidate

    @Mock
    private lateinit var userCompete: User
    private lateinit var userIncomplete: User

    @Before
    fun setup() {
        userValidate = mock(UserMemoryValidate::class.java)
        userRepo = mock(UserRepo::class.java)
        userService = UserService(userRepo, userValidate)

        userCompete = User(
                "1",
                "ball.onedaycat@gmail.com",
                "password",
                "male",
                "ball",
                "omo",
                null,
                null,
                null)

        userIncomplete = User(
                "",
                "",
                "",
                "",
                "",
                "",
                null,
                null,
                null)

    }

    @Test
    fun `create user with user complete`() {
        val valueCapture = ArgumentCaptor.forClass(User::class.java)
        doNothing().`when`(userRepo).create(valueCapture.capture())
        `when`(userValidate.hasUser(userCompete)).thenReturn(true)

        userService.createUser(userCompete)

        verify(userRepo).create(userCompete)
        verify(userValidate).hasUser(userCompete)

        val expected = userCompete
        val result = valueCapture.value
        Assert.assertEquals(expected, result)
    }

    @Test(expected = Exception::class)
    fun `create user with email of user already`() {
        `when`(userValidate.hasUser(userCompete)).thenReturn(true)
        `when`(userRepo.create(userCompete)).thenThrow(Exception("User exist"))

        userService.createUser(userCompete)

        verify(userRepo).create(userCompete)
        verify(userValidate).hasUser(userCompete)
    }

    @Test(expected = Exception::class)
    fun `create user with input user isNull`() {
        `when`(userValidate.hasUser(isNull())).thenThrow(Exception::class.java)

        userService.createUser(null)

        verify(userValidate).hasUser(isNull())
    }

    @Test(expected = Exception::class)
    fun `create user with input user incomplete`() {
        `when`(userValidate.hasUser(userIncomplete)).thenThrow(Exception::class.java)

        userService.createUser(userIncomplete)

        verify(userValidate).hasUser(userIncomplete)
    }

    @Test
    fun `update user with user complete`() {
        val valueCapture = ArgumentCaptor.forClass(User::class.java)
        doNothing().`when`(userRepo).update(valueCapture.capture())
        `when`(userValidate.hasUser(userCompete)).thenReturn(true)

        userService.updateUser(userCompete)

        verify(userRepo).update(userCompete)
        verify(userValidate).hasUser(userCompete)

        val expected = userCompete
        val result = valueCapture.value
        Assert.assertEquals(expected, result)
    }

    @Test(expected = Exception::class)
    fun `update user with userId not exist`() {
        val valueCapture = ArgumentCaptor.forClass(User::class.java)
        `when`(userRepo.create(valueCapture.capture())).thenThrow(Exception::class.java)
        `when`(userValidate.hasUser(userCompete)).thenReturn(true)

        userService.createUser(userCompete)

        verify(userRepo).create(userCompete)
        verify(userValidate).hasUser(userCompete)

        val expected = userCompete
        val result = valueCapture.value
        Assert.assertEquals(expected, result)
    }

    @Test(expected = Exception::class)
    fun `update user with user incorrect`() {
        `when`(userValidate.hasUser(isNull())).thenThrow(Exception::class.java)
        userService.updateUser(null)
        verify(userValidate).hasUser(isNull())
    }

    @Test(expected = Exception::class)
    fun `update user with user Incomplete`() {
        `when`(userValidate.hasUser(userIncomplete)).thenThrow(Exception::class.java)
        userService.updateUser(userIncomplete)

        verify(userValidate).hasUser(userIncomplete)
    }

    @Test
    fun `get user with userId exist`() {
        val userId = "1000"
        `when`(userValidate.checkId(userId)).thenReturn(true)
        `when`(userRepo.get(userId)).thenReturn(userCompete)

        userService.getUser(userId)

        verify(userRepo).get(userId)
        verify(userValidate).checkId(userId)

        val expected = userCompete
        val result = userService.getUser(userId)
        Assert.assertEquals(expected, result)
    }

    @Test(expected = Exception::class)
    fun `get user with userId incorrect`() {
        val userId = "          "
        `when`(userValidate.checkId(userId)).thenThrow(Exception::class.java)

        userService.getUser(userId)

        verify(userValidate).checkId(userId)
    }

    @Test(expected = Exception::class)
    fun `get user with userId not exist`() {
        val userId = "000"
        `when`(userValidate.checkId(userId)).thenReturn(true)
        `when`(userRepo.get(userId)).thenThrow(Exception::class.java)

        userService.getUser(userId)

        verify(userRepo).get(userId)
        verify(userValidate).checkId(userId)
    }
}