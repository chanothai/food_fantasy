package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.entity.Product
import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.contract.repository.ProductMemo
import onedaycat.com.foodfantasyservicelib.contract.repository.ProductPaging
import onedaycat.com.foodfantasyservicelib.util.clock.Clock
import onedaycat.com.foodfantasyservicelib.util.idgen.IdGen
import onedaycat.com.foodfantasyservicelib.validate.ProductValidate
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
    private lateinit var productPaging: ProductPaging

    @Mock
    private lateinit var expProduct: Product

    @Mock
    private lateinit var input: CreateProductInput
    private lateinit var inputIncorrect: CreateProductInput
    private lateinit var getProductInput: GetProductInput

    @Before
    fun setup() {
        productRepo = mock(ProductMemo::class.java)
        productValidate = mock(ProductValidate::class.java)
        productService = ProductService(productRepo, productValidate)

        input = CreateProductInput(
                "Apple",
                "Apple from November",
                100000,
                "img.png"
        )

        getProductInput = GetProductInput(
                "xxxx"
        )

        inputIncorrect = CreateProductInput(
                "",
                "",
                -1,
                "")

        val now = Clock.NowUTC()
        expProduct = Product(
                "xxxx",
                name = input.name,
                price = input.price,
                desc = input.desc,
                image = input.image,
                createDate = now,
                updateDate = now
        )

        IdGen.setFreezeID(id = "xxxx")
        Clock.setFreezeTimes(now)
    }

    @Test
    fun `create product success`() {
        `when`(productValidate.inputProduct(input = input)).thenReturn(null)
        `when`(productRepo.create(expProduct)).thenReturn(null)

        val (product, error) = productService.createProduct(input)

        Assert.assertNull(error)
        Assert.assertEquals(expProduct.id, product!!.id)

        verify(productValidate).inputProduct(input)
        verify(productRepo).create(expProduct)
    }

    @Test
    fun `create product fail`() {
        val expError = Errors.UnableCreateProduct
        `when`(productValidate.inputProduct(input)).thenReturn(null)
        `when`(productRepo.create(expProduct)).thenReturn(expError)

        val (product, error) = productService.createProduct(input)

        Assert.assertEquals(expError, error)
        Assert.assertNull(product)

        verify(productValidate).inputProduct(input)
        verify(productRepo).create(expProduct)
    }

    @Test
    fun `create product then validate fail`() {
        val expError = Errors.InvalidInputProduct
        `when`(productValidate.inputProduct(inputIncorrect)).thenReturn(expError)

        val (product, error) = productService.createProduct(inputIncorrect)

        Assert.assertEquals(expError, error)
        Assert.assertNull(product)

        verify(productValidate).inputProduct(inputIncorrect)
    }

    @Test
    fun `remove product success`() {
        val input = RemoveProductInput(
                id = "xxxx"
        )

        `when`(productValidate.inputId(input.id)).thenReturn(null)
        `when`(productRepo.remove(input.id)).thenReturn(null)

        val error = productService.removeProduct(input)

        Assert.assertNull(error)

        verify(productValidate).inputId(input.id)
        verify(productRepo).remove(input.id)
    }

    @Test
    fun `remove product fail`() {
        val input = RemoveProductInput(
                id = "xxxx"
        )

        val expError = Errors.UnableRemoveProduct

        `when`(productValidate.inputId(input.id)).thenReturn(null)
        `when`(productRepo.remove(input.id)).thenReturn(expError)

        val error = productService.removeProduct(input)

        Assert.assertEquals(expError, error)

        verify(productValidate).inputId(input.id)
        verify(productRepo).remove(input.id)
    }

    @Test
    fun `remove product but validate failed`() {
        val input = RemoveProductInput(
                id = "   "
        )
        val expError = Errors.InvalidInput

        `when`(productValidate.inputId(input.id)).thenReturn(expError)

        val error = productService.removeProduct(input)

        Assert.assertEquals(expError, error)

        verify(productValidate).inputId(input.id)
    }

    @Test
    fun `get product success`() {
        val id = getProductInput.productId
        `when`(productValidate.inputId(id)).thenReturn(null)
        `when`(productRepo.get(id)).thenReturn(Pair(expProduct, null))

        val (product, error) = productService.getProduct(getProductInput)

        Assert.assertNull(error)
        Assert.assertEquals(expProduct, product)

        verify(productValidate).inputId(id)
        verify(productRepo).get(id)
    }

    @Test
    fun `get product not found`() {
        val id = getProductInput.productId
        val expError = Errors.ProductNotFound
        `when`(productValidate.inputId(id)).thenReturn(null)
        `when`(productRepo.get(id)).thenReturn(Pair(null, expError))

        val (product, error) = productService.getProduct(getProductInput)

        Assert.assertNull(product)
        Assert.assertEquals(expError, error)

        verify(productValidate).inputId(id)
        verify(productRepo).get(id)
    }

    @Test
    fun `get product then validate fail`() {
        val id = getProductInput.productId
        val expError = Errors.InvalidInput
        `when`(productValidate.inputId(id)).thenReturn(expError)

        val (product, error) = productService.getProduct(getProductInput)

        Assert.assertNull(product)
        Assert.assertEquals(expError, error)

        verify(productValidate).inputId(id)
    }

    @Test
    fun `get product all success`() {
        val input = GetProductsInput(
                limit = 10
        )

        productPaging = ProductPaging(
                products = mutableListOf(
                        Product("id1", "name1", 100000, "desc1", "img1", "1", "2"),
                        Product("id2", "name2", 200000, "desc2", "img2", "3", "4"),
                        Product("id3", "name3", 300000, "desc3", "img3", "5", "6")
                ),
                next = "111",
                prev = "222"
        )

        val expProductPaging = productPaging

        `when`(productValidate.inputLimitPaging(input)).thenReturn(null)
        `when`(productRepo.getAllWithPaging(input.limit)).thenReturn(Pair(expProductPaging, null))

        val (productPaging, error) = productService.getProducts(input)

        Assert.assertNull(error)
        Assert.assertEquals(expProductPaging, productPaging)

        verify(productValidate).inputLimitPaging(input)
        verify(productRepo).getAllWithPaging(input.limit)
    }

    @Test
    fun `get product all failed`() {
        val input = GetProductsInput(
                limit = 10
        )

        val expError = Errors.UnableGetProduct

        `when`(productValidate.inputLimitPaging(input)).thenReturn(null)
        `when`(productRepo.getAllWithPaging(input.limit)).thenReturn(Pair(null, expError))

        val (productPaging, error) = productService.getProducts(input)

        Assert.assertNull(productPaging)
        Assert.assertEquals(expError, error)

        verify(productValidate).inputLimitPaging(input)
        verify(productRepo).getAllWithPaging(input.limit)
    }

    @Test
    fun `get product all but validate failed`() {
        val input = GetProductsInput(
                limit = -1
        )

        val expError = Errors.InvalidInputLimitPaging

        `when`(productValidate.inputLimitPaging(input)).thenReturn(expError)

        val (productPaging, error) = productService.getProducts(input)

        Assert.assertNull(productPaging)
        Assert.assertEquals(expError, error)

        verify(productValidate).inputLimitPaging(input)
    }
}