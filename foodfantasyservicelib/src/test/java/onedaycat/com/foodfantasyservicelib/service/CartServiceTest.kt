package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.entity.*
import onedaycat.com.foodfantasyservicelib.contract.repository.CartRepo
import onedaycat.com.foodfantasyservicelib.contract.repository.StockRepo
import onedaycat.com.foodfantasyservicelib.error.*
import onedaycat.com.foodfantasyservicelib.validate.CartValidate
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*

class CartServiceTest {
    @Mock
    private lateinit var cartService: CartService
    private lateinit var cartValidate: CartValidate
    private lateinit var cartRepo: CartRepo
    private lateinit var stockRepo: StockRepo

    @Mock
    private lateinit var input: AddToCartInput
    private lateinit var inputRemove: RemoveFromCartInput
    private lateinit var inputCart: GetCartInput

    @Mock
    private lateinit var expCart: Cart
    private lateinit var stock:ProductStock
    private lateinit var stockWithPrice: ProductStockWithPrice

    @Before
    fun setup() {
        cartValidate = mock(CartValidate::class.java)
        cartRepo = mock(CartRepo::class.java)
        stockRepo = mock(StockRepo::class.java)
        cartService = CartService(stockRepo, cartRepo, cartValidate)

        stock = ProductStock().createProductStock("111", 50)!!
        stockWithPrice = ProductStockWithPrice(
                stock,
                100
        )

        input = AddToCartInput(
                "u1",
                "111",
                10
        )

        inputRemove = RemoveFromCartInput(
                "u1",
                "111",
                 5
        )

        inputCart = GetCartInput(
                "u1"
        )

        expCart = Cart(
                mutableListOf(
                        ProductQTY("111", 100, 15)
                )
        )
    }

    @Test
    fun `Add to cart success`() {
        val newCart = Cart(mutableListOf())
        newCart.addPQTY(newProductQTY(input.productID, 100,5), stock)

        doNothing().`when`(cartValidate).inputCart(input)
        `when`(cartRepo.getByUserID(input.userID)).thenReturn(newCart)
        `when`(stockRepo.getWithPrice(input.productID)).thenReturn(stockWithPrice)
        doNothing().`when`(cartRepo).upsert(expCart)

        val actualCart = cartService.addProductCart(input)

        Assert.assertEquals(expCart, actualCart)

        verify(cartValidate).inputCart(input)
        verify(cartRepo).getByUserID(input.userID)
        verify(stockRepo).getWithPrice(input.productID)
        verify(cartRepo).upsert(expCart)
    }

    @Test(expected = InvalidInputException::class)
    fun `Add cart but validate failed`() {
        `when`(cartValidate.inputCart(input)).thenThrow(Errors.InvalidInput)

        cartService.addProductCart(input)
    }

    @Test(expected = InternalError::class)
    fun `Add cart but get cart failed`() {
        doNothing().`when`(cartValidate).inputCart(input)
        `when`(cartRepo.getByUserID(input.userID)).thenThrow(Errors.UnableGetCart)

        cartService.addProductCart(input)
    }

    @Test(expected = InternalError::class)
    fun `get stock failed`() {
        val newCart = Cart(mutableListOf())
        newCart.addPQTY(newProductQTY(input.productID, 100, 5), stock)

        doNothing().`when`(cartValidate).inputCart(input)
        `when`(cartRepo.getByUserID(input.userID)).thenReturn(newCart)
        `when`(stockRepo.getWithPrice(input.productID)).thenThrow(Errors.UnableGetProductStock)

        cartService.addProductCart(input)
    }

    @Test(expected = BadRequestException::class)
    fun `Add cart failed`() {
        val stock = stock.newProductStock("111", 10)!!
        val stockWithPrice = ProductStockWithPrice(stock, 100)

        val newCart = Cart(mutableListOf())
        newCart.addPQTY(newProductQTY(input.productID, 100, 5), stockWithPrice.productStock)

        doNothing().`when`(cartValidate).inputCart(input)
        `when`(cartRepo.getByUserID(input.userID)).thenReturn(newCart)
        `when`(stockRepo.getWithPrice(input.productID)).thenReturn(stockWithPrice)

        cartService.addProductCart(input)
    }

    @Test(expected = InternalError::class)
    fun `Save cart failed`() {
        val newCart = Cart(mutableListOf())
        newCart.addPQTY(newProductQTY(input.productID, 100, 5), stock)

        doNothing().`when`(cartValidate).inputCart(input)
        `when`(cartRepo.getByUserID(input.userID)).thenReturn(newCart)
        `when`(stockRepo.getWithPrice(input.productID)).thenReturn(stockWithPrice)
        `when`(cartRepo.upsert(expCart)).thenThrow(Errors.UnableSaveCart)

        cartService.addProductCart(input)
    }


