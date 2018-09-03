package onedaycat.com.food_fantasy.feature.cart

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.cart_detail_item.view.*
import onedaycat.com.food_fantasy.R

class CartAdapter(
        private val items: ArrayList<CartModel>,
        private val context: Context,
        private val actionCartListener: OnActionCartListener): RecyclerView.Adapter<CartHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.cart_detail_item, parent, false)

        return CartHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CartHolder, position: Int) {
        val priceStr = "${items[position].cartPrice} ฿"

        holder.cartName.text = items[position].cartName
        holder.cartPrice.text = priceStr

        Glide.with(context)
                .load(items[position].cartImg)
                .into(holder.cartImg)

        holder.cartQTY.setText(items[position].cartQTY.toString())
        holder.cartLimitQTY.text = items[position].cartQTYLimit.toString()

        holder.btnClickListenter(items[position], actionCartListener)
        holder.sumTotalPrice(items[position],items, actionCartListener)
        holder.actionDone(items[position], actionCartListener)
    }

    fun removeItem(cartItem: CartModel) {
        items.remove(cartItem)
        notifyDataSetChanged()
    }

    fun sumTotalPrice(): String {
        var result = 0
        for (cart in items) {
            result += cart.cartPrice * cart.cartQTY
        }

        return "$result ฿"
    }
}

class CartHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    var cartImg = itemView.cart_img_item!!
    var cartName = itemView.cart_name_item!!
    var cartQTY = itemView.cart_qty_item!!
    var cartPrice = itemView.cart_price_item!!
    var cartLimitQTY = itemView.cart_limit_qty
    var btnDelete = itemView.btn_delete_item!!

    fun btnClickListenter(cartItem: CartModel, actionCartListener: OnActionCartListener) {
        btnDelete.setOnClickListener {
            actionCartListener.onRemoveCart(cartItem)
        }
    }

    fun actionDone(cartItem: CartModel, actionCartListener: OnActionCartListener) {
        cartQTY.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                actionCartListener.onActionIME(cartItem)

                true
            }else {
                false
            }
        }
    }

    fun sumTotalPrice(cartItem: CartModel, cartItems: ArrayList<CartModel>, actionCartListener: OnActionCartListener) {
        cartQTY.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (char.toString().isNotEmpty()) {
                    val qty: Int = char.toString().toInt()

                    val totalPrice = qty * cartItem.cartPrice

                    cartItem.cartQTY = qty
                    cartItem.cartTotalPrice = totalPrice

                    val strPrice = totalPrice.toString() + "฿"
                    cartPrice.text = strPrice

                    var allTotalPrice = 0
                    for (cart in cartItems) {
                        allTotalPrice += cart.cartTotalPrice
                    }

                    actionCartListener.onTextWatcherTotalPrice(allTotalPrice)
                }
            }
        })
    }
}