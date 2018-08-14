package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.entity.Product
import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.contract.repository.ProductPaging
import onedaycat.com.foodfantasyservicelib.contract.repository.ProductRepo
import onedaycat.com.foodfantasyservicelib.util.clock.Clock
import onedaycat.com.foodfantasyservicelib.util.idgen.IdGen
import onedaycat.com.foodfantasyservicelib.validate.ProductValidate

class ProductService(val productRepo: ProductRepo, val productValidate: ProductValidate) {

    fun createProduct(input: CreateProductInput): Pair<Product?, Error?> {

        var error = productValidate.inputProduct(input)

        if (error != null) {
            return Pair(null, error)
        }

        val product = Product(
                IdGen.NewId(),
                input.name,
                input.price,
                input.desc,
                input.image,
                Clock.NowUTC(),
                Clock.NowUTC()
        )

        error = productRepo.create(product)

        if (error != null) {
            return Pair(null, error)
        }

        return Pair(product, null)
    }

    fun removeProduct(input: RemoveProductInput): Error? {
        val error = productValidate.inputId(input.id)

        if (error != null) {
            return error
        }

        return productRepo.remove(input.id)
    }

    fun getProduct(input: GetProductInput): Pair<Product?, Error?> {
        val error = productValidate.inputId(input.productId)
        if (error != null) {
            return Pair(null, error)
        }

        return productRepo.get(input.productId)
    }

    fun getProducts(input: GetProductsInput): Pair<ProductPaging?, Error?> {

        val error = productValidate.inputLimitPaging(input)

        if (error != null) {
            return Pair(null, error)
        }

        val (productPaging, err) = productRepo.getAllWithPaging(input.limit)

        if (err != null) {
            return Pair(null, err)
        }

        return Pair(productPaging, null)
    }
}