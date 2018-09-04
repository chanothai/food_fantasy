package onedaycat.com.food_fantasy.feature.payment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.appbar_collapsing_toolbar.*
import onedaycat.com.food_fantasy.R
import onedaycat.com.food_fantasy.common.BaseActivity
import onedaycat.com.food_fantasy.mainfood.activity.foodDetailActivity
import onedaycat.com.foodfantasyservicelib.contract.creditcard_payment.CreditCard
import onedaycat.com.foodfantasyservicelib.contract.creditcard_payment.CreditCardPayment
import onedaycat.com.foodfantasyservicelib.input.ChargeInput
import onedaycat.com.foodfantasyservicelib.input.CreditCardType
import onedaycat.com.foodfantasyservicelib.input.DeleteCartInput
import onedaycat.com.foodfantasyservicelib.service.EcomService

fun Context.PaymentActivity(userId: String): Intent {
    return Intent(this, PaymentActivity::class.java).apply {
        putExtra(USERID, userId)
    }
}

private const val USERID = "user_id"

class PaymentActivity : BaseActivity(), View.OnClickListener {
    private var userId: String? = null

    private lateinit var paymentViewModel: PaymentViewModel

    override fun isDisplayHomeEnable(): Boolean? = true

    override fun getToolbarInstance(): Toolbar? = toolbar

    override fun title(): String? = resources.getString(R.string.title_payment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        btn_payment.setOnClickListener(this)
        initViewModel()

        if (savedInstanceState == null) {
            userId = intent.getStringExtra(USERID)
            requireNotNull(userId) { "No food_name provided in Intent extras" }
        }
    }

    private fun initViewModel() {
        paymentViewModel = ViewModelProviders.of(this,
                viewModelFactory { PaymentViewModel(EcomService) }).get(PaymentViewModel::class.java)

        paymentViewModel.order.observe(this, Observer { order ->
            if (order != null) {

                val input = DeleteCartInput(
                        order.userId!!
                )

                paymentViewModel.deleteProductCart(input)

                Toast.makeText(this, "THANK YOU", Toast.LENGTH_LONG).show()
                dismissDialog()

                finish()
            }
        })
    }

    private fun btnPayClicked() {
        showLoadingDialog()
        val creditCart = CreditCard(
                CreditCardType.CreditCardMasterCard,
                cardName.text.toString(),
                card_numbers.text.toString(),
                card_ccv.text.toString(),
                card_expire_date.text.toString(),
                card_expire_year.text.toString()
        )

        val input = ChargeInput(
                userId!!,
                creditCart
        )

        paymentViewModel.payment(input)
    }

    override fun onClick(view: View?) {
        btnPayClicked()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) {
            finish()
            true
        }else {
            super.onOptionsItemSelected(item)
        }
    }
}
