package onedaycat.com.food_fantasy.feature.order.viewHolder

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.layout_item_order_id_type.view.*

class HolderOrderIdType(itemView: View): RecyclerView.ViewHolder(itemView) {
    var orderId = itemView.order_item_id
}