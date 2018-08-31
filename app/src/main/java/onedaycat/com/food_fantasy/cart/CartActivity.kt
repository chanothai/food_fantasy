package onedaycat.com.food_fantasy.cart

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.appbar_collapsing_toolbar.*
import kotlinx.android.synthetic.main.recyclerview_and_swipe_refresh_layout.*
import onedaycat.com.food_fantasy.R
import onedaycat.com.food_fantasy.common.BaseActivity
import onedaycat.com.food_fantasy.mainfood.CartStore
import onedaycat.com.food_fantasy.mainfood.FoodCartLiveStore

fun Context.cartActivity(): Intent {
    return Intent(this, CartActivity::class.java).apply {  }
}
class CartActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener{

    private lateinit var cartVM: CartViewModel

    override fun getToolbarInstance(): Toolbar? = toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        prepareSwipeRefreshLayout()

        initViewModel()
    }

    private fun initViewModel() {
        cartVM = ViewModelProviders.of(this, viewModelFactory { CartViewModel(FoodCartLiveStore(CartStore.foodCart)) }).get(CartViewModel::class.java)
        cartVM.foodCart.observe(this, Observer { data ->
            for ((i, item) in data?.foodList!!.withIndex()) {
                Toast.makeText(this, item.foodName, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun prepareSwipeRefreshLayout() {
        swipe_container.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.YELLOW, Color.RED)
        swipe_container.setOnRefreshListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) {
            finish()
            true
        }else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onRefresh() {
        swipe_container.isRefreshing = false
    }
}
