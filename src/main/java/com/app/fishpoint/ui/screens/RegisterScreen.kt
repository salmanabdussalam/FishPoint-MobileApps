package com.app.fishpoint.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.fishpoint.ui.components.CurvedFishPointLogo
import com.app.fishpoint.ui.components.LabeledField
import com.app.fishpoint.ui.components.fishPointTextFieldColors
import com.app.fishpoint.ui.theme.*
import com.app.fishpoint.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit, onBackClick: () -> Unit, onNavigateToLogin: () -> Unit, authViewModel: AuthViewModel = viewModel()) {
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Box(modifier = Modifier.fillMaxWidth().background(FishGreen).padding(horizontal = 16.dp, vertical = 16.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.White, modifier = Modifier.align(Alignment.CenterStart).size(24.dp).clickable(onClick = onBackClick))
            Text("Buat akun baru", modifier = Modifier.align(Alignment.Center), fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        }

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(28.dp))
            CurvedFishPointLogo()
            Spacer(Modifier.height(16.dp))
            Text("Daftar untuk menemukan spot terbaik", fontSize = 13.sp, color = TextHint, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            Text("Bergabung dengan komunitas\npemancing", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary, textAlign = TextAlign.Center, lineHeight = 26.sp)
            Spacer(Modifier.height(24.dp))

            LabeledField("Username") {
                OutlinedTextField(value = authViewModel.registerUsername, onValueChange = { authViewModel.onRegisterUsernameChanged(it) }, placeholder = { Text("pemancing_solo", color = TextHint) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = fishPointTextFieldColors(), singleLine = true)
            }
            Spacer(Modifier.height(14.dp))
            LabeledField("Password") {
                OutlinedTextField(value = authViewModel.registerPassword, onValueChange = { authViewModel.onRegisterPasswordChanged(it) }, placeholder = { Text("••••••••", color = TextHint) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = fishPointTextFieldColors(), singleLine = true, visualTransformation = if (authViewModel.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), trailingIcon = { IconButton(onClick = { authViewModel.togglePasswordVisibility() }) { Icon(if (authViewModel.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null, tint = TextHint) } }, supportingText = { Text("Hanya huruf, angka, dan garis bawah", fontSize = 11.sp, color = TextHint) })
            }
            Spacer(Modifier.height(4.dp))
            LabeledField("Confirm Password") {
                OutlinedTextField(value = authViewModel.registerConfirmPassword, onValueChange = { authViewModel.onRegisterConfirmPasswordChanged(it) }, placeholder = { Text("Ketik ulang password", color = TextHint) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = if (authViewModel.hasPasswordMismatch) ErrorRed else FishGreen, unfocusedBorderColor = if (authViewModel.hasPasswordMismatch) ErrorRed else BorderColor), singleLine = true, isError = authViewModel.hasPasswordMismatch, visualTransformation = if (authViewModel.isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), trailingIcon = { IconButton(onClick = { authViewModel.toggleConfirmPasswordVisibility() }) { Icon(if (authViewModel.isConfirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null, tint = TextHint) } }, supportingText = if (authViewModel.hasPasswordMismatch) { { Text("Password tidak cocok", color = ErrorRed, fontSize = 12.sp) } } else null)
            }

            if (authViewModel.registerErrorMessage.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(authViewModel.registerErrorMessage, color = ErrorRed, fontSize = 13.sp)
            }
            Spacer(Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = authViewModel.isTermsAccepted, onCheckedChange = { authViewModel.onTermsCheckedChanged(it) }, colors = CheckboxDefaults.colors(checkedColor = FishGreen))
                Spacer(Modifier.width(4.dp))
                Text(buildAnnotatedString { append("Saya Setuju dengan "); withStyle(SpanStyle(color = FishGreen, fontWeight = FontWeight.Medium)) { append("Syarat & Ketentuan") }; append(" dan "); withStyle(SpanStyle(color = FishGreen, fontWeight = FontWeight.Medium)) { append("Kebijakan Privasi FishPoint") } }, fontSize = 12.sp, color = TextSecondary, lineHeight = 18.sp)
            }
            Spacer(Modifier.height(20.dp))

            Button(onClick = { authViewModel.validateAndRegister(onRegisterSuccess) }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = FishGreen), enabled = authViewModel.isRegisterButtonEnabled && !authViewModel.isRegisterLoading) {
                if (authViewModel.isRegisterLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                else Text("→  Buat Akun", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(Modifier.height(16.dp))
            Row {
                Text("Sudah punya akun?  ", fontSize = 13.sp, color = TextSecondary)
                Text("Masuk sekarang!", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = FishGreen, modifier = Modifier.clickable(onClick = onNavigateToLogin))
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}