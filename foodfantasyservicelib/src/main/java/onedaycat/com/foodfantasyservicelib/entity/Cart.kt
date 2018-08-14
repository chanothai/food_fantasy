package onedaycat.com.foodfantasyservicelib.entity

import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.error.Errors

data class Cart(
        var products: MutableList<ProductQTY?>)
{
    private var cart = this
    private fun checkStock(productQTY: ProductQTY, stock: ProductStock) {
        if (productQTY.productId != stock.productID) {
            throw Errors.ProductNotMatched
        }

        var qty = productQTY.qty

        if (products.size > 0) {
            //Sum qty
            for (value in products) {
                if (value!!.productId == productQTY.productId) {
                    qty += value.qty
                    productQTY.qty = qty
                }
            }
        }

        if (!stock.has(qty)) {
            throw Errors.ProductOutOfStock
        }
    }

    //Compare product qty
    fun addPQTY(productQTY: ProductQTY, stock: ProductStock) {
        checkStock(productQTY, stock)

        if (products.size == 0) {
            products.add(productQTY)
        }

        for ((i, value) in products.withIndex()) {
            if (value!!.productId == productQTY.productId) {
                products.set(i, productQTY)
                return
            }
        }

        products.add(productQTY)
    }

    fun remove(newProductQTY: ProductQTY, stock: ProductStock) {
        if (products.size == 0) {
            throw Errors.ProductNotFound
        }

        for ((i, productQty) in products.withIndex()) {
            if (newProductQTY.productId == productQty!!.productId) {
                val result = productQty.qty - newProductQTY.qty

                if (result < 0) {
                    throw Errors.UnableRemoveProduct
                }

                if (result == 0) {
                    products.removeAt(i)
                    stock.qty += newProductQTY.qty
                    return
                }

                productQty.qty = result

                products.set(i, productQty)

                stock.qty += newProductQTY.qty
                return
            }
        }

        throw Errors.ProductNotFound
    }

    fun productIDs(): MutableList<String> {
        val arrProductId = mutableListOf<String>()
        if (cart.products.size == 0) {
            throw Errors.ProductNotFound
        }

        for (product in cart.products) {
            arrProductId.add(product!!.productId!!)
        }

        return arrProductId
    }

    fun getPQTY(productId: String): ProductQTY? {
        if (cart.products.size == 0) {
            return null
        }

        for (pQTY in cart.products) {
            if (productId == pQTY!!.productId) {
                return pQTY
            }
        }

        return null
    }

    fun toProductQTYList(): MutableList<ProductQTY?> {
        return cart.toProductQTYList()
    }

    fun totalPrice(): Int {
        var sumTotalPrice = 0
        for (product in cart.products) {
            sumTotalPrice += product!!.price!!
        }

        return sumTotalPrice
    }
}
