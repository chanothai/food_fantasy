package onedaycat.com.food_fantasy.ui.mainfood

import android.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.experimental.runBlocking
import onedaycat.com.food_fantasy.mainfood.FoodListModel
import onedaycat.com.food_fantasy.mainfood.FoodModel
import onedaycat.com.food_fantasy.mainfood.FoodViewModel
import onedaycat.com.food_fantasy.store.FoodCartLiveStore
import onedaycat.com.foodfantasyservicelib.contract.repository.ProductPaging
import onedaycat.com.foodfantasyservicelib.entity.Product
import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.input.GetProductsInput
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

    lateinit var foodCartLiveStore: FoodCartLiveStore
    private lateinit var eComService: EcomService

    private lateinit var productService: ProductService

    private lateinit var foodViewModel: FoodViewModel
    private lateinit var input: GetProductsInput
    private lateinit var productPaging: ProductPaging

    @Before
    fun setup() {
        foodCartLiveStore = mock(FoodCartLiveStore::class.java)
        eComService = mock(EcomService::class.java)
        productService = mock(ProductService::class.java)

        `when`(eComService.productService).thenReturn(productService)

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
}