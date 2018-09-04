package onedaycat.com.food_fantasy.feature.order

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.order_detail_item.view.*
import onedaycat.com.food_fantasy.R
import onedaycat.com.foodfantasyservicelib.entity.Order

class OrderAdapter(
        val context:Context,
        private val orders: ArrayList<Order>): RecyclerView.Adapter<OrderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.order_detail_item, parent, false)

        return OrderViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val orderIdStr = "Order#${orders[position].id}"
        holder.orderId.text = orderIdStr
        holder.orderDate.text = orders[position].createDate

        val totalPriceStr = "${orders[position].totalPrice} ฿"
        holder.orderTotalPrice.text = totalPriceStr

        holder.orderStatus.text = orders[position].status.toString()
    }
}

class OrderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    var orderId = itemView.order_item_id!!
    var orderDate = itemView.order_item_date!!
    var orderTotalPrice = itemView.order_total_price!!
    var orderStatus = itemView.order_item_status!!


}