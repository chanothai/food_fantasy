package onedaycat.com.food_fantasy.common

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.appbar_normal.view.*
import kotlinx.android.synthetic.main.badge_icon_layout.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import onedaycat.com.food_fantasy.R
import onedaycat.com.food_fantasy.dialog.LoadingDialogFragment


abstract class BaseActivity: AppCompatActivity() {

    private val tagDialogFragment = "dialog_fragment"
    private var loadingDialogFragment: LoadingDialogFragment? = null
    private lateinit var toolbar: Toolbar

    override fun onResume() {
        super.onResume()

        this.getToolbarInstance()?.let {
            this.initView(it)
        }
    }

    private fun initView(toolbar: Toolbar) {
        this.toolbar = toolbar

        title()?.let {
            toolbar.title_toolbar.text = it
        }

        toolbar.setNavigationIcon(R.drawable.ic_back_arrow_white)
        setSupportActionBar(toolbar)

        supportActionBar?.let {
            supportActionBar?.setDisplayHomeAsUpEnabled(isDisplayHomeEnable()!!)

            it.setDisplayShowTitleEnabled(false)
        }
    }

    fun updateTitleToolbar(title: String) {
        toolbar.title_toolbar.text = title
    }

    abstract fun isDisplayHomeEnable(): Boolean?
    abstract fun getToolbarInstance(): Toolbar?
    abstract fun title(): String?

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

    fun convertLayoutToImage(count: Int, drawable: Int): Drawable {
        val view = LayoutInflater.from(applicationContext).inflate(R.layout.badge_icon_layout, null)

        view.findViewById<ImageView>(R.id.menu_icon).setImageResource(drawable)

        if (count == 0) {
            view.findViewById<FrameLayout>(R.id.counter_value_panel).visibility = View.GONE
        } else {
            view.findViewById<TextView>(R.id.txt_count).text = count.toString()
        }

        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))

        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.isDrawingCacheEnabled = true
        view.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH

        val bitmap = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false

        return BitmapDrawable(applicationContext.resources, bitmap)
    }

    inline fun <VM: ViewModel> viewModelFactory(crossinline f: () -> VM) =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T = f() as T
            }
}