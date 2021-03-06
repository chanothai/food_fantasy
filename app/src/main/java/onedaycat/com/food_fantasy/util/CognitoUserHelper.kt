package onedaycat.com.food_fantasy.util

import android.content.Context
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.regions.Regions

class CognitoUserHelper {

    companion object {
        val cognitoUser: (context: Context) -> CognitoUserPool = fun(context): CognitoUserPool {
            return CognitoUserPool(
                    context,
                    "ap-southeast-1_M7jwwdfSy",
                    "6qualkk2qcjqokgsujkgm5c2sq",
                    null,
                    Regions.AP_SOUTHEAST_1)
        }
    }
}