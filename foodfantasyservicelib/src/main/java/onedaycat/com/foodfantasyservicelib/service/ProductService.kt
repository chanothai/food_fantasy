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
        try {
            productValidate.inputProduct(input)

            val product = Product(
                    IdGen.NewId(),
                    input.name,
                    input.price,
                    input.desc,
                    input.image,
                    Clock.NowUTC(),
                    Clock.NowUTC()
            )

            productRepo.create(product)

            return product
        }catch (e:Error) {
            e.printStackTrace()
        }

        return null
    }

    fun removeProduct(input: RemoveProductInput): Boolean {
        try {
            productValidate.inputId(input.id)

            productRepo.remove(input.id)

            return true
        }catch (e:Error) {
            e.printStackTrace()
        }

        return false
    }

    fun getProduct(input: GetProductInput): Product? {
        try{
            productValidate.inputId(input.productId)

            return productRepo.get(input.productId)
        }catch (e:Error) {
            e.printStackTrace()
        }

        return null
    }

    fun getProducts(input: GetProductsInput): ProductPaging? {

        try {
            productValidate.inputLimitPaging(input)

            return productRepo.getAllWithPaging(input.limit)
        }catch (e:Error) {
            e.printStackTrace()
        }

        return null
    }
}