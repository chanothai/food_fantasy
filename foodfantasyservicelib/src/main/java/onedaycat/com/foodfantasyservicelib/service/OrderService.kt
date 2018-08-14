package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.entity.Order
import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.contract.repository.OrderRepo
import onedaycat.com.foodfantasyservicelib.validate.OrderValidate

class OrderService(val orderRepo: OrderRepo, val orderValidate: OrderValidate) {
    fun getOrder(input: GetOrderInput): Order? {
        try {
            if (!orderValidate.inputGetOrder(input)) {
                throw Errors.InvalidInput
            }

            return orderRepo.get(input.id)
        }catch (e: Error) {
            e.printStackTrace()
        }

        return null
    }
}