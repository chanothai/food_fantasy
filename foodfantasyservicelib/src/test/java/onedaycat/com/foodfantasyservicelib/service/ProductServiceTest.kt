package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.model.Product
import onedaycat.com.foodfantasyservicelib.repository.ProductMemo
import onedaycat.com.foodfantasyservicelib.repository.ProductRepo
import onedaycat.com.foodfantasyservicelib.repository.UserRepo
import onedaycat.com.foodfantasyservicelib.validate.ProductMemoValidate
import onedaycat.com.foodfantasyservicelib.validate.ProductValidate
import onedaycat.com.foodfantasyservicelib.validate.UserMemoryValidate
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*

class ProductServiceTest {
    @Mock
    private lateinit var productService: ProductService
    private lateinit var productRepo: ProductMemo
    private lateinit var productValidate: ProductValidate

    @Mock
    private lateinit var productCorrect: Product
    private lateinit var productIncorrect: Product

    @Before
    fun setup() {
        productRepo = mock(ProductMemo::class.java)
        productValidate = mock(ProductValidate::class.java)
        productService = ProductService(productRepo, productValidate)

        productCorrect = Product(
                "",
                "ต้มยำ",
                "อร่อยมาก",
                40,
                "",
                "")

        productIncorrect = Product(
                "",
                "",
                "",
                0,
                "",
                "")
    }

    @Test
    fun `create product with product correct`() {
        `when`(productValidate.hasProduct(productCorrect)).thenReturn(true)
        doNothing().`when`(productRepo).create(productCorrect)

        productService.createProduct(productCorrect)

        verify(productRepo).create(productCorrect)
        verify(productValidate).hasProduct(productCorrect)
    }

    @Test(expected = Exception::class)
    fun `create product then product name exist`() {
        `when`(productValidate.hasProduct(productCorrect)).thenReturn(true)
        `when`(productRepo.create(productCorrect)).thenThrow(Exception("product exist"))

        productService.createProduct(productCorrect)
    }

    @Test(expected = RuntimeException::class)
    fun `create product with product isNull`() {
        `when`(productValidate.hasProduct(null)).thenReturn(false)
        productService.createProduct(null)

        verify(productValidate).hasProduct(null)
    }

    @Test(expected = RuntimeException::class)
    fun `create product with product incorrect`() {
        `when`(productValidate.hasProduct(productIncorrect)).thenReturn(false)
        productService.createProduct(productIncorrect)

        verify(productValidate).hasProduct(productIncorrect)
    }

    @Test
    fun `update product with product correct`() {
        `when`(productValidate.hasProduct(productCorrect)).thenReturn(true)
        doNothing().`when`(productRepo).update(productCorrect)

        productService.updateProduct(productCorrect)

        verify(productValidate).hasProduct(productCorrect)
        verify(productRepo).update(productCorrect)
    }

    @Test(expected = Exception::class)
    fun `update product with product incorrect`() {
        `when`(productValidate.validateId(productIncorrect.id)).thenReturn(false)

        productService.updateProduct(productIncorrect)

        verify(productValidate).validateId(productIncorrect.id)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `update product with id not exist`() {
        `when`(productValidate.validateId(productCorrect.id)).thenReturn(true)
        `when`(productRepo.create(productCorrect)).thenThrow(IllegalArgumentException::class.java)

        productService.updateProduct(productCorrect)
    }

    @Test
    fun `delete product with product exist`() {
        `when`(productValidate.validateId(productCorrect.id)).thenReturn(true)
        doNothing().`when`(productRepo).delete(productCorrect.id)

        productService.deleteProduct(productCorrect.id)

        verify(productValidate).validateId(productCorrect.id)
        verify(productRepo).delete(productCorrect.id)
    }

    @Test(expected = Exception::class)
    fun `delete product with product not exist`() {
        `when`(productValidate.validateId(productCorrect.id)).thenReturn(true)
        `when`(productRepo.delete(productCorrect.id)).thenThrow(Exception("Product Not Found"))

        productService.deleteProduct(productCorrect.id)
    }
}