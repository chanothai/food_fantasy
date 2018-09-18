package onedaycat.com.food_fantasy.ui.cart.viewHolder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.TextView
import onedaycat.com.food_fantasy.R
import onedaycat.com.food_fantasy.ui.cart.CartModel
import onedaycat.com.food_fantasy.ui.cart.OnActionCartListener

class CartTypeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    var cartName = itemView.findViewById<TextView>(R.id.cart_item_name)
    var cartQTY = itemView.findViewById<TextView>(R.id.cart_item_qty)
    var btnAddQTY = itemView.findViewById<Button>(R.id.btn_add_qty)
    var btnRemoveQTY = itemView.findViewById<Button>(R.id.btn_remove_qty)

    fun setOnItemClicked(cartModel: CartModel, onActionCartListener: OnActionCartListener) {
        btnAddQTY.setOnClickListener {view->

            cartModel.let {
                it.isCart = true
                it.cartQTY += 1
                cartQTY.text = "${cartModel.cartQTY}"

                it
            }.also {
                onActionCartListener.onAddCart(it)
            }
        }

        btnRemoveQTY.setOnClickListener { view ->
            cartModel.let {
                it.isCart = false
                if (it.cartQTY == 1) {
                    return@setOnClickListener
                }

                it.cartQTY -= 1
                cartQTY.text = "${it.cartQTY}"

                it
            }

            with(cartModel) {
                onActionCartListener.onRemoveCart(this)
            }
        }
    }
}