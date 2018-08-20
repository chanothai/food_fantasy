package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.entity.Product
import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.contract.repository.ProductPaging
import onedaycat.com.foodfantasyservicelib.contract.repository.ProductRepo
import onedaycat.com.foodfantasyservicelib.util.clock.Clock
import onedaycat.com.foodfantasyservicelib.util.idgen.IdGen
import onedaycat.com.foodfantasyservicelib.validate.ProductValidate

class ProductService(val productRepo: ProductRepo, val productValidate: ProductValidate) {

    fun createProduct(input: CreateProductInput): Product? {
        productValidate.inputProduct(input)

        val now = Clock.NowUTC()
        val product = Product(
                IdGen.NewId(),
                input.name,
                input.price,
                input.desc,
                input.image,
                now,
                now
        )

        productRepo.create(product)

        return product
    }

    fun removeProduct(input: RemoveProductInput) {
        productValidate.inputId(input.id)

        productRepo.remove(input.id)
    }

    fun getProduct(input: GetProductInput): Product? {
        productValidate.inputId(input.productId)

        return productRepo.get(input.productId)
    }

    fun getProducts(input: GetProductsInput): ProductPaging? {

        productValidate.inputLimitPaging(input)

        return productRepo.getAllWithPaging(input.limit)
    }
}