    @Test
    fun `remove cart success`() {
        val newCart = Cart(mutableListOf())
        newCart.addPQTY(newProductQTY("111", 100, 5), stock)
        newCart.addPQTY(newProductQTY("222", 100, 5), stock.newProductStock("222", 50)!!)

        val expCart = Cart(mutableListOf())
        expCart.addPQTY(newProductQTY("222", 100, 5), stock.newProductStock("222", 50)!!)

        doNothing().`when`(cartValidate).inputRemoveCart(inputRemove)
        `when`(cartRepo.getByUserID(inputRemove.userID)).thenReturn(newCart)
        `when`(stockRepo.getWithPrice(inputRemove.productID)).thenReturn(stockWithPrice)
        doNothing().`when`(cartRepo).upsert(expCart)

        val actualCart = cartService.removeFromeCart(inputRemove)

        Assert.assertEquals(expCart, actualCart)

        verify(cartValidate).inputRemoveCart(inputRemove)
        verify(cartRepo).getByUserID(inputRemove.userID)
        verify(stockRepo).getWithPrice(inputRemove.productID)
        verify(cartRepo).upsert(expCart)
    }

    @Test(expected = InvalidInputException::class)
    fun `remove cart but validate failed`() {
        `when`(cartValidate.inputRemoveCart(inputRemove)).thenThrow(Errors.InvalidInput)

        cartService.removeFromeCart(inputRemove)
    }

    @Test(expected = InternalError::class)
    fun `remove cart but get cart failed`() {
        doNothing().`when`(cartValidate).inputRemoveCart(inputRemove)
        `when`(cartRepo.getByUserID(inputRemove.userID)).thenThrow(Errors.UnableGetCart)

        cartService.removeFromeCart(inputRemove)
    }

    @Test(expected = InternalError::class)
    fun `remove cart but get stock failed`() {
        doNothing().`when`(cartValidate).inputRemoveCart(inputRemove)
        `when`(cartRepo.getByUserID(inputRemove.userID)).thenReturn(expCart)
        `when`(stockRepo.getWithPrice(inputRemove.productID)).thenThrow(Errors.UnableGetProductStock)

        cartService.removeFromeCart(inputRemove)
    }

    @Test(expected = NotFoundException::class)
    fun `remove cart failed`() {
        val newCart = Cart(mutableListOf())

        doNothing().`when`(cartValidate).inputRemoveCart(inputRemove)
        `when`(cartRepo.getByUserID(inputRemove.userID)).thenReturn(newCart)
        `when`(stockRepo.getWithPrice(inputRemove.productID)).thenReturn(stockWithPrice)

        cartService.removeFromeCart(inputRemove)
    }

    @Test(expected = InternalError::class)
    fun `remove cart but save or upadte failed`() {
        val newCart = Cart(mutableListOf())
        newCart.addPQTY(newProductQTY("111", 100, 5), stock)
        newCart.addPQTY(newProductQTY("222", 100, 5), stock.newProductStock("222", 50)!!)

        expCart = Cart(mutableListOf())
        expCart.addPQTY(newProductQTY("222", 100, 5), stock.newProductStock("222", 50)!!)

        doNothing().`when`(cartValidate).inputRemoveCart(inputRemove)
        `when`(cartRepo.getByUserID(inputRemove.userID)).thenReturn(newCart)
        `when`(stockRepo.getWithPrice(inputRemove.productID)).thenReturn(stockWithPrice)
        `when`(cartRepo.upsert(expCart)).thenThrow(Errors.UnableSaveCart)

        cartService.removeFromeCart(inputRemove)
    }


    @Test
    fun `get cart success`() {
        expCart = Cart(mutableListOf())
        expCart.addPQTY(newProductQTY("111", 100,5), stock)

        doNothing().`when`(cartValidate).inputGetCart(inputCart)
        `when`(cartRepo.getByUserID(input.userID)).thenReturn(expCart)

        val actualCart = cartService.getCartWithUserID(inputCart)

        Assert.assertEquals(expCart, actualCart)

        verify(cartValidate).inputGetCart(inputCart)
        verify(cartRepo).getByUserID(input.userID)
    }

    @Test(expected = InvalidInputException::class)
    fun `get cart but validate failed`() {
        `when`(cartValidate.inputGetCart(inputCart)).thenThrow(Errors.InvalidInput)

        cartService.getCartWithUserID(inputCart)
    }

    @Test(expected = InternalError::class)
    fun `get cart failed`() {
        doNothing().`when`(cartValidate).inputGetCart(inputCart)
        `when`(cartRepo.getByUserID(input.userID)).thenThrow(Errors.UnableGetCart)

        cartService.getCartWithUserID(inputCart)
    }
}