package onedaycat.com.food_fantasy.mainfood

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_food_detail_header.view.*
import kotlinx.android.synthetic.main.food_detail_information_item.view.*
import kotlinx.android.synthetic.main.food_detail_item.view.*
import kotlinx.android.synthetic.main.food_img_item.view.*
import onedaycat.com.food_fantasy.R

class FoodAdapter(
        private val items: FoodListModel,
        private val context: Context,
        private val addRemoveCallback: AddRemoveCallback): RecyclerView.Adapter<FoodViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.food_list_item, parent, false)

        return FoodViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (items.foodList.size == 0) return 0
        return items.foodList.size
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val longString:String = items.foodList[position].foodDesc
        val newDesc = "${longString.substring(0, 30)}..."

        holder.descFood.text = newDesc
        holder.nameFood.text = items.foodList[position].foodName
        holder.price.text = items.foodList[position].foodPrice.toString()

        holder.setHolderClicked(items.foodList[position],addRemoveCallback)

        holder.btnAddCart(items.foodList[position], addRemoveCallback)

        Glide.with(context)
                .load(items.foodList[position].foodIMG)
                .into(holder.foodImg)

        if (!items.foodList[position].isAddToCart) {
            holder.btnAdd.setBackgroundResource(R.drawable.selector_btn_add_cart)
        }else {
            holder.btnAdd.setBackgroundResource(R.drawable.selector_btn_remove_cart)
        }

    }
}

class FoodViewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {
    var foodImg = itemView.food_img_item!!
    var nameFood = itemView.txt_item_name!!
    var descFood = itemView.txt_item_describe!!
    var price = itemView.txt_item_price!!
    var btnAdd = itemView.btn_add!!

    fun setHolderClicked(foodModel: FoodModel, addRemoveCallback: AddRemoveCallback) {
        itemView.setOnClickListener {
            addRemoveCallback.goCart(foodModel)
        }
    }

    fun btnAddCart(foodModel: FoodModel, addRemoveCallback: AddRemoveCallback) {
        btnAdd.setOnClickListener {
            if (!foodModel.isAddToCart) {
                foodModel.isAddToCart = true
                btnAdd.setBackgroundResource(R.drawable.selector_btn_remove_cart)

                addRemoveCallback.addItem(foodModel)
            }else {
                foodModel.isAddToCart = false
                btnAdd.setBackgroundResource(R.drawable.selector_btn_add_cart)

                addRemoveCallback.removeItem(foodModel)
            }
        }
    }
}