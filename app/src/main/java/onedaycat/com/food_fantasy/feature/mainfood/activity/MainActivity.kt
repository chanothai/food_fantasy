package onedaycat.com.food_fantasy.mainfood.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.appbar_header_main_food.*
import kotlinx.android.synthetic.main.appbar_main_food.*
import onedaycat.com.food_fantasy.R
import kotlinx.android.synthetic.main.recyclerview_and_swipe_refresh_layout.*
import onedaycat.com.food_fantasy.feature.cart.CartActivity
import onedaycat.com.food_fantasy.feature.cart.CartModel
import onedaycat.com.food_fantasy.feature.cart.CartViewModel
import onedaycat.com.food_fantasy.common.BaseActivity
import onedaycat.com.food_fantasy.mainfood.*
import onedaycat.com.food_fantasy.store.CartStore
import onedaycat.com.food_fantasy.store.FoodCartLiveStore
import onedaycat.com.food_fantasy.store.FoodCartStore
import onedaycat.com.foodfantasyservicelib.contract.repository.CartFireStore
import onedaycat.com.foodfantasyservicelib.contract.repository.StockFireStore
import onedaycat.com.foodfantasyservicelib.input.AddToCartInput
import onedaycat.com.foodfantasyservicelib.input.GetProductsInput
import onedaycat.com.foodfantasyservicelib.input.RemoveFromCartInput
import onedaycat.com.foodfantasyservicelib.service.CartService
import onedaycat.com.foodfantasyservicelib.service.StockService
import onedaycat.com.foodfantasyservicelib.validate.CartMemoValidate
import onedaycat.com.foodfantasyservicelib.validate.StockMemoValidate

class MainActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private lateinit var foodViewModel: FoodViewModel
    private lateinit var cartViewModel: CartViewModel

    //service
    private val stockService = StockService(StockFireStore(), StockMemoValidate())
    private val cartService = CartService(StockFireStore(),CartFireStore(), CartMemoValidate())

    private var foodAdapter: FoodAdapter? = null

    private val limit = 10
    private val userId = "u1"
    private val beginQTY = 1

    override fun getToolbarInstance(): Toolbar? = toolbar_collapse
    override fun isDisplayHomeEnable(): Boolean? = false
    override fun title(): String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prepareSwipeRefreshLayout()
        fb_shop_to_cart.setOnClickListener(this)
        initViewModel()
        loadTitleIMG()

        if (savedInstanceState == null) {
            firstFetchDataFood(limit)
        }
    }

    private fun initViewModel() {
        CartStore.foodCart = FoodCartStore()
        CartStore.foodCart?.userId = userId

        foodViewModel = ViewModelProviders.of(this,
                viewModelFactory { FoodViewModel(FoodCartLiveStore(CartStore.foodCart)) })
                .get(FoodViewModel::class.java)

        cartViewModel = ViewModelProviders.of(this,
                viewModelFactory { CartViewModel(FoodCartLiveStore(CartStore.foodCart), stockService, cartService) })
                .get(CartViewModel::class.java)

        initCartObserver()
    }

    private fun initCartObserver(){
        cartViewModel.cart.observe(this, Observer { data ->
            if (data != null) {
                if (data.status) {
                    addFoodCart(data)
                }else {
                    removeFoodCart(data)
                }
            }

            invalidateOptionsMenu()
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

    override fun onResume() {
        super.onResume()

        foodViewModel.updateFoodCart()
        foodDataObserver()
        invalidateOptionsMenu()
    }

    private fun foodDataObserver() {
        foodViewModel.foodData.observe(this, Observer { data ->

            if (foodAdapter == null) {
                val itemClicked = { foodItem: FoodModel, view: View? -> foodItemClicked(foodItem, view) }
                foodAdapter = FoodAdapter(data!!, this, itemClicked)

                rv_food_list.layoutManager = LinearLayoutManager(this)
                rv_food_list.hasFixedSize()
                rv_food_list.adapter = foodAdapter
            }else {
                foodAdapter?.notifyDataSetChanged()
            }

            swipe_container.isRefreshing = false
            dismissDialog()
        })
    }

    override fun onRefresh() {
        rv_food_list.isEnabled = false
        fetchDataFood(limit)
    }

    private fun fetchDataFood(limit: Int) {
        val input = GetProductsInput(limit)
        foodViewModel.loadProducts(input)
    }

    private fun foodItemClicked(foodItem: FoodModel, view: View?) {
        if (view?.id == R.id.btn_add) {
            if (foodItem.isAddToCart) {

                showLoadingDialog()
                val input = AddToCartInput(
                        userId,
                        foodItem.foodId,
                        beginQTY
                )

                cartViewModel.addProductToCart(input, mapToCart(foodItem))
            }else {

                showLoadingDialog()
                val input = RemoveFromCartInput(
                        userId,
                        foodItem.foodId,
                        beginQTY
                )

                cartViewModel.removeProductToCart(input, mapToCart(foodItem))
            }
        }

        if (view == null) {
            startActivity(foodDetailActivity(foodItem))
        }
    }

    private fun mapToCart(food: FoodModel): CartModel {
        val cartModel = CartModel()
        cartModel.cartPId = food.foodId
        cartModel.cartName = food.foodName
        cartModel.cartPrice = food.foodPrice
        cartModel.cartQTY = 1
        cartModel.cartTotalPrice = food.foodPrice
        cartModel.cartImg = food.foodIMG
        cartModel.status = food.isAddToCart

        return cartModel
    }

    private fun addFoodCart(data: CartModel) {
        CartStore.foodCart?.cartList?.add(data)
    }

    private fun removeFoodCart(data: CartModel) {
        for ((i, food) in CartStore.foodCart?.cartList!!.withIndex()) {
            if (food.cartPId == data.cartPId) {
                CartStore.foodCart?.cartList?.removeAt(i)
                return
            }
        }
    }

    override fun onClick(view: View?) {
        if (view?.id == R.id.fb_shop_to_cart) {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_food_menu, menu)
        val menuItem = menu?.findItem(R.id.show_badge_food)
        menuItem?.icon = convertLayoutToImage(CartStore.counter, R.drawable.ic_shopping_cart)

        return true
    }

    override fun onBackPressed() {

    }
}
