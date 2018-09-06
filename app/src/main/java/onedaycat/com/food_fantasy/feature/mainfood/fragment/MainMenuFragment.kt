package onedaycat.com.food_fantasy.feature.mainfood.fragment


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
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
import kotlinx.android.synthetic.main.recyclerview_and_swipe_refresh_layout.*

import onedaycat.com.food_fantasy.R
import onedaycat.com.food_fantasy.mainfood.ItemClickedCallback
import onedaycat.com.food_fantasy.mainfood.FoodAdapter
import onedaycat.com.food_fantasy.mainfood.FoodModel
import onedaycat.com.food_fantasy.mainfood.FoodViewModel
import onedaycat.com.food_fantasy.mainfood.activity.MainActivity
import onedaycat.com.food_fantasy.mainfood.activity.foodDetailActivity
import onedaycat.com.food_fantasy.store.CartStore
import onedaycat.com.food_fantasy.store.FoodCartLiveStore
import onedaycat.com.food_fantasy.store.FoodCartStore
import onedaycat.com.foodfantasyservicelib.input.AddToCartInput
import onedaycat.com.foodfantasyservicelib.input.GetProductsInput
import onedaycat.com.foodfantasyservicelib.input.RemoveFromCartInput
import onedaycat.com.foodfantasyservicelib.service.EcomService

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainMenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MainMenuFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, ItemClickedCallback {
    //View
    private var recyclerView:RecyclerView? = null
    private lateinit var foodViewModel: FoodViewModel

    private var foodAdapter: FoodAdapter? = null
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    //fixed data
    private val limit = 10
    private val userID = "u1"
    private val beginQTY = 1
    private var badgeCart = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                MainMenuFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_menu, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        prepareSwipeRefreshLayout()
        initViewModel()

        if (savedInstanceState == null) {
            firstFetchDataFood(limit)
        }
    }

    private fun firstFetchDataFood(limit: Int) {
        (activity as MainActivity).showLoadingDialog()
        val input = GetProductsInput(limit)
        foodViewModel.loadProducts(input)
    }

    private fun prepareSwipeRefreshLayout() {
        swipe_container.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.YELLOW, Color.RED)
        swipe_container.setOnRefreshListener(this)
    }

    private fun initViewModel() {
        CartStore.foodCart = FoodCartStore().apply {
            this.userId = userID
        }

        foodViewModel = ViewModelProviders.of(this,
                (activity as MainActivity).viewModelFactory { FoodViewModel(FoodCartLiveStore(CartStore), EcomService) })
                .get(FoodViewModel::class.java)

        foodDataObserver()
        cartObserver()
    }

    private fun cartObserver() {
        foodViewModel.cartStore.observe(this, Observer {
            it?.let {
                badgeCart = it.counter

                it
            }?.also {
                CartStore.foodCart = it.foodCart
                CartStore.counter = it.counter
            }
        })
    }

    private fun foodDataObserver() {
        foodViewModel.foodData.observe(this, Observer { data ->
            swipe_container.isRefreshing = false

            data?.let {
                foodAdapter?.let { adapter ->
                    adapter.notifyDataSetChanged()
                    adapter
                }

                foodAdapter = FoodAdapter(it, this.context!!, this)

                rv_food_list.layoutManager = LinearLayoutManager(this.context)
                rv_food_list.hasFixedSize()
                rv_food_list.adapter = foodAdapter
            }

            (activity as MainActivity).dismissDialog()
        })
    }

    override fun onRefresh() {
        swipe_container.isRefreshing = false
        fetchDataFood(limit)
    }

    private fun fetchDataFood(limit: Int) {
        (activity as MainActivity).showLoadingDialog()
        val input = GetProductsInput(limit)
        foodViewModel.loadProducts(input)
    }

    override fun onClicked(foodModel: FoodModel) {
        startActivity(activity?.foodDetailActivity(foodModel))
    }
}
