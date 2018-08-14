package onedaycat.com.foodfantasyservicelib.validate

import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.service.ChargeInput
import onedaycat.com.foodfantasyservicelib.service.RefundInput

interface PaymentValidate {
    fun inputCharge(input: ChargeInput)
    fun inputRefund(input: RefundInput)
}

class PaymentMemoValidate: PaymentValidate {
    override fun inputCharge(input: ChargeInput) {
        if (input.userID.isBlank() || input.userID.isEmpty()) {
            throw Errors.InvalidInput
        }

        if ((input.creditCard.name.isEmpty() || input.creditCard.name.isBlank())
                || (input.creditCard.ccv.isEmpty() || input.creditCard.ccv.isBlank())
                || (input.creditCard.expiredData.isEmpty() || input.creditCard.expiredData.isBlank())
                || (input.creditCard.expireYear.isEmpty() || input.creditCard.expireYear.isBlank())) {

            throw Errors.InvalidInput
        }
    }

    override fun inputRefund(input: RefundInput) {
        if (input.userID.isEmpty()
                || input.userID.isBlank()
                || input.orderID.isEmpty()
                || input.orderID.isBlank()) {

            throw Errors.InvalidInput
        }
    }
}