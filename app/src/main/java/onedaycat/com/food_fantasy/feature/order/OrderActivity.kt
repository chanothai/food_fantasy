package onedaycat.com.food_fantasy.feature.order

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.appbar_collapsing_toolbar.*
import kotlinx.android.synthetic.main.recyclerview_layout.*
import onedaycat.com.food_fantasy.R
import onedaycat.com.food_fantasy.common.BaseActivity
import onedaycat.com.foodfantasyservicelib.input.GetOrderInput
import onedaycat.com.foodfantasyservicelib.service.EcomService

fun Context.OrderActivity(userID: String): Intent{
    return Intent(this, OrderActivity::class.java).apply {
        putExtra(USERID, userID)
    }
}

private const val USERID = "user_id"

class OrderActivity : BaseActivity() {
    private lateinit var orderViewModel: OrderViewModel
    private var userId: String? = null

    private var orderAdapter:OrderAdapter? = null

    override fun isDisplayHomeEnable(): Boolean? = true
    override fun getToolbarInstance(): Toolbar? = toolbar
    override fun title(): String? = getString(R.string.title_order)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        initViewMode()

        if (savedInstanceState == null) {
            showOrderHistory()
        }
    }

    private fun showOrderHistory() {
        userId = intent.getStringExtra(USERID)
        val input = GetOrderInput(
                userId!!
        )

        orderViewModel.loadOrderHistory(input)
    }

    private fun initViewMode() {
        orderViewModel = ViewModelProviders.of(this,
                viewModelFactory { OrderViewModel(EcomService) }).get(OrderViewModel::class.java)

        orderObserver()
    }

    private fun orderObserver() {
        orderViewModel.orders.observe(this, Observer { orders ->
            if (orders?.size!! > 0) {

                if (orderAdapter == null) {
                    orderAdapter = OrderAdapter(this, orders)

                    recyclerView.layoutManager = LinearLayoutManager(this)
                    recyclerView.adapter = orderAdapter

                    return@Observer
                }

                orderAdapter?.notifyDataSetChanged()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) {
            finish()
            true
        }else {
            super.onOptionsItemSelected(item)
        }
    }
}
