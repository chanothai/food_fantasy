package onedaycat.com.food_fantasy.mainfood.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.view.menu.MenuBuilder
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_food_detail_body.*
import kotlinx.android.synthetic.main.activity_food_detail_header.*
import kotlinx.android.synthetic.main.appbar_normal.*
import onedaycat.com.food_fantasy.R
import onedaycat.com.food_fantasy.common.BaseActivity
import onedaycat.com.food_fantasy.mainfood.FoodModel

fun Context.foodDetailActivity(foodModel: FoodModel): Intent {
    return Intent(this, FoodDetailActivity::class.java).apply {
        putExtra(FOOD_NAME, foodModel.foodName)
        putExtra(FOOD_DESC, foodModel.foodDesc)
        putExtra(FOOD_NAME, foodModel.foodName)
        putExtra(FOOD_PRICE, foodModel.foodPrice)
        putExtra(FOOD_IMG, foodModel.foodIMG)
        putExtra(FOOD_STATUS, foodModel.isAddToCart)
    }
}

private const val FOOD_NAME = "food_name"
private const val FOOD_DESC = "food_desc"
private const val FOOD_PRICE = "food_price"
private const val FOOD_IMG = "food_IMG"
private const val FOOD_STATUS = "food_status"


class FoodDetailActivity : BaseActivity() {
    private var foodName: String? = null
    private var foodDesc: String? = null
    private var foodPrice: Int = 0
    private var foodImg: String? = null
    private var foodStatus: Boolean = false

    override fun getToolbarInstance(): Toolbar? = toolbar
    override fun isDisplayHomeEnable(): Boolean? = true
    override fun title(): String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_detail)

        if (savedInstanceState == null) {
            bindBundle()
            setView()
        }
    }

    private fun bindBundle() {
        foodName = intent.getStringExtra(FOOD_NAME)
        foodDesc = intent.getStringExtra(FOOD_DESC)
        foodPrice = intent.getIntExtra(FOOD_PRICE, 0)
        foodImg = intent.getStringExtra(FOOD_IMG)
        foodStatus = intent.getBooleanExtra(FOOD_STATUS, false)

        requireNotNull(foodName) {"No food_name provided in Intent extras"}
        requireNotNull(foodDesc) {"No food_desc provided in intent extras"}
        requireNotNull(foodPrice) {"No food_price provided in intent extras"}
        requireNotNull(foodImg) {"No food_ing provided in intent extras"}
    }

    private fun setView() {
        val price = "$foodPrice ${resources.getString(R.string.currency_dollar)}"
        detail_name.text = foodName
        detail_price.text = price
        detail_desc.text = foodDesc

        Glide.with(this)
                .load(foodImg)
                .into(detail_img)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) {
            finish()
            true
        }else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun MenuItem.getShowAsAction(): Int {
        val f = this.javaClass.getDeclaredField("mShowAsAction")
        f.isAccessible = true
        return f.getInt(this)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            if (menu is MenuBuilder) {
                try {
                    val field = menu.javaClass.getDeclaredField("mOptionalIconsVisible")
                    field.isAccessible = true
                    field.setBoolean(menu, true)
                }catch (ignored: Exception) {
                    ignored.printStackTrace()
                }
            }
        }

        for (item in 0 until menu!!.size()) {
            val menuItem = menu.getItem(item)
            menuItem.icon.setIconColor(
                    if (menuItem.getShowAsAction() == 0) Color.BLUE
                    else Color.GRAY
            )
        }

        return super.onPrepareOptionsMenu(menu)
    }
}
