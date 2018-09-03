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
import kotlinx.android.synthetic.main.badge_icon_layout.*
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
        toolbar.title = title()
        setSupportActionBar(toolbar)

        supportActionBar?.let {
            supportActionBar?.setDisplayHomeAsUpEnabled(isDisplayHomeEnable()!!)

            if (title() == null) it.setDisplayShowTitleEnabled(false)
        }
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

    protected inline fun <VM: ViewModel> viewModelFactory(crossinline f: () -> VM) =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T = f() as T
            }

}