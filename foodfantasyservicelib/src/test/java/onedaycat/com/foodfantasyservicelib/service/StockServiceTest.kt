package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.entity.ProductStock
import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.contract.repository.StockRepo
import onedaycat.com.foodfantasyservicelib.validate.StockValidate
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*

class StockServiceTest {
    @Mock
    private lateinit var stockService: StockService
    private lateinit var stockRepo: StockRepo
    private lateinit var stockValidate: StockValidate

    @Mock
    private lateinit var inputAddStock: AddProductStockInput
    private lateinit var inputSubStock: SubProductStockInput
    private lateinit var expPStock: ProductStock

    private var stock: ProductStock? = null
    @Before
    fun setup() {
        stockRepo = mock(StockRepo::class.java)
        stockValidate = mock(StockValidate::class.java)
        stockService = StockService(stockRepo, stockValidate)

        inputAddStock = AddProductStockInput(
                "111",
                10
        )

        inputSubStock = SubProductStockInput(
                "111",
                5
        )


        stock = ProductStock(
                "111",
                0
        )

        expPStock = stock!!.newProductStock("111", 20)!!
    }

    @Test
    fun `Add product stock success`() {
        val psGet = expPStock.newProductStock("111", 10)

        `when`(stockValidate.inputPStock(inputAddStock)).thenReturn(null)
        `when`(stockRepo.get(inputAddStock.productID)).thenReturn(Pair(psGet, null))
        `when`(stockRepo.upsert(expPStock)).thenReturn(null)

        val (pstock, error) = stockService.addProductStock(inputAddStock)

        Assert.assertNull(error)
        Assert.assertEquals(expPStock, pstock)

        verify(stockValidate).inputPStock(inputAddStock)
        verify(stockRepo).get(inputAddStock.productID)
        verify(stockRepo).upsert(expPStock)
    }

    @Test
    fun `Add product stock but validate failed`() {
        `when`(stockValidate.inputPStock(inputAddStock)).thenReturn(Errors.InvalidInputProductStock)

        val (pstock, error) = stockService.addProductStock(inputAddStock)

        Assert.assertNull(pstock)
        Assert.assertEquals(Errors.InvalidInputProductStock, error)

        verify(stockValidate).inputPStock(inputAddStock)
    }

    @Test
    fun `Add product stock but get product stock failed`() {
        `when`(stockValidate.inputPStock(inputAddStock)).thenReturn(null)
        `when`(stockRepo.get(inputAddStock.productID)).thenReturn(Pair(null, Errors.UnableGetProductStock))

        val (pstock, error) = stockService.addProductStock(inputAddStock)

        Assert.assertNull(pstock)
        Assert.assertEquals(Errors.UnableGetProductStock, error)

        verify(stockValidate).inputPStock(inputAddStock)
        verify(stockRepo).get(inputAddStock.productID)
    }

    @Test
    fun `Add product stock but not found product stock`() {
        val expPStock = expPStock.newProductStock("111", 10)

        `when`(stockValidate.inputPStock(inputAddStock)).thenReturn(null)
        `when`(stockRepo.get(inputAddStock.productID)).thenReturn(Pair(null, Errors.ProductStockNotFound))
        `when`(stockRepo.upsert(expPStock)).thenReturn(null)

        val (pstock, error) = stockService.addProductStock(inputAddStock)

        Assert.assertNull(error)
        Assert.assertEquals(expPStock, pstock)

        verify(stockValidate).inputPStock(inputAddStock)
        verify(stockRepo).get(inputAddStock.productID)
    }

    @Test
    fun `Add product stock but save or update failed`() {
        val psGet = expPStock.newProductStock("111", 10)

        `when`(stockValidate.inputPStock(inputAddStock)).thenReturn(null)
        `when`(stockRepo.get(inputAddStock.productID)).thenReturn(Pair(psGet, null))
        `when`(stockRepo.upsert(expPStock)).thenReturn(Errors.UnableSaveProductStock)

        val (pstock, error) = stockService.addProductStock(inputAddStock)

        Assert.assertNull(pstock)
        Assert.assertEquals(Errors.UnableSaveProductStock, error)

        verify(stockValidate).inputPStock(inputAddStock)
        verify(stockRepo).get(inputAddStock.productID)
        verify(stockRepo).upsert(expPStock)
    }

    @Test
    fun `Sub product stock success`() {
        val psGet = expPStock.newProductStock("111", 10)

        val expPStock = ProductStock(
                "111",
                5
        )

        `when`(stockValidate.inputSubStock(inputSubStock)).thenReturn(null)
        `when`(stockRepo.get(inputSubStock.productID)).thenReturn(Pair(psGet, null))
        `when`(stockRepo.upsert(expPStock)).thenReturn(null)

        val (pstock, error) = stockService.subProductStock(inputSubStock)

        Assert.assertNull(error)
        Assert.assertEquals(expPStock, pstock)

        verify(stockValidate).inputSubStock(inputSubStock)
        verify(stockRepo).get(inputSubStock.productID)
        verify(stockRepo).upsert(expPStock)
    }

    @Test
    fun `Sub product stock but validate failed`() {
        `when`(stockValidate.inputSubStock(inputSubStock)).thenReturn(Errors.InvalidInputProductStock)

        val (pstock, error) = stockService.subProductStock(inputSubStock)

        Assert.assertNull(pstock)
        Assert.assertEquals(Errors.InvalidInputProductStock, error)

        verify(stockValidate).inputSubStock(inputSubStock)
    }

    @Test
    fun `Sub product stock but get stock failed`() {
        `when`(stockValidate.inputSubStock(inputSubStock)).thenReturn(null)
        `when`(stockRepo.get(inputSubStock.productID)).thenReturn(Pair(null, Errors.UnableGetProductStock))

        val (pstock, error) = stockService.subProductStock(inputSubStock)

        Assert.assertNull(pstock)
        Assert.assertEquals(Errors.UnableGetProductStock, error)

        verify(stockValidate).inputSubStock(inputSubStock)
        verify(stockRepo).get(inputSubStock.productID)
    }

    @Test
    fun `Sub product stock but save or update failed`() {
        val psGet = expPStock.newProductStock("111", 10)
        `when`(stockValidate.inputSubStock(inputSubStock)).thenReturn(null)
        `when`(stockRepo.get(inputSubStock.productID)).thenReturn(Pair(psGet,null))
        `when`(stockRepo.upsert(psGet)).thenReturn(Errors.UnableSaveProductStock)

        val (pstock, error) = stockService.subProductStock(inputSubStock)

        Assert.assertNull(pstock)
        Assert.assertEquals(Errors.UnableSaveProductStock, error)

        verify(stockValidate).inputSubStock(inputSubStock)
        verify(stockRepo).get(inputSubStock.productID)
        verify(stockRepo).upsert(psGet)
    }

    @Test
    fun `Sub product stock failed`() {
        val psGet = expPStock.newProductStock("111", 2)

        `when`(stockValidate.inputSubStock(inputSubStock)).thenReturn(null)
        `when`(stockRepo.get(inputSubStock.productID)).thenReturn(Pair(psGet,null))

        val (pstock, error) = stockService.subProductStock(inputSubStock)

        Assert.assertNull(pstock)
        Assert.assertEquals(Errors.ProductOutOfStock, error)

        verify(stockValidate).inputSubStock(inputSubStock)
        verify(stockRepo).get(inputSubStock.productID)
    }
}