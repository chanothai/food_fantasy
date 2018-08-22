package onedaycat.com.food_fantasy

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import onedaycat.com.foodfantasyservicelib.input.CreateUserInput

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val input = CreateUserInput(
                "ballomo@onedaycat.com",
                "Chanothai Duangrahwa",
                "password2233"
        )
    }
}
