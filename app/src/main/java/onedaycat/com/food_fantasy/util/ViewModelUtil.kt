package onedaycat.com.food_fantasy.util

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import onedaycat.com.food_fantasy.mainfood.FoodViewModel

object ViewModelUtil {
    var mockViewModel: ViewModelProvider.Factory? = null

    get() {
        return field?.let {
            it
        }
    }

    set(value) {
        value?.let {
            field = value
        }
    }

    fun <T : ViewModel> createViewModelFor(model: T): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(model.javaClass)) {
                        return model as T
                    }
                    throw IllegalArgumentException("Unexpected model class $modelClass")
                }
            }
}