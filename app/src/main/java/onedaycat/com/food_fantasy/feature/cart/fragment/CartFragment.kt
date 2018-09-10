package onedaycat.com.food_fantasy.feature.cart.fragment


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.android.synthetic.main.layout_empty_state.*
import kotlinx.android.synthetic.main.recyclerview_layout.*

import onedaycat.com.food_fantasy.R
import onedaycat.com.food_fantasy.feature.cart.CartAdapter
import onedaycat.com.food_fantasy.feature.cart.CartModel
import onedaycat.com.food_fantasy.feature.cart.OnActionCartListener
import onedaycat.com.food_fantasy.mainfood.FoodViewModel
import onedaycat.com.food_fantasy.mainfood.activity.MainActivity
import onedaycat.com.food_fantasy.store.CartStore
import onedaycat.com.foodfantasyservicelib.contract.creditcard_payment.CreditCard
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

        initViewModel()
        chooseMenuClicked()
        initLayout()

        if (savedInstanceState == null) {
            foodViewModel.cartSumTotalPrice()
        }
    }

    private fun initLayout() {
        CartStore.foodCart?.cartList?.let { carts ->
            if (carts.size == 0) {
                layout_card_bottom.visibility = View.GONE
                recyclerView.visibility = View.GONE
                container_empty_state.visibility = View.VISIBLE
                return
            }

            carts
        }?.also { carts ->
            CartAdapter(carts, this.context!!, this).let {
                recyclerView?.layoutManager = LinearLayoutManager(this.context)
                recyclerView?.hasFixedSize()

                recyclerView?.adapter = it
            }
        }
    }

    private fun initViewModel() {
        (activity as MainActivity).foodViewModel.let {
            foodViewModel = it
            totalPriceObserver()
            paymentObserver()
        }
    }

    private fun totalPriceObserver() {
        foodViewModel.totalPrice.observe(this, Observer { total ->
            total?.let {
                val priceFormat = "${getString(R.string.currency_dollar)}$it"
                cart_total_price.text = priceFormat
            }
        })
    }

    private fun paymentObserver() {
        foodViewModel.pay.observe(this, Observer {order->
            order?.let {
                (activity as MainActivity).dismissDialog()
                foodViewModel.deleteCart()
                initLayout()
            }
        })
    }

    private fun chooseMenuClicked() {
        btn_confirm_order.setOnClickListener {

            (activity as MainActivity).showLoadingDialog()
            val creditCard = CreditCard(
                    CreditCardType.CreditCardMasterCard,"123123","1321312","123123",
                    "123123","123123"
            )
            val input = ChargeInput(
                    userID = CartStore.foodCart?.userId!!,
                    creditCard = creditCard
            )

            foodViewModel.payment(input)
        }
    }

    override fun onAddCart(cartModel: CartModel) {
        foodViewModel.updateCartItem(cartModel)
    }

    override fun onRemoveCart(cartModel: CartModel) {
        foodViewModel.updateCartItem(cartModel)
    }
}
