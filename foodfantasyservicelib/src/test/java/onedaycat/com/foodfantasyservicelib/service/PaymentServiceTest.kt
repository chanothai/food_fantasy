package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.contract.creditcart_payment.CreditCardPayment
import onedaycat.com.foodfantasyservicelib.contract.repository.OrderRepo
import onedaycat.com.foodfantasyservicelib.contract.repository.PaymentRepo
import onedaycat.com.foodfantasyservicelib.contract.repository.StockRepo
import org.junit.Before
import org.mockito.Mock

class PaymentServiceTest {
    @Mock
    private lateinit var paymentRepo: PaymentRepo
    private lateinit var orderRepo: OrderRepo
    private lateinit var ccPaument: CreditCardPayment
    private lateinit var stockRepo: StockRepo

    @Before
    fun setup() {

    }
}