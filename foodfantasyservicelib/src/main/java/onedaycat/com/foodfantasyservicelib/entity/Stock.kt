package onedaycat.com.foodfantasyservicelib.entity

import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.error.Errors


data class ProductStockWithPrice(
        var productStock: ProductStock,
        var price: Int
){
    private var pstockPrice = this

    fun newProductStockWithPrice(productID: String, price: Int, qty: Int): ProductStockWithPrice {
        return ProductStockWithPrice(
                ProductStock().newProductStock(
                        productID,
                        qty
                )!!,
                price
        )
    }
}

data class ProductStock(
        var productID: String = "",
        var qty: Int = 0) {

    private var productStock: ProductStock? = this

    fun createProductStock(productID: String, qty: Int): ProductStock? {
        productStock = ProductStock(
                productID,
                qty
        )

        return productStock
    }

    fun newProductStock(productID: String, qty: Int): ProductStock? {
        productStock = ProductStock(
                productID
        )

        deposit(qty)

        return productStock
    }

    fun deposit(qty: Int) {
        productStock!!.qty += qty
    }

    fun withDraw(qty: Int): Pair<ProductStock?, Error?> {
        if ((productStock!!.qty - qty) < 0) {
            return Pair(null, Errors.ProductOutOfStock)
        }

        productStock!!.qty -= qty

        return Pair(
                ProductStock(
                        productStock!!.productID,
                        qty
                ), null)
    }

    fun has(qty: Int): Boolean {
        val currentStockQTY = productStock!!.qty
        if (currentStockQTY > qty) {
            return true
        }

        return false
    }
}