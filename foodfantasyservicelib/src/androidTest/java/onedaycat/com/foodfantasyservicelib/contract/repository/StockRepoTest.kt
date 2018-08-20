package onedaycat.com.foodfantasyservicelib.contract.repository

import junit.framework.Assert
import onedaycat.com.foodfantasyservicelib.entity.ProductStock
import onedaycat.com.foodfantasyservicelib.entity.ProductStockWithPrice
import onedaycat.com.foodfantasyservicelib.error.NotFoundException
import org.junit.Before
import org.junit.Test

class StockRepoTest {

    private lateinit var expPStockPrice: ProductStockWithPrice
    private lateinit var expPStock: ProductStock
    private lateinit var stockRepo: StockRepo

    @Before
    fun setup() {
        stockRepo = StockFireStore()

        expPStockPrice = ProductStockWithPrice(
                ProductStock(
                        "1111",
                        300
                ),
                100
        )

        expPStock = ProductStock(
                "1111",
                300
        )
    }

    @Test
    fun createStockProduct() {
        stockRepo.upsert(expPStock)
    }

    @Test
    fun getStockProduct() {
        val id = "1111"
        val pstock = stockRepo.get(id)
        Assert.assertEquals(expPStock, pstock)
    }

    @Test(expected = NotFoundException::class)
    fun getStockProductFailed() {
        val id = "2222"
        stockRepo.get(id)
    }

    @Test
    fun getStockProductWithPrice() {
        val id = "1111"
        val pstock = stockRepo.getWithPrice(id)
        Assert.assertEquals(expPStockPrice, pstock)
    }

    @Test(expected = NotFoundException::class)
    fun getStockProductWIthPriceFailed(){
        val id = "22222"
        stockRepo.getWithPrice(id)
    }

    @Test
    fun getByIds() {
        val ids = mutableListOf("1111", "1112")
        val pStocks = stockRepo.getByIDs(ids)

        Assert.assertNotNull(pStocks)
    }

    @Test(expected = NotFoundException::class)
    fun getByIdsFailed() {
        val ids = mutableListOf("1111", "2222")
        stockRepo.getByIDs(ids)
    }
}