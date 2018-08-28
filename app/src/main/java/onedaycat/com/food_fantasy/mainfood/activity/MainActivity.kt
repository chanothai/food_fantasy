package onedaycat.com.food_fantasy.mainfood.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import onedaycat.com.food_fantasy.R
import kotlinx.android.synthetic.main.rs_list_food.*
import onedaycat.com.food_fantasy.mainfood.FoodViewModel
import onedaycat.com.food_fantasy.mainfood.adapter.FoodAdapter
import onedaycat.com.foodfantasyservicelib.input.GetProductsInput

class MainActivity : AppCompatActivity() {

    private lateinit var foodViewModel: FoodViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            foodViewModel = ViewModelProviders.of(this).get(FoodViewModel::class.java)
            foodViewModel.foodData.observe(this, Observer { data ->
                rv_food_list.layoutManager = LinearLayoutManager(this)
                rv_food_list.adapter = FoodAdapter(data!!, this)
            })

            val input = GetProductsInput(4)
            foodViewModel.loadProducts(input)
        }
    }
}
