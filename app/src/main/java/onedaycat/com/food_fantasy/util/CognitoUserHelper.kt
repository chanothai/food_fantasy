package onedaycat.com.food_fantasy.util

import android.app.Application
import android.content.Context
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.regions.Regions
import onedaycat.com.food_fantasy.common.MainApplication

class CognitoUserHelper {
    companion object {
        val cognitoUser:() -> CognitoUserPool = fun(): CognitoUserPool {
            return CognitoUserPool(
                    MainApplication.applicationContext(),
                    "ap-southeast-1_M7jwwdfSy",
                    "6qualkk2qcjqokgsujkgm5c2sq",
                    null,
                    Regions.AP_SOUTHEAST_1)
        }
    }
}