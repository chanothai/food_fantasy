package onedaycat.com.foodfantasyservicelib.entity

data class Product(
        var id: String? = null,
        var name:String? = null,
        var price: Int? = null,
        var desc: String? = null,
        var image: String? = null,
        var createDate: String? = null,
        var updateDate: String? = null)

//ProductQTY Product and qty
data class ProductQTY(
        var productId: String?,
        var price: Int?,
        var qty: Int) {
}

fun newProductQTYWithProduct(product: Product, qty: Int): ProductQTY {
    return ProductQTY(
            productId = product.id,
            price = product.price,
            qty = qty
    )
}
//NewProductQTY create ProductQTY
fun newProductQTY(productId: String, price: Int?, qty: Int): ProductQTY {
    return ProductQTY(
            productId = productId,
            price = price,
            qty = qty)
}




