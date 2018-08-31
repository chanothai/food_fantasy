package onedaycat.com.food_fantasy.common

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import onedaycat.com.food_fantasy.R
import onedaycat.com.food_fantasy.dialog.LoadingDialogFragment


abstract class BaseActivity: AppCompatActivity() {

    private val tagDialogFragment = "dialog_fragment"
    private var loadingDialogFragment: LoadingDialogFragment? = null

    override fun onResume() {
        super.onResume()

        if (getToolbarInstance() != null) {
            this.getToolbarInstance()?.let {
                this.initView(it)
            }
        }
    }

    private fun initView(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(false)
        }
    }

    abstract fun getToolbarInstance(): Toolbar?

    fun showLoadingDialog() {
        dismissDialog()
        loadingDialogFragment = LoadingDialogFragment.Builder.build()
        createFragmentDialog(loadingDialogFragment!!)
    }

    fun dismissDialog() {
        if (loadingDialogFragment != null) {
            loadingDialogFragment?.dismiss()
        }
    }

    private fun createFragmentDialog(dialogFragment: DialogFragment) {
        dialogFragment.show(supportFragmentManager, tagDialogFragment)
    }

    fun Drawable.setIconColor(color: Int) {
        mutate()
        setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }

    protected inline fun <VM: ViewModel> viewModelFactory(crossinline f: () -> VM) =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T = f() as T
            }
}