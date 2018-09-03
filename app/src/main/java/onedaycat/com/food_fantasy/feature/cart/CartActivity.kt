package onedaycat.com.food_fantasy.feature.cart

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.LinearLayout.VERTICAL
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.appbar_collapsing_toolbar.*
import kotlinx.android.synthetic.main.recyclerview_layout.*
import onedaycat.com.food_fantasy.R
import onedaycat.com.food_fantasy.common.BaseActivity
import onedaycat.com.food_fantasy.store.CartStore
import onedaycat.com.food_fantasy.store.FoodCartLiveStore
import onedaycat.com.foodfantasyservicelib.contract.repository.CartFireStore
import onedaycat.com.foodfantasyservicelib.contract.repository.StockFireStore
import onedaycat.com.foodfantasyservicelib.input.AddToCartInput
import onedaycat.com.foodfantasyservicelib.input.RemoveFromCartInput
import onedaycat.com.foodfantasyservicelib.service.CartService
import onedaycat.com.foodfantasyservicelib.service.StockService
import onedaycat.com.foodfantasyservicelib.validate.CartMemoValidate
import onedaycat.com.foodfantasyservicelib.validate.StockMemoValidate

class CartActivity : BaseActivity(), OnActionCartListener{

    private lateinit var cartVM: CartViewModel
    private var cartAdapter: CartAdapter? = null

    private val stockService = StockService(StockFireStore(), StockMemoValidate())
    private val cartService = CartService(StockFireStore(),CartFireStore(), CartMemoValidate())

    override fun getToolbarInstance(): Toolbar? = toolbar
    override fun isDisplayHomeEnable(): Boolean? = true
    override fun title(): String? = resources.getString(R.string.title_cart)

    private var userId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        initViewModel()

        if (savedInstanceState == null) {
            showLoadingDialog()
            cartVM.mapCart()
        }
    }

    private fun initViewModel() {
        cartVM = ViewModelProviders.of(this,
                viewModelFactory { CartViewModel(FoodCartLiveStore(CartStore.foodCart),
                        stockService,
                        cartService)
        }).get(CartViewModel::class.java)

        initDataFoodObserver()
        initCartObserver()
    }

    private fun initCartObserver() {
        cartVM.cart.observe(this, Observer { data ->
            if (data != null) {

                if (!data.status) {
                    cartAdapter?.removeItem(data)
                }

                cart_total_price.text = cartAdapter?.sumTotalPrice()
            }

            dismissDialog()
        })
    }

    private fun initDataFoodObserver() {
        cartVM.foodCart.observe(this, Observer { data ->
            if (data?.cartList != null) {

                if (cartAdapter == null) {
                    cartAdapter = CartAdapter(data.cartList!!, this, this)

                    recyclerView.layoutManager = LinearLayoutManager(this)
                    recyclerView.hasFixedSize()

                    val divider = DividerItemDecoration(this, VERTICAL)
                    recyclerView.addItemDecoration(divider)
                    recyclerView.adapter = cartAdapter


                    cart_total_price.text = cartAdapter?.sumTotalPrice()

                    userId = data.userId
                }
            }

            dismissDialog()
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

    override fun onRemoveCart(cartModel: CartModel) {
        val input = RemoveFromCartInput(
                userId!!,
                cartModel.cartPId!!,
                cartModel.cartQTY
        )

        showLoadingDialog()
        cartVM.removeProductToCart(input, cartModel)
    }

    override fun onTextWatcherTotalPrice(totalPrice: Int) {
        val result = "$totalPrice ฿"
        cart_total_price.text = result
    }

    override fun onActionIME(cartModel: CartModel) {
        showLoadingDialog()
        val input = AddToCartInput(
                userId!!,
                cartModel.cartPId!!,
                cartModel.cartQTY
        )

        cartVM.addProductToCart(input, cartModel)
    }
}
