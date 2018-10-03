package onedaycat.com.food_fantasy.ui.cart.fragment


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.android.synthetic.main.recyclerview_layout.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

import onedaycat.com.food_fantasy.R
import onedaycat.com.food_fantasy.ui.cart.CartAdapter
import onedaycat.com.food_fantasy.ui.cart.CartModel
import onedaycat.com.food_fantasy.ui.cart.OnActionCartListener
import onedaycat.com.food_fantasy.mainfood.FoodViewModel
import onedaycat.com.food_fantasy.mainfood.activity.MainActivity
import onedaycat.com.food_fantasy.store.CartStore
import onedaycat.com.foodfantasyservicelib.contract.creditcard_payment.CreditCard
import onedaycat.com.foodfantasyservicelib.entity.Cart
import onedaycat.com.foodfantasyservicelib.entity.ProductQTY
import onedaycat.com.foodfantasyservicelib.input.AddCartsToCartInput
import onedaycat.com.foodfantasyservicelib.input.ChargeInput
import onedaycat.com.foodfantasyservicelib.input.CreditCardType

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CartFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class CartFragment : Fragment(), OnActionCartListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var foodViewModel: FoodViewModel
    private lateinit var mainActivity: MainActivity
    private lateinit var cartsModel: ArrayList<CartModel>
    private lateinit var cart: Cart

    private var creditCard: CreditCard? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = CartFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainActivity = (activity as MainActivity)

        initViewModel()
        confirmOrder()

        if (savedInstanceState == null) {
            foodViewModel.cartSumTotalPrice()
        }
    }

    private fun initViewModel() {
        mainActivity.foodViewModel?.let {
            foodViewModel = it
            totalPriceObserver()
            cartStoreObserver()
            paymentObserver()
//            errorObserver()
            cartLiveDataObserver()
        }
    }

    private fun cartLiveDataObserver() {
        foodViewModel.cartLiveData.observe(this, Observer {cart->
            cart?.let {
                creditCard?.let {credit->
                    val input = ChargeInput(
                            userID = CartStore.foodCart?.userId!!,
                            cart = it,
                            creditCard = credit
                    )

                    input
                }?.also {input->
                    launch(UI) {
                        foodViewModel.payment(input)
                    }
                }
                
                foodViewModel.createErrorMessage("Please put your credit card")

            }
        })
    }

    private fun totalPriceObserver() {
        foodViewModel.totalPrice.observe(this, Observer { total ->
            total?.let {
                val priceFormat = "${getString(R.string.currency_dollar)}$it"
                cart_total_price.text = priceFormat
            }
        })
    }

    private fun cartStoreObserver() {
        foodViewModel.cartStore.observe(this, Observer {cartStore->
            cartStore?.let {
                it.foodCart?.cartList?.let {carts->
                    this.cartsModel = carts

                    if (carts.size == 0) {
                        layout_card_bottom.visibility = View.GONE
                        recyclerView.visibility = View.GONE
                        container_empty_state.visibility = View.VISIBLE
                        return@let carts
                    }

                    cartModelMapToCartEntity(carts, it)
                    carts
                }?.also {carts->
                    CartAdapter(carts, this.context!!, this).let {
                        recyclerView?.layoutManager = LinearLayoutManager(this.context)
                        recyclerView?.hasFixedSize()

                        recyclerView?.adapter = it
                    }
                }

                mainActivity.createBadgeCart(it.counter)
            }
        })
    }

    private fun cartModelMapToCartEntity(carts: ArrayList<CartModel>, it: CartStore) {
        val pQTYs = arrayListOf<ProductQTY>()
        for (value in carts) {
            pQTYs.add(ProductQTY().apply {
                this.productId = value.cartPId
                this.productName = value.cartName!!
                this.price = value.cartPrice
                this.qty = value.cartQTY
            })
        }

        cart = Cart().apply {
            this.userId = it.foodCart?.userId
            this.products = pQTYs
        }
    }

    private fun errorObserver() {
        foodViewModel.msgError.observe(this, Observer {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        })
    }

    private fun paymentObserver() {
        foodViewModel.pay.observe(this, Observer {
            it?.let { order ->
                mainActivity.dismissDialog()
                foodViewModel.deleteCart()

                mainActivity.createBadgeCart(0)
                mainActivity.bottomBar.currentItem = 2

                order
            }

            mainActivity.dismissDialog()
        })
    }

    private fun confirmOrder() {
        btn_confirm_order.setOnClickListener {
            mainActivity.showLoadingDialog()

            val input = AddCartsToCartInput(
                    cart
            )

            launch(UI) { foodViewModel.addAllProductCart(input) }
        }
    }

    override fun onAddCart(cartModel: CartModel) {
        foodViewModel.updateCartItem(cartModel)
    }

    override fun onRemoveCart(cartModel: CartModel) {
        foodViewModel.updateCartItem(cartModel)
    }

    override fun onTextChange(text: String, position:Int) {
        creditCard = foodViewModel.createCreditCart(text, position)
    }

    override fun onDestroy() {
        super.onDestroy()
        foodViewModel.deleteOrder()
    }
}
