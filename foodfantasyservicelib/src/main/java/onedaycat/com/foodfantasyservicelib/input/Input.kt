package onedaycat.com.foodfantasyservicelib.input

import onedaycat.com.foodfantasyservicelib.contract.creditcard_payment.CreditCard

enum class CreditCardType {
    CreditCardVisa {
        var CreditCardType = "VISA"
    },
    CreditCardMasterCard {
        var CreditCardType = "MASTER_CARD"
    }
}

class CreateUserInput(
        var email: String,
        var name: String,
        var password: String)

class GetUserInput(
        var userId: String
)

class CreateProductInput(
        var name: String,
        var desc: String,
        var price: Int,
        var image: String
)

class RemoveProductInput(
        var id: String
)

class GetProductInput(
        var productId: String
)

class GetProductsInput(
        var limit: Int = 1
)

class GetProductStocksInput(
        var productIds: ArrayList<String>
)

class AddToCartInput(
        var userID: String,
        var productID: String,
        var qty: Int
)

class AddProductStockInput(
        var productID: String,
        var qty: Int
)

class SubProductStockInput(
        var productID: String,
        var qty: Int
)

class RemoveFromCartInput(
        var userID: String,
        var productID: String,
        var qty: Int
)

class GetCartInput(
        var userID: String
)

class GetOrderInput(
        var id: String
)

class ChargeInput(
        var userID: String,
        var creditCard: CreditCard
)

class RefundInput(
        var userID: String,
        var orderID: String
)