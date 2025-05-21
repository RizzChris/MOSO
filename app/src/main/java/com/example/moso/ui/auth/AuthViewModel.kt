package com.example.moso.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moso.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val authRepo = AuthRepository()

    // Estado de nombre de usuario
    private val _currentUserName = MutableStateFlow<String?>(null)
    val currentUserName: StateFlow<String?> = _currentUserName

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Estado de mensaje de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    /** Permite ajustar el mensaje de error desde la UI */
    fun setErrorMessage(msg: String?) {
        _errorMessage.value = msg
    }

    /** Cierra sesión */
    fun logout() {
        authRepo.logout()
        _currentUserName.value = null
    }

    /** Login con email y contraseña */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val user: FirebaseUser = authRepo.login(email, password).getOrThrow()
                _currentUserName.value = user.displayName
                    ?: user.email
                            ?: "Usuario"
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Registro con email, contraseña, nombre y apellido */
    fun register(email: String, password: String, nombre: String, apellido: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val user: FirebaseUser = authRepo.register(email, password, nombre, apellido)
                    .getOrThrow()
                _currentUserName.value = user.displayName
                    ?: user.email
                            ?: "Usuario"
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Login con token de Google */
    fun loginWithGoogle(idToken: String) = viewModelScope.launch {
        _isLoading.value = true
        _errorMessage.value = null
        authRepo.signInWithGoogle(idToken)
            .onSuccess { user ->
                _currentUserName.value = user.displayName
                    ?: user.email
                            ?: "Usuario"
            }
            .onFailure { e ->
                _errorMessage.value = e.message
            }
        _isLoading.value = false
    }

    /** Login con token de Facebook */
    fun loginWithFacebook(accessToken: String) = viewModelScope.launch {
        _isLoading.value = true
        _errorMessage.value = null
        authRepo.signInWithFacebook(accessToken)
            .onSuccess { user ->
                _currentUserName.value = user.displayName
                    ?: user.email
                            ?: "Usuario"
            }
            .onFailure { e ->
                _errorMessage.value = e.message
            }
        _isLoading.value = false
    }
}

