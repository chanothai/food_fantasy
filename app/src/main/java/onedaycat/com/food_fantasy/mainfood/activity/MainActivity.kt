package onedaycat.com.food_fantasy.mainfood.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.appbar_header_main_food.*
import kotlinx.android.synthetic.main.appbar_main_food.*
import onedaycat.com.food_fantasy.R
import kotlinx.android.synthetic.main.recyclerview_and_swipe_refresh_layout.*
import onedaycat.com.food_fantasy.cart.cartActivity
import onedaycat.com.food_fantasy.common.BaseActivity
import onedaycat.com.food_fantasy.mainfood.*
import onedaycat.com.foodfantasyservicelib.input.GetProductsInput

class MainActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private lateinit var foodViewModel: FoodViewModel
    private var foodAdapter: FoodAdapter? = null
    private var foodCart: FoodCartStore? = null

    private val limit = 10

    override fun getToolbarInstance(): Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fb_shop_to_cart.setOnClickListener(this)
        initViewModel()
        loadTitleIMG()

        if (savedInstanceState == null) {
            prepareSwipeRefreshLayout()
            firstFetchDataFood(limit)

            foodCart = FoodCartStore()
            foodCart?.userId = "u1"
        }
    }

    private fun initViewModel() {
        foodViewModel = ViewModelProviders.of(this).get(FoodViewModel::class.java)
        foodViewModel.foodData.observe(this, Observer { data ->

            if (foodAdapter == null) {
                val itemClicked = { foodItem: FoodModel -> foodItemClicked(foodItem) }
                foodAdapter = FoodAdapter(data!!, this, itemClicked)

                rv_food_list.layoutManager = LinearLayoutManager(this)
                rv_food_list.adapter = foodAdapter
            }

            swipe_container.isRefreshing = false
            dismissDialog()
        })
    }

    private fun addFoodCart(data: FoodModel) {
        foodCart?.foodList?.add(data)
        CartStore.foodCart = foodCart!!
    }

    private fun prepareSwipeRefreshLayout() {
        swipe_container.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.YELLOW, Color.RED)
        swipe_container.setOnRefreshListener(this)
    }

    private fun firstFetchDataFood(limit: Int) {
        showLoadingDialog()
        val input = GetProductsInput(limit)
        foodViewModel.loadProducts(input)
    }

    private fun fetchDataFood(limit: Int) {
        val input = GetProductsInput(limit)
        foodViewModel.loadProducts(input)
    }

    private fun loadTitleIMG() {
        val titleIMG = "https://healthyhappysmart.com/blog/wp-content/uploads/2016/03/clean-eating.png"

        val option = RequestOptions().apply {
            this.placeholder(R.mipmap.ic_launcher)
        }

        Glide.with(this)
                .load(titleIMG)
                .apply(option)
                .into(app_bar_image)
    }

    override fun onRefresh() {
        rv_food_list.isEnabled = false
        fetchDataFood(limit)
    }

    private fun foodItemClicked(foodItem: FoodModel) {
        addFoodCart(foodItem)
        startActivity(foodDetailActivity(foodItem))
    }

    override fun onClick(view: View?) {
        if (view?.id == R.id.fb_shop_to_cart) {
            startActivity(cartActivity())
        }
    }
}
