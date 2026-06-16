package com.app.fishpoint.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.fishpoint.data.api.FishPointApi
import com.app.fishpoint.data.model.User
import com.app.fishpoint.data.model.UserRole
import com.app.fishpoint.data.repository.FishPointRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    var currentUser: User? by mutableStateOf(null)
        private set

    val isAdmin: Boolean get() = currentUser?.role == UserRole.ADMIN
    val isLoggedIn: Boolean get() = currentUser != null
    val currentUsername: String? get() = currentUser?.username
    val currentUserId: Int? get() = currentUser?.id

    var loginUsername by mutableStateOf("")
        private set
    var loginPassword by mutableStateOf("")
        private set
    var loginErrorMessage by mutableStateOf("")
        private set
    var isLoginLoading by mutableStateOf(false)
        private set

    var registerUsername by mutableStateOf("")
        private set
    var registerPassword by mutableStateOf("")
        private set
    var registerConfirmPassword by mutableStateOf("")
        private set
    var isPasswordVisible by mutableStateOf(false)
        private set
    var isConfirmPasswordVisible by mutableStateOf(false)
        private set
    var isTermsAccepted by mutableStateOf(false)
        private set
    var hasPasswordMismatch by mutableStateOf(false)
        private set
    var isRegisterLoading by mutableStateOf(false)
        private set
    var registerErrorMessage by mutableStateOf("")
        private set

    private val repository = FishPointRepository(FishPointApi.create())

    fun onLoginUsernameChanged(value: String) { loginUsername = value; loginErrorMessage = "" }
    fun onLoginPasswordChanged(value: String) { loginPassword = value; loginErrorMessage = "" }
    fun onRegisterUsernameChanged(value: String) { registerUsername = value; registerErrorMessage = "" }
    fun onRegisterPasswordChanged(value: String) { registerPassword = value; hasPasswordMismatch = false; registerErrorMessage = "" }
    fun onRegisterConfirmPasswordChanged(value: String) { registerConfirmPassword = value; hasPasswordMismatch = false; registerErrorMessage = "" }

    fun togglePasswordVisibility() { isPasswordVisible = !isPasswordVisible }
    fun toggleConfirmPasswordVisibility() { isConfirmPasswordVisible = !isConfirmPasswordVisible }
    fun onTermsCheckedChanged(checked: Boolean) { isTermsAccepted = checked }

    val isRegisterButtonEnabled: Boolean
        get() = isTermsAccepted && registerUsername.isNotBlank() && registerPassword.isNotBlank()

    fun validateAndLogin(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoginLoading = true
            loginErrorMessage = ""

            if (loginUsername.isBlank() || loginPassword.isBlank()) {
                loginErrorMessage = "Username dan password tidak boleh kosong"
                isLoginLoading = false
                return@launch
            }

            val result = repository.login(loginUsername, loginPassword)
            if (result.isSuccess) {
                val user = result.getOrNull()
                if (user?.isBanned == true) loginErrorMessage = "Akun Anda telah dinonaktifkan"
                else if (user != null) {
                    currentUser = user
                    onSuccess()
                } else loginErrorMessage = "Respons server tidak valid"
            } else loginErrorMessage = result.exceptionOrNull()?.message ?: "Username atau password tidak valid"

            isLoginLoading = false
        }
    }

    fun validateAndRegister(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isRegisterLoading = true
            registerErrorMessage = ""

            when {
                registerPassword != registerConfirmPassword -> hasPasswordMismatch = true
                registerUsername.isBlank() || registerPassword.isBlank() || registerConfirmPassword.isBlank() -> registerErrorMessage = "Harap isi semua kolom"
                else -> {
                    val result = repository.register(registerUsername, registerPassword, registerUsername)
                    if (result.isSuccess) {
                        val user = result.getOrNull()
                        if (user != null) {
                            currentUser = user
                            onSuccess()
                        } else registerErrorMessage = "Respons server tidak valid"
                    } else registerErrorMessage = result.exceptionOrNull()?.message ?: "Gagal mendaftar"
                }
            }
            isRegisterLoading = false
        }
    }

    fun updateFullName(newName: String) {
        val updated = currentUser?.copy(fullName = newName) ?: return
        currentUser = updated
        // Sinkronisasi MockData dihapus karena sepenuhnya menggunakan API sekarang
    }

    var allUsers by mutableStateOf<List<User>>(emptyList())
        private set
    var isUsersLoading by mutableStateOf(false)
        private set
    var usersErrorMessage by mutableStateOf<String?>(null)
        private set

    fun fetchUsers() {
        viewModelScope.launch {
            isUsersLoading = true
            usersErrorMessage = null
            val result = repository.getUsers()
            if (result.isSuccess) {
                allUsers = result.getOrNull() ?: emptyList()
            } else {
                usersErrorMessage = result.exceptionOrNull()?.message ?: "Gagal mengambil data pengguna"
            }
            isUsersLoading = false
        }
    }

    fun manageUser(action: String, userId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = repository.manageUser(action, userId)
            if (result.isSuccess) {
                fetchUsers()
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.message ?: "Gagal mengubah status pengguna")
            }
        }
    }

    fun resetAllState() {
        currentUser = null
        loginUsername = ""; loginPassword = ""; loginErrorMessage = ""
        registerUsername = ""; registerPassword = ""; registerConfirmPassword = ""
        isPasswordVisible = false; isConfirmPasswordVisible = false; isTermsAccepted = false
        hasPasswordMismatch = false; registerErrorMessage = ""
        allUsers = emptyList()
    }
}