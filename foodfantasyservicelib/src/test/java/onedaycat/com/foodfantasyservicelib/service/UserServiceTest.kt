package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.entity.User
import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.contract.repository.UserRepo
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
        `when`(userValidate.inputUser(inputUser)).thenReturn(true)
        `when`(userRepo.getByEmail(inputUser.email)).thenReturn(Pair(null, null))
        `when`(userRepo.create(expUser)).thenReturn( null)

        val (user, error) = userService.createUser(inputUser)

        //Expected result
        Assert.assertNull(error)
        Assert.assertEquals(expUser.email, user!!.email)

        verify(userValidate).inputUser(inputUser)
        verify(userRepo).create(expUser)
        verify(userRepo).getByEmail(inputUser.email)
    }

    @Test
    fun `create user failed`() {
        val expError = Errors.UnableCreateUser

        `when`(userValidate.inputUser(inputUser)).thenReturn(true)
        `when`(userRepo.getByEmail(inputUser.email)).thenReturn(Pair(null, null))
        `when`(userRepo.create(expUser)).thenReturn(expError)

        val (user, error) = userService.createUser(inputUser)

        Assert.assertEquals(expError, error)
        Assert.assertNull(user)

        verify(userRepo).getByEmail(inputUser.email)
        verify(userRepo).create(expUser)
        verify(userValidate).inputUser(inputUser)
    }

    @Test
    fun `create user exist`() {
        val expError = Errors.EmailExist

        `when`(userValidate.inputUser(inputUser)).thenReturn(true)
        `when`(userRepo.getByEmail(inputUser.email)).thenReturn(Pair(expUser, null))

        val (user, error) = userService.createUser(inputUser)

        Assert.assertEquals(expError, error)
        Assert.assertEquals(expUser, user)

        verify(userRepo).getByEmail(inputUser.email)
        verify(userValidate).inputUser(inputUser)
    }

    @Test
    fun `check email exist failed`() {
        val expError = Errors.UserNotFound

        `when`(userValidate.inputUser(inputUser)).thenReturn(true)
        `when`(userRepo.getByEmail(inputUser.email)).thenReturn(Pair(null, expError))

        val (user, error) = userService.createUser(inputUser)

        Assert.assertNull(user)
        Assert.assertEquals(expError, error)

        verify(userValidate).inputUser(inputUser)
        verify(userRepo).getByEmail(inputUser.email)
    }

    @Test
    fun `validate failed`() {
        val expError = Errors.InvalidInput
        `when`(userValidate.inputUser(inputUser)).thenReturn(false)

        val (user, error) = userService.createUser(inputUser)

        Assert.assertNull(user)
        Assert.assertEquals(expError, error)

        verify(userValidate).inputUser(inputUser)
    }

    @Test
    fun `get user complete`() {
        `when`(userValidate.inputId(getUserInput.userId)).thenReturn(true)
        `when`(userRepo.get(getUserInput.userId)).thenReturn(Pair(expUser, null))

        val (user, error) = userService.getUser(getUserInput)

        Assert.assertNull(error)
        Assert.assertEquals(expUser, user)

        verify(userRepo).get(getUserInput.userId)
        verify(userValidate).inputId(getUserInput.userId)
    }

    @Test
    fun `get user then validate failed`() {
        val expError = Errors.InvalidInput
        getUserInput = GetUserInput("   ")
        `when`(userValidate.inputId(getUserInput.userId)).thenReturn(false)

        val (user, error) = userService.getUser(getUserInput)

        Assert.assertNull(user)
        Assert.assertEquals(expError, error)

        verify(userValidate).inputId(getUserInput.userId)
    }

    @Test
    fun `get user then not found`() {
        getUserInput = GetUserInput("10000")

        val expError = Errors.UserNotFound

        `when`(userValidate.inputId(getUserInput.userId)).thenReturn(true)
        `when`(userRepo.get(getUserInput.userId)).thenReturn(Pair(null, expError))

        val (user, error) = userService.getUser(getUserInput)

        Assert.assertEquals(expError, error)
        Assert.assertNull(user)

        verify(userValidate).inputId(getUserInput.userId)
        verify(userRepo).get(getUserInput.userId)
    }
}