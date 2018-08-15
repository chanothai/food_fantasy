package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.entity.Order
import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.contract.repository.OrderRepo
import onedaycat.com.foodfantasyservicelib.validate.OrderValidate

class OrderService(val orderRepo: OrderRepo, val orderValidate: OrderValidate) {
    fun getOrder(input: GetOrderInput): Order? {
        orderValidate.inputGetOrder(input)

        return orderRepo.get(input.id)
    }
}