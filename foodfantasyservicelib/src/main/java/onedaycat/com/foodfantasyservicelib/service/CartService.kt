package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.entity.*
import onedaycat.com.foodfantasyservicelib.contract.repository.CartRepo
import onedaycat.com.foodfantasyservicelib.contract.repository.StockRepo
import onedaycat.com.foodfantasyservicelib.error.NotFoundException
import onedaycat.com.foodfantasyservicelib.input.AddToCartInput
import onedaycat.com.foodfantasyservicelib.input.GetCartInput
import onedaycat.com.foodfantasyservicelib.input.RemoveFromCartInput
import onedaycat.com.foodfantasyservicelib.validate.CartValidate

class CartService(private val stockRepo: StockRepo,
                  private val cartRepo: CartRepo,
                  private val cartValidate: CartValidate) {

    fun addProductCart(input: AddToCartInput): Cart? {
        var cart: Cart?

        try {
            cartValidate.inputCart(input)

            //get cart if not found cart will create new cart
            cart = cartRepo.getByUserID(input.userID)
            cart!!.userId = input.userID

        }catch (e:NotFoundException) {
            cart = Cart(
                    input.userID,
                    mutableListOf()
            )
        }

        //get stock
        val pStock = stockRepo.getWithPrice(input.productID)!!

        //create new productQTY
        val newProductQTY = newProductQTY(pStock.productStock!!.productID, pStock.price, input.qty)

        //add product qty to cart
        cart!!.addPQTY(newProductQTY, pStock.productStock!!)

        //Save or Update Cart
        cartRepo.upsert(cart)

        return cart
    }

    fun removeFromeCart(input: RemoveFromCartInput): Cart? {
        cartValidate.inputRemoveCart(input)

        val cart = cartRepo.getByUserID(input.userID)

        val pstock = stockRepo.getWithPrice(input.productID)

        cart!!.remove(newProductQTY(pstock?.productStock!!.productID, pstock.price, input.qty), pstock.productStock!!)

        cartRepo.upsert(cart)

        return cart
    }

    fun getCartWithUserID(input: GetCartInput): Cart? {
        cartValidate.inputGetCart(input)

        return cartRepo.getByUserID(input.userID)
    }
}