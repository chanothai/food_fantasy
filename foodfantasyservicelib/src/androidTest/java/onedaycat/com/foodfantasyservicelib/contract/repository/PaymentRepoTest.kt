package onedaycat.com.foodfantasyservicelib.contract.repository

import android.support.test.runner.AndroidJUnit4
import onedaycat.com.foodfantasyservicelib.entity.*
import onedaycat.com.foodfantasyservicelib.util.clock.Clock
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PaymentRepoTest {
    private lateinit var paymentRepo: PaymentRepo
    private lateinit var expOrder: Order
    private lateinit var expTransaction: Transaction
    private lateinit var expPStocks: MutableList<ProductStock?>

    @Before
    fun setup(){
        paymentRepo = PaymentFireStore()

        val now = Clock.NowUTC()
        expOrder = Order(
                "r1",
                "u1",
                mutableListOf(
                        ProductQTY("1111", "Apple",100,1),
                        ProductQTY("1112", "Apple",200,2)),
                300,
                now,
                State.OrderStatus.PAID)

        expTransaction = Transaction(
                "tx1",
                "r1",
                TransactionState.CHARGE,
                500,
                now
        )

        expPStocks = mutableListOf(
                ProductStock("1111", "Apple",50),
                ProductStock("1112", "Apple",50)
        )
    }

    @Test
    fun savePayment() {
        paymentRepo.savePayment(expOrder, expTransaction, expPStocks)
    }
}