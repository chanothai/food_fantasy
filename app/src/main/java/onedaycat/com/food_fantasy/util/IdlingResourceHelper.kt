package onedaycat.com.food_fantasy.util

object IdlingResourceHelper {
    var mIdlingResource: CustomIdlingResource? = null
        get(){
            field?.let {
                return it
            }

            field = CustomIdlingResource()
            return field
        }
}