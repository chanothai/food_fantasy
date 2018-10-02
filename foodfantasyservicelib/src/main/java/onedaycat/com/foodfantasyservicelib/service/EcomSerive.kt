package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.food_fantasy.oauth.OauthAdapter
import onedaycat.com.food_fantasy.oauth.OauthCognito
import onedaycat.com.foodfantasyservicelib.contract.creditcard_payment.CreditCardMemoPayment
import onedaycat.com.foodfantasyservicelib.contract.repository.*
import onedaycat.com.foodfantasyservicelib.validate.*

class EcomService {
    var paymentService = PaymentService(
            OrderFireStore(),
            CreditCardMemoPayment(),
            StockFireStore(),
            CartFireStore(),
            PaymentFireStore(),
            PaymentMemoValidate()
    )

    var orderService = OrderService(
            OrderFireStore(),
            OrderMemoValidate()
    )

    var cartService = CartService(
            StockFireStore(),
            CartFireStore(),
            CartMemoValidate()
    )

    var stockService = StockService(StockFireStore(), StockMemoValidate())

    var productService = ProductService(ProductFireStore(OauthCognito()), ProductMemoValidate())

    var userService = UserService(UserFireStore(), UserMemoryValidate())
}