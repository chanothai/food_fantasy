package onedaycat.com.foodfantasyservicelib.contract.repository

import junit.framework.Assert
import onedaycat.com.foodfantasyservicelib.entity.Product
import onedaycat.com.foodfantasyservicelib.error.NotFoundException
import onedaycat.com.foodfantasyservicelib.util.clock.Clock
import onedaycat.com.foodfantasyservicelib.util.idgen.IdGen
import org.junit.Before
import org.junit.Test

class ProductRepoTest {
    private lateinit var productRepo:ProductRepo
    private lateinit var expProduct: Product

    @Before
    fun setup() {
        productRepo = ProductFireStore()

        val id = IdGen.NewId()
        val now = Clock.NowUTC()
        expProduct = Product(
                "1114",
                "Banana",
                100,
                "Banana from Thailand",
                "banana.png",
                now,
                now
        )

        IdGen.setFreezeID(id)
        Clock.setFreezeTimes(now)
    }

    @Test
    fun createProduct() {
        productRepo.create(expProduct)
    }

    @Test
    fun removeProductSuccess() {
        val id = "1114"
        productRepo.remove(id)
    }

    @Test(expected = NotFoundException::class)
    fun removeProductFailed() {
        val id = "12312312313"
        productRepo.remove(id)
    }

    @Test
    fun getProductSuccess() {
        val id = "1113"
        val product = productRepo.get(id)

        expProduct.id = id
        Assert.assertEquals(expProduct.name, product!!.name)
    }

    @Test(expected = NotFoundException::class)
    fun getProductFailed() {
        val id = "1234232423"
        productRepo.get(id)
    }

    @Test
    fun getProductAllWithPagingSuccess() {
        val products = productRepo.getAllWithPaging(3)
        Assert.assertEquals(3, products!!.products.size)
    }
}