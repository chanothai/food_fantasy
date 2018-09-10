package onedaycat.com.food_fantasy.mainfood.activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter
import com.aurelhubert.ahbottomnavigation.notification.AHNotification
import kotlinx.android.synthetic.main.appbar_normal.*
import onedaycat.com.food_fantasy.R
import onedaycat.com.food_fantasy.common.BaseActivity
import onedaycat.com.food_fantasy.feature.cart.fragment.CartFragment
import onedaycat.com.food_fantasy.feature.mainfood.fragment.MainMenuFragment
import onedaycat.com.food_fantasy.mainfood.FoodViewModel
import onedaycat.com.food_fantasy.store.CartStore
import onedaycat.com.food_fantasy.store.FoodCartLiveStore
import onedaycat.com.food_fantasy.store.FoodCartStore
import onedaycat.com.foodfantasyservicelib.service.EcomService

class MainActivity : BaseActivity(), AHBottomNavigation.OnTabSelectedListener {

    lateinit var bottomBar: AHBottomNavigation
    override fun getToolbarInstance(): Toolbar? = toolbar
    override fun isDisplayHomeEnable(): Boolean? = false
    override fun title(): String? = null

    lateinit var foodViewModel: FoodViewModel
    private val userID = "u1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViewModel()
        createBottomBar()
        if (savedInstanceState == null) {
            openFragment(
                    MainMenuFragment.newInstance(),
                    "MainMenuFragment")
        }
    }

    private fun initViewModel() {
        with(CartStore) {
            this.foodCart?.let {
                foodViewModel = ViewModelProviders.of(this@MainActivity,
                        viewModelFactory { FoodViewModel(FoodCartLiveStore(this), EcomService) })
                        .get(FoodViewModel::class.java)

                return
            }

            this.foodCart = FoodCartStore().apply {
                this.userId = userID
            }
        }

        with(CartStore) {
            foodViewModel = ViewModelProviders.of(this@MainActivity,
                    viewModelFactory { FoodViewModel(FoodCartLiveStore(this), EcomService) })
                    .get(FoodViewModel::class.java)
        }
    }

    private fun createBottomBar() {
        bottomBar = findViewById(R.id.bottom_bar)

        val tabColors = resources.getIntArray(R.array.color_bottom_navigation_selected)
        val adapter = AHBottomNavigationAdapter(this, R.menu.menu_navigation)
        adapter.setupWithBottomNavigation(bottomBar, tabColors)
        bottomBar.accentColor = ContextCompat.getColor(this, R.color.colorPrimary)

        bottomBar.setOnTabSelectedListener(this)
    }

    private fun createBadgeCart(count: Int) {
        if (count == 0) return

        AHNotification.Builder().let { builder ->
            builder.setText(count.toString())
                    .setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .setTextColor(ContextCompat.getColor(this, R.color.color_bg_white))

            builder.build()
        }.also { notification ->

            bottomBar.setNotification(notification, 1)
        }
    }


    private fun openFragment(fragment: Fragment, tag: String) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, fragment, tag)
                .commit()
    }

    override fun onTabSelected(position: Int, wasSelected: Boolean): Boolean {
        return when (position) {
            0 -> {
                openFragment(
                        MainMenuFragment.newInstance(),
                        "MainMenuFragment")

                updateTitleToolbar(getString(R.string.title_menu_th))
                true
            }

            1 -> {
                openFragment(CartFragment.newInstance(), "CartFragment")
                updateTitleToolbar(getString(R.string.title_cart_th))
                true
            }

            2 -> {
                true
            }

            else -> false
        }
    }

    override fun onBackPressed() {

    }

    override fun onResume() {
        super.onResume()
        with(CartStore) {
            createBadgeCart(counter)
        }
    }

}
