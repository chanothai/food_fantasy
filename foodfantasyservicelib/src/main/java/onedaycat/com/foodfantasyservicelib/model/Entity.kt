package onedaycat.com.foodfantasyservicelib.model

data class User(
        val id: String,
        val email: String,
        val password: String,
        val gender: String,
        val firstName: String,
        val lastName: String,
        val address: String?,
        val createDate: String?,
        val updateDate: String?)

data class Product(
        val id: String,
        val name:String,
        val description: String?,
        val price: Int,
        val createDate: String?,
        val updateDate: String?)

data class ProductList(
        val products: MutableList<Product?>
)

data class ProductCart(
        val product: Product,
        val qty: Int)

data class ProductCartList(
        val productList: MutableList<ProductCart>
)

data class Cart(
        val id: String,
        val product: ProductList?,
        val totalPrice: Int,
        val userId: String,
        val createData: String?)

data class Order(
        val id: String,
        val transactionId: String,
        val products: ProductList,
        val totalPrice: Int,
        val userId: String,
        val status: String,
        val createDate: String?)

data class OrderList(
        val orders: MutableList<Order>)

data class PaymentTransaction(
        val transactionId: String,
        val amount: Int, val orderId: String,
        val createDate: String?)

data class CreditCard(
        val id: String,
        val name: String,
        val ccv: Int,
        val expiredData: String,
        val expireYear: String)

data class Stock(
        val id: String,
        val product: Product,
        val qty:Int)