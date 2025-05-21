package com.example.moso

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.moso.ui.auth.AuthViewModel
import com.example.moso.ui.navigation.AppNavigation
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult

class MainActivity : ComponentActivity(){
    // ① Instancia tu ViewModel aquí
    private val authViewModel: AuthViewModel by viewModels()

    // ② CallbackManager de Facebook
    private val callbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializa el SDK y registra tu callback
        FacebookSdk.sdkInitialize(this)
        LoginManager.getInstance().registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    // Ahora sí existe authViewModel
                    authViewModel.loginWithFacebook(result.accessToken.token)
                }
                override fun onCancel() { }
                override fun onError(error: FacebookException) {
                    authViewModel.setErrorMessage(error.message)
                }
            }
        )

        setContent {
            AppNavigation()
        }
    }

    // ③ Este método recoge el resultado de Facebook
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

}



