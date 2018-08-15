package onedaycat.com.foodfantasyservicelib.validate

import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.service.GetOrderInput

interface OrderValidate {
    fun inputGetOrder(input: GetOrderInput)
}

class OrderMemoValidate: OrderValidate {
    override fun inputGetOrder(input: GetOrderInput) {
        if (input.id.isEmpty() || input.id.isBlank()) {
            throw Errors.InvalidInput
        }
    }
}