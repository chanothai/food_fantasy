package onedaycat.com.food_fantasy.ui.mainfood

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import kotlinx.coroutines.experimental.runBlocking
import onedaycat.com.food_fantasy.mainfood.FoodListModel
import onedaycat.com.food_fantasy.mainfood.FoodModel
import onedaycat.com.food_fantasy.mainfood.FoodViewModel
import onedaycat.com.food_fantasy.store.CartStore
import onedaycat.com.food_fantasy.store.FoodCartLiveStore
import onedaycat.com.food_fantasy.store.FoodCartStore
import onedaycat.com.foodfantasyservicelib.contract.repository.ProductPaging
import onedaycat.com.foodfantasyservicelib.entity.Cart
import onedaycat.com.foodfantasyservicelib.entity.Product
import onedaycat.com.foodfantasyservicelib.entity.ProductQTY
import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.input.GetCartInput
import onedaycat.com.foodfantasyservicelib.input.GetProductsInput
import onedaycat.com.foodfantasyservicelib.service.CartService
import onedaycat.com.foodfantasyservicelib.service.EcomService
import onedaycat.com.foodfantasyservicelib.service.ProductService
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito.*

class FoodViewModelTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var foodCartLiveStore: FoodCartLiveStore
    private lateinit var cartStore: CartStore
    private lateinit var eComService: EcomService

    private lateinit var cartService:CartService
    private lateinit var productService: ProductService

    private lateinit var foodViewModel: FoodViewModel
    private lateinit var input: GetProductsInput
    private lateinit var productPaging: ProductPaging

    @Before
    fun setup() {
        with(CartStore) {
            this.foodCart = FoodCartStore(
                    "u1",
                    arrayListOf()
            )

            this.counter = 0

            foodCartLiveStore = FoodCartLiveStore(this)
        }

        eComService = mock(EcomService::class.java)
        productService = mock(ProductService::class.java)
        cartService = mock(CartService::class.java)

        `when`(eComService.productService).thenReturn(productService)
        `when`(eComService.cartService).thenReturn(cartService)

        foodViewModel = FoodViewModel(foodCartLiveStore, eComService)

        input = GetProductsInput().apply {
            this.limit = 1
        }
    }

    @Test
    fun loadProductSuccess() {
        val product = Product().apply {
            this.id = "1111"
            this.name = "p1"
            this.price = 300
            this.desc = "Test describe"
            this.image = "img1"
        }.also {
            productPaging = ProductPaging(
                    arrayListOf(it)
            )
        }

        FoodListModel(
                arrayListOf(FoodModel().apply {
                    this.foodId = product.id!!
                    this.foodName = product.name!!
                    this.foodPrice = product.price!!
                    this.foodDesc = product.desc!!
                    this.foodIMG = product.image!!
                })
        ).also {expected->

            `when`(eComService.productService.getProducts(input)).thenReturn(productPaging)

            runBlocking {
                foodViewModel.loadProducts(input)
            }

            val result = foodViewModel.foodData.value

            Assert.assertEquals(expected, result)

            verify(eComService.productService).getProducts(input)
        }
    }

    @Test
    fun loadProductFailed() {
        val msgExpect = Errors.ProductNotFound.message
        `when`(eComService.productService.getProducts(input)).thenThrow(Errors.ProductNotFound)

        runBlocking {
            foodViewModel.loadProducts(input)
        }

        val result = foodViewModel.msgError.value

        Assert.assertEquals(msgExpect, result)
    }

    @Test
    fun loadCartSuccess() {
        val input = GetCartInput(
                "u1"
        )

        val cart = Cart().apply {
            this.userId = input.userID
            this.products = arrayListOf(
                    ProductQTY(
                            "p1",
                            "Pork Potato",
                            450,
                            10
                    )
            )
        }

        `when`(eComService.cartService.getCartWithUserID(input)).thenReturn(cart)

        runBlocking {
            foodViewModel.loadCart(input)
        }

        val result = foodViewModel.cartStore.value

        Assert.assertEquals(cart, result)

        verify(eComService.cartService).getCartWithUserID(input)
    }
}