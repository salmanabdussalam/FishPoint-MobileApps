package com.app.fishpoint.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.fishpoint.ui.components.CurvedFishPointLogo
import com.app.fishpoint.ui.components.fishPointTextFieldColors
import com.app.fishpoint.ui.theme.*
import com.app.fishpoint.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onNavigateToRegister: () -> Unit, onContinueAsGuest: () -> Unit, authViewModel: AuthViewModel = viewModel()) {
    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(horizontal = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(48.dp))
        CurvedFishPointLogo()
        Spacer(Modifier.height(16.dp))
        Text(text = "Temukan spot mancing terbaik", fontSize = 12.sp, color = TextHint, textAlign = TextAlign.Center, letterSpacing = 0.5.sp)
        Spacer(Modifier.height(40.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Username", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(value = authViewModel.loginUsername, onValueChange = { authViewModel.onLoginUsernameChanged(it) }, placeholder = { Text("Masukkan Username", color = TextHint) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = fishPointTextFieldColors(), singleLine = true)
        }
        Spacer(Modifier.height(16.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Password", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(value = authViewModel.loginPassword, onValueChange = { authViewModel.onLoginPasswordChanged(it) }, placeholder = { Text("Masukkan Password", color = TextHint) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = fishPointTextFieldColors(), singleLine = true, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
        }
        if (authViewModel.loginErrorMessage.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(authViewModel.loginErrorMessage, color = ErrorRed, fontSize = 13.sp)
        }

        Spacer(Modifier.height(28.dp))
        Button(onClick = { authViewModel.validateAndLogin(onLoginSuccess) }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = FishGreenLight), enabled = !authViewModel.isLoginLoading) {
            if (authViewModel.isLoginLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = FishGreenDark, strokeWidth = 2.dp)
            else Text("LOGIN", fontSize = 15.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp, color = FishGreenDark)
        }
        Spacer(Modifier.height(32.dp))
        Text("Belum punya akun?", fontSize = 13.sp, color = TextSecondary, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onNavigateToRegister, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary), border = ButtonDefaults.outlinedButtonBorder) {
            Text("DAFTAR SEKARANG", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
        }
        Spacer(Modifier.height(16.dp))
        Text("Lihat sebagai Tamu", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextSecondary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().clickable(onClick = onContinueAsGuest).padding(vertical = 4.dp))
    }
}