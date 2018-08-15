package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.entity.User
import onedaycat.com.foodfantasyservicelib.contract.repository.UserRepo
import onedaycat.com.foodfantasyservicelib.error.*
import onedaycat.com.foodfantasyservicelib.util.clock.Clock
import onedaycat.com.foodfantasyservicelib.util.idgen.IdGen
import onedaycat.com.foodfantasyservicelib.validate.UserMemoryValidate
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*

class UserServiceTest {
    @Mock
    private lateinit var userService: UserService
    private lateinit var userRepo: UserRepo
    private lateinit var userValidate: UserMemoryValidate

    @Mock
    private lateinit var inputUser: CreateUserInput
    private lateinit var getUserInput: GetUserInput

    @Mock
    private lateinit var expUser: User

    @Before
    fun setup() {
        userValidate = mock(UserMemoryValidate::class.java)
        userRepo = mock(UserRepo::class.java)
        userService = UserService(userRepo, userValidate)

        inputUser = CreateUserInput(
                "ball.onedaycat@gmail.com",
                "ball omo",
                "password")

        val id = IdGen.NewId()
        val dateTime = Clock.NowUTC()

        expUser = User(
                id,
                inputUser.email,
                inputUser.name,
                inputUser.password,
                dateTime,
                dateTime)

        getUserInput = GetUserInput(id)

        Clock.setFreezeTimes(dateTime)
        IdGen.setFreezeID(id)
    }

    @Test
    fun `create user with user complete`() {
        doNothing().`when`(userValidate).inputUser(inputUser)
        `when`(userRepo.getByEmail(inputUser.email)).thenThrow(Errors.UserNotFound)
        doNothing().`when`(userRepo).create(expUser)

        val user = userService.createUser(inputUser)

        Assert.assertEquals(expUser.email, user!!.email)

        verify(userValidate).inputUser(inputUser)
        verify(userRepo).create(expUser)
        verify(userRepo).getByEmail(inputUser.email)
    }


    @Test(expected = InternalError::class)
    fun `create user failed`() {
        doNothing().`when`(userValidate).inputUser(inputUser)
        `when`(userRepo.getByEmail(inputUser.email)).thenThrow(Errors.UserNotFound)
        `when`(userRepo.create(expUser)).thenThrow(Errors.UnableCreateUser)

        val user = userService.createUser(inputUser)

        Assert.assertNull(user)

        verify(userRepo).getByEmail(inputUser.email)
        verify(userRepo).create(expUser)
        verify(userValidate).inputUser(inputUser)
    }

    @Test(expected = BadRequestException::class)
    fun `create user exist`() {
        doNothing().`when`(userValidate).inputUser(inputUser)
        `when`(userRepo.getByEmail(inputUser.email)).thenReturn(expUser)

        val user = userService.createUser(inputUser)

        Assert.assertNull(user)

        verify(userRepo).getByEmail(inputUser.email)
        verify(userValidate).inputUser(inputUser)
    }

    @Test(expected = InvalidInputException::class)
    fun `create user validate failed`() {
        `when`(userValidate.inputUser(inputUser)).thenThrow(Errors.InvalidInput)

        val user = userService.createUser(inputUser)

        Assert.assertNull(user)

        verify(userValidate).inputUser(inputUser)
    }

    @Test
    fun `get user complete`() {
        doNothing().`when`(userValidate).inputId(getUserInput.userId)
        `when`(userRepo.get(getUserInput.userId)).thenReturn(expUser)

        val user = userService.getUser(getUserInput)

        Assert.assertEquals(expUser, user)

        verify(userRepo).get(getUserInput.userId)
        verify(userValidate).inputId(getUserInput.userId)
    }

    @Test(expected = InvalidInputException::class)
    fun `get user then validate failed`() {
        getUserInput = GetUserInput("   ")
        `when`(userValidate.inputId(getUserInput.userId)).thenThrow(Errors.InvalidInput)

        val user = userService.getUser(getUserInput)

        Assert.assertNull(user)

        verify(userValidate).inputId(getUserInput.userId)
    }

    @Test(expected = NotFoundException::class)
    fun `get user then not found`() {
        getUserInput = GetUserInput("10000")

        doNothing().`when`(userValidate).inputId(getUserInput.userId)
        `when`(userRepo.get(getUserInput.userId)).thenThrow(Errors.UserNotFound)

        val user = userService.getUser(getUserInput)

        Assert.assertNull(user)

        verify(userValidate).inputId(getUserInput.userId)
        verify(userRepo).get(getUserInput.userId)
    }
}