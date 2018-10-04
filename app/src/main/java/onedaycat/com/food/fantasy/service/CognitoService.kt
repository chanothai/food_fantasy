package onedaycat.com.food.fantasy.service

import onedaycat.com.food.fantasy.api.CognitoUserRepo

class CognitoService {
    val userAuthService = UserAuthenticationService(CognitoUserRepo())
}