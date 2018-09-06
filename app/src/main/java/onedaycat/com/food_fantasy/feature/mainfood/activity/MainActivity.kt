package onedaycat.com.food_fantasy.mainfood.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.appbar_header_main_food.*
import kotlinx.android.synthetic.main.appbar_main_food.*
import onedaycat.com.food_fantasy.R
import kotlinx.android.synthetic.main.recyclerview_and_swipe_refresh_layout.*
import onedaycat.com.food_fantasy.feature.cart.CartActivity
import onedaycat.com.food_fantasy.feature.cart.CartModel
import onedaycat.com.food_fantasy.common.BaseActivity
import onedaycat.com.food_fantasy.feature.mainfood.fragment.MainMenuFragment
import onedaycat.com.food_fantasy.feature.order.OrderActivity
import onedaycat.com.food_fantasy.mainfood.*
import onedaycat.com.food_fantasy.store.CartStore
import onedaycat.com.food_fantasy.store.FoodCartLiveStore
import onedaycat.com.food_fantasy.store.FoodCartStore
import onedaycat.com.foodfantasyservicelib.input.AddToCartInput
import onedaycat.com.foodfantasyservicelib.input.GetProductsInput
import onedaycat.com.foodfantasyservicelib.input.RemoveFromCartInput
import onedaycat.com.foodfantasyservicelib.service.EcomService

class MainActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener{

    override fun getToolbarInstance(): Toolbar? = toolbar_collapse
    override fun isDisplayHomeEnable(): Boolean? = false
    override fun title(): String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_bar.setOnNavigationItemSelectedListener(this)
        if (savedInstanceState == null) {
            openFragment(
                    MainMenuFragment.newInstance(),
                    "MainMenuFragment")
        }
    }

    private fun openFragment(fragment: Fragment, tag: String) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, fragment, tag)
                .addToBackStack(null)
                .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.navigation_menu -> {
                openFragment(
                        MainMenuFragment.newInstance(),
                        "MainMenuFragment")
                true
            }

            R.id.navigation_cart -> {
                true
            }

            R.id.navigation_order -> {
                true
            }

            else -> false
        }
    }

    override fun onBackPressed() {

    }

    public fun get() {

    }
}
