package onedaycat.com.food_fantasy.ui.order.fragment


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.layout_empty_state.*
import kotlinx.android.synthetic.main.recyclerview_and_swipe_refresh_layout.*
import kotlinx.android.synthetic.main.recyclerview_and_swipe_refresh_layout.view.*

import onedaycat.com.food_fantasy.R
import onedaycat.com.food_fantasy.mainfood.activity.MainActivity
import onedaycat.com.food_fantasy.store.CartStore
import onedaycat.com.food_fantasy.ui.order.*
import onedaycat.com.food_fantasy.util.IdlingResourceHelper
import onedaycat.com.foodfantasyservicelib.input.GetOrderInput
import onedaycat.com.foodfantasyservicelib.service.EcomService

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OrderFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class OrderFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, OrderItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var mainActivity: MainActivity
    private lateinit var orderViewModel: OrderViewModel

    private var orderAdapter: OrderAdapter? = null

    var viewModelFactory: ViewModelProvider.Factory? = null

    //View
    private var recyclerView: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order, container, false)
        recyclerView = view.rv_with_refresh
        return view
    }


    companion object {
        @JvmStatic
        fun newInstance() = OrderFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainActivity = (activity as MainActivity)

        prepareSwipeRefreshLayout()
        initViewMode()

        if (savedInstanceState == null) {
            showOrderHistory()
        }
    }

    private fun prepareSwipeRefreshLayout() {
        swipe_container.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.YELLOW, Color.RED)
        swipe_container.setOnRefreshListener(this)
    }

    private fun initLayout() {
        layout_empty_state.visibility = View.VISIBLE
        image_empty_state.setImageResource(R.drawable.ic_history)
        msg_empty_state.text = getString(R.string.msg_order_empty_state)

        recyclerView?.visibility = View.GONE
    }

    private fun showOrderHistory() {
        CartStore.foodCart?.let {
            val input = GetOrderInput(
                    CartStore.foodCart?.userId!!
            )

            orderViewModel.loadOrderHistory(input)
        }?.run {
            //The IdlingResource is null in production.
            IdlingResourceHelper.mIdlingResource?.let {
                it.setIdleState(false)
                it
            }
        }
    }

    private fun initViewMode() {
        viewModelFactory?.let {
            orderViewModel = ViewModelProviders.of(this, it)
                    .get(OrderViewModel::class.java)

            orderObserver()

            return
        }


        orderViewModel = ViewModelProviders.of(this,
                mainActivity.viewModelFactory { OrderViewModel(EcomService()) }).get(OrderViewModel::class.java)

        orderObserver()
    }

    private fun orderObserver() {
        orderViewModel.orders.observe(this, Observer { order ->
            order?.orderModels?.let {orders->
                swipe_container.isRefreshing = false

                IdlingResourceHelper.mIdlingResource?.let { idle->
                    idle.setIdleState(true)
                    idle
                }

                if (orders.size > 0) {
                    orderAdapter?.let {adapter->
                        adapter.notifyDataSetChanged()
                        return@Observer
                    }

                    orderAdapter = OrderAdapter(this.context!!, orders, this)

                    recyclerView?.layoutManager = LinearLayoutManager(this.context)
                    recyclerView?.adapter = orderAdapter
                    return@Observer
                }

                initLayout()
            }

        })
    }

    override fun onRefresh() {
        showOrderHistory()
    }

    override fun onClicked(orderModel: OrderModel) {
        startActivity(activity?.OrderDetailActivity(orderModel))
    }
}
