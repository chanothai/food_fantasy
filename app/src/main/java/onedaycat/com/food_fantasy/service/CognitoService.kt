package onedaycat.com.food_fantasy.service

import onedaycat.com.food_fantasy.api.CognitoUserRepo

class CognitoService {
    val userAuthService = UserAuthenticationService(CognitoUserRepo())
}