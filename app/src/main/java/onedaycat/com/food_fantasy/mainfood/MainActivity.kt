package onedaycat.com.food_fantasy.mainfood

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.appbar_header.*
import onedaycat.com.food_fantasy.R
import kotlinx.android.synthetic.main.rs_list_food.*
import onedaycat.com.food_fantasy.common.BaseActivity
import onedaycat.com.foodfantasyservicelib.input.GetProductsInput

class MainActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var foodViewModel: FoodViewModel
    private var foodAdapter: FoodAdapter? = null

    private val limit = 10

    override fun getToolbarInstance(): Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            bindView()
            loadTitleIMG()
            prepareSwipeRefreshLayout()
            firstFetchDataFood(limit)
        }
    }

    private fun bindView() {
        foodViewModel = ViewModelProviders.of(this).get(FoodViewModel::class.java)
        foodViewModel.foodData.observe(this, Observer { data ->

            if (foodAdapter == null) {
                val itemClicked = {foodItem: FoodModel -> foodItemClicked(foodItem)}
                foodAdapter = FoodAdapter(data!!, this, itemClicked)

                rv_food_list.layoutManager = LinearLayoutManager(this)
                rv_food_list.adapter = foodAdapter
            }

            swipe_container.isRefreshing = false
            dismissDialog()
        })
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

    private fun loadTitleIMG(){
        val titleIMG = "https://healthyhappysmart.com/blog/wp-content/uploads/2016/03/clean-eating.png"
        Glide.with(this)
                .load(titleIMG)
                .into(app_bar_image)
    }

    override fun onRefresh() {
        rv_food_list.isEnabled = false
        fetchDataFood(limit)
    }

    private fun foodItemClicked(foodItem: FoodModel) {
        startActivity(foodDetailActivity(foodItem))
    }
}
