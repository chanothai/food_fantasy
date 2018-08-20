package onedaycat.com.foodfantasyservicelib.entity

import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.error.Errors


data class ProductStockWithPrice(
        var productStock: ProductStock? = null,
        var price: Int = 0
)

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

    fun withDraw(qty: Int): ProductStock? {
        if ((productStock!!.qty - qty) < 0) {
            throw Errors.ProductOutOfStock
        }

        productStock!!.qty -= qty

        return ProductStock(
                productStock!!.productID,
                productStock!!.qty
        )
    }

    fun has(qty: Int): Boolean {
        val currentStockQTY = productStock!!.qty
        if (currentStockQTY > qty) {
            return true
        }

        return false
    }
}