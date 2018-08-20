package onedaycat.com.foodfantasyservicelib.contract.repository

import onedaycat.com.foodfantasyservicelib.entity.Order
import onedaycat.com.foodfantasyservicelib.entity.State
import onedaycat.com.foodfantasyservicelib.entity.newProductQTY
import onedaycat.com.foodfantasyservicelib.error.BadRequestException
import onedaycat.com.foodfantasyservicelib.error.InternalError
import onedaycat.com.foodfantasyservicelib.error.NotFoundException
import onedaycat.com.foodfantasyservicelib.util.clock.Clock
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class OrderRepoTest {
    private lateinit var orderRepo: OrderRepo
    private lateinit var expOrder: Order

    @Before
    fun setup(){
        orderRepo = OrderFireStore()

        val now = Clock.NowUTC()
        expOrder = Order(
                "r1",
                "u1",
                mutableListOf(
                        newProductQTY("111", 100, 10),
                        newProductQTY("222", 200, 20)
                ),
                300,
                now,
                State.OrderStatus.PAID
        )
    }

    @Test
    fun upsertOrder() {
        orderRepo.upsert(expOrder)
    }

    @Test
    fun getOrder() {
        val order = orderRepo.get(expOrder.id!!)

        Assert.assertEquals(expOrder.id, order.id)
    }

    @Test(expected = InternalError::class)
    fun getOrderFailed() {
        val id = "33333"
        orderRepo.get(id)
    }
}