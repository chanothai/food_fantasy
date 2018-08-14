package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.entity.*
import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.contract.repository.CartRepo
import onedaycat.com.foodfantasyservicelib.contract.repository.StockRepo
import onedaycat.com.foodfantasyservicelib.validate.CartValidate

class CartService(val stockRepo: StockRepo,val cartRepo: CartRepo, val cartValidate: CartValidate) {

    fun addProductCart(input: AddToCartInput): Cart? {
        try {
            cartValidate.inputCart(input)

            //get cart if not found cart will create new cart
            val cart = cartRepo.getByUserID(input.userID)

            //get stock
            val pstock = stockRepo.getWithPrice(input.productID)!!

            //create new productQTY
            val newProductQTY = newProductQTY(pstock.productStock.productID, pstock.price, input.qty)

            //add product to cart
            cart!!.addPQTY(newProductQTY, pstock.productStock)

            //Save or Update Cart
            cartRepo.upsert(cart)

            return cart
        }catch (e: Error) {
            e.printStackTrace()
        }

        return null
    }

    fun removeFromeCart(input: RemoveFromCartInput): Cart? {
        try {
            cartValidate.inputRemoveCart(input)

            val cart = cartRepo.getByUserID(input.userID)

            val pstock = stockRepo.getWithPrice(input.productID)

            cart!!.remove(newProductQTY(pstock!!.productStock.productID, pstock.price, input.qty), pstock.productStock)

            cartRepo.upsert(cart)

            return cart
        }catch (e: Error) {
            e.printStackTrace()
        }

        return null
    }

    fun getCartWithUserID(input:GetCartInput): Cart? {
        try{
            cartValidate.inputGetCart(input)

            return cartRepo.getByUserID(input.userID)
        }catch (e: Error) {
            e.printStackTrace()
        }

        return null
    }
}