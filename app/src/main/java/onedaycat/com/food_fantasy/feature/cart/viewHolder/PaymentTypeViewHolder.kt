package onedaycat.com.food_fantasy.feature.cart.viewHolder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.EditText
import android.widget.TextView
import onedaycat.com.food_fantasy.R

class PaymentTypeViewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {
    var payTopicName = itemView.findViewById<TextView>(R.id.pay_item_topic)
    var payEditData = itemView.findViewById<EditText>(R.id.pay_item_edit)
}
