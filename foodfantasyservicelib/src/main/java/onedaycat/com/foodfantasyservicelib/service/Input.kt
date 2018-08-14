package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.contract.creditcart_payment.CreditCard

enum class CreditCardType {
    CreditCardVisa {
        var CreditCardType = "VISA"
    },
    CreditCardMasterCard {
        var CreditCardType = "MASTER_CARD"
    }
}

data class CreateUserInput(
        var email: String,
        var name: String,
        var password: String)

data class GetUserInput(
        var userId: String
)

data class CreateProductInput(
        var name: String,
        var desc: String,
        var price: Int,
        var image: String
)

data class RemoveProductInput(
        var id: String
)

data class GetProductInput(
        var productId: String
)

data class GetProductsInput(
        var limit: Int
)

data class AddToCartInput(
        var userID: String,
        var productID: String,
        var qty: Int
)

data class AddProductStockInput(
        var productID: String,
        var qty: Int
)

data class SubProductStockInput(
        var productID: String,
        var qty: Int
)

data class RemoveFromCartInput(
        var userID: String,
        var productID: String,
        var qty: Int
)

data class GetCartInput(
        var userID: String
)

data class GetOrderInput(
        var id: String
)

data class ChargeInput(
        var userID: String,
        var creditCard: CreditCard
)

data class RefundInput(
        var userID: String,
        var orderID: String
)
