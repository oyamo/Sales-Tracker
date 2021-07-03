package com.oyasis.salestracker

import android.content.Context
import android.provider.Settings
import com.amazonaws.mobileconnectors.cognitoidentityprovider.*
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.VerificationHandler
import com.amazonaws.regions.Regions
import com.oyasis.salestracker.Constants.COGNITO_CLIENT_ID
import com.oyasis.salestracker.Constants.COGNITO_POOL_ID
import com.oyasis.salestracker.Constants.COGNITO_SECRET
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.lang.Exception
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails




public class Cognito(
    private var ctx: Context
) {
    private val poolID = COGNITO_POOL_ID
    private val clientID = COGNITO_CLIENT_ID
    private val clientSecret = COGNITO_SECRET

    private val regions: Regions = Regions.US_EAST_1
    private var userPool: CognitoUserPool =
        CognitoUserPool(ctx, this.poolID, this.clientID, this.clientSecret, this.regions)
    private  var cognitoUserAttributes: CognitoUserAttributes =
        CognitoUserAttributes()


    private var userPassword: String? = null
    @Volatile private var instance: Cognito? = null

    companion object {
        @JvmStatic
        fun getInstance(ctx: Context) : Cognito {
            return Cognito(ctx)
        }
    }

    fun signUpInBackground(userId: String, password: String, callBack: (  user: CognitoUser?,  state: Boolean, e: Exception?) -> Unit) {
        val signUpHandler = object : SignUpHandler {
            override fun onSuccess(
                user: CognitoUser?,
                signUpConfirmationState: Boolean,
                cognitoUserCodeDeliveryDetails: CognitoUserCodeDeliveryDetails?
            ) {
               callBack(user, signUpConfirmationState,null)
            }

            override fun onFailure(exception: Exception?) {
                callBack(null, false, exception)

            }

        }
        userPool.signUpInBackground(userId, password, cognitoUserAttributes, null, signUpHandler)
    }


    /**
     * Confirms if a user has signed up
     */
    fun confirmUser(userId: String, code: String, callback: (exeption: Exception?) -> Unit) {
        val cognitoUser: CognitoUser = userPool.getUser(userId)
        val handler = object : GenericHandler {
            override fun onSuccess() {
                callback(null)
            }

            override fun onFailure(exception: Exception?) {
               callback(exception)
            }

        }
        cognitoUser.confirmSignUpInBackground(code, false, handler)
    }

    fun reSendConfirmationCode(userId: String, callback: (vmedium : CognitoUserCodeDeliveryDetails?, exception: Exception?) -> Unit) {
        val cognitoUser: CognitoUser = userPool.getUser(userId)
        val verificationHandler = object : VerificationHandler {
            override fun onSuccess(verificationCodeDeliveryMedium: CognitoUserCodeDeliveryDetails?) {
                callback(verificationCodeDeliveryMedium, null)
            }

            override fun onFailure(exception: Exception?) {
                callback(null, exception)
            }

        }
        cognitoUser.resendConfirmationCodeInBackground(verificationHandler)
    }

    fun addAttribute(key: String, value: String) {
        cognitoUserAttributes.addAttribute(key, value)
    }

    fun userLogin(userId: String, password: String, callback:(userSession: CognitoUserSession?, exception: Exception?) -> Unit) {
        val user: CognitoUser = userPool.getUser(userId);
        this.userPassword = password;

        val authenticationHandler = object : AuthenticationHandler {
            override fun onSuccess(userSession: CognitoUserSession?, newDevice: CognitoDevice?) {
                    callback(userSession, null);
            }

            override fun getAuthenticationDetails(
                authenticationContinuation: AuthenticationContinuation?,
                userId: String?
            ) {
                val authenticationDetails = AuthenticationDetails(userId, userPassword, null)
                authenticationContinuation?.setAuthenticationDetails(authenticationDetails);
                authenticationContinuation?.continueTask();
            }

            override fun getMFACode(continuation: MultiFactorAuthenticationContinuation?) {

            }

            override fun authenticationChallenge(continuation: ChallengeContinuation?) {

            }

            override fun onFailure(exception: Exception?) {
                callback(null, exception)
            }
        }

        user.getSessionInBackground(authenticationHandler)

    }


}