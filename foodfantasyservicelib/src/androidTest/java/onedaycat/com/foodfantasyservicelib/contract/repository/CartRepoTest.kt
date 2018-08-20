package onedaycat.com.foodfantasyservicelib.contract.repository

import onedaycat.com.foodfantasyservicelib.entity.Cart
import onedaycat.com.foodfantasyservicelib.entity.newProductQTY
import onedaycat.com.foodfantasyservicelib.error.NotFoundException
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class CartRepoTest {

    private lateinit var cartRepo: CartRepo
    private lateinit var expCart: Cart

    @Before
    fun setup(){
        cartRepo = CartFireStore()
        expCart = Cart(
                "u1",
                mutableListOf(
                        newProductQTY("111", 100, 20),
                        newProductQTY("112", 200, 10)
                )
        )
    }

    @Test
    fun addCart() {
        cartRepo.upsert(expCart)
    }

    @Test
    fun getCartSuccess() {
        val cart = cartRepo.getByUserID(expCart.userId!!)
        Assert.assertEquals(expCart, cart)
    }

    @Test(expected = NotFoundException::class)
    fun getCartFailed() {
        val id = "434332"
        cartRepo.getByUserID(id)
    }
}