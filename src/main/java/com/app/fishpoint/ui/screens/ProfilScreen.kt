package com.app.fishpoint.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fishpoint.data.model.FishingSpot
import com.app.fishpoint.data.model.User
import com.app.fishpoint.navigation.Screen
import com.app.fishpoint.ui.components.*
import com.app.fishpoint.ui.theme.*
import com.app.fishpoint.ui.viewmodel.AuthViewModel

@Composable
fun ProfilScreen(onBerandaClick: () -> Unit, onTambahClick: () -> Unit, onLogoutClick: () -> Unit, onEditSpotClick: (spotId: Int) -> Unit, authViewModel: AuthViewModel, spotViewModel: com.app.fishpoint.ui.viewmodel.SpotViewModel) {
    val user = authViewModel.currentUser ?: return
    val ownSpots = remember(user.username, spotViewModel.spots) { spotViewModel.spots.filter { it.isOwnedBy(user.username) } }
    var showEditDialog by remember { mutableStateOf(false) }

    if (showEditDialog) {
        EditProfilDialog(currentName = user.fullName ?: "", onDismiss = { showEditDialog = false }, onSave = { newName -> authViewModel.updateFullName(newName); showEditDialog = false })
    }

    Scaffold(bottomBar = { FishPointBottomBar(currentRoute = Screen.Profil.route, onBeranda = onBerandaClick, onTambah = onTambahClick, onProfil = {}) }, containerColor = PageBackground) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState())) {
            Box(modifier = Modifier.fillMaxWidth().background(FishGreenDark).padding(vertical = 28.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    FishPointLogo(size = 1.1f)
                    Spacer(Modifier.height(10.dp))
                    Text(user.fullName ?: "Unknown", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Member sejak ${user.memberSince ?: "-"}", fontSize = 13.sp, color = Color.White.copy(alpha = 0.85f))
                    Spacer(Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) { Text(ownSpots.size.toString(), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White); Text("Spot", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f)) }
                        Box(modifier = Modifier.width(1.dp).height(36.dp).background(Color.White.copy(alpha = 0.4f)))
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) { Text(ownSpots.sumOf { it.reviewCount }.toString(), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White); Text("Ulasan Diterima", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f)) }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            if (ownSpots.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth().background(Color.White).padding(16.dp)) {
                    Text("Spot Saya", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(Modifier.height(10.dp))
                    ownSpots.forEachIndexed { idx, spot ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            SpotCoverImage(coverUrl = spot.coverPhotoUrl, contentDescription = spot.name, modifier = Modifier.size(56.dp).clip(RoundedCornerShape(6.dp)))
                            Column(modifier = Modifier.weight(1f)) { Text(spot.name, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary, maxLines = 1); StarRatingDisplay(rating = spot.averageRating, reviewCount = spot.reviewCount) }
                            Icon(Icons.Default.Edit, contentDescription = "Edit Spot", tint = TextSecondary, modifier = Modifier.size(20.dp).clickable { onEditSpotClick(spot.id) })
                        }
                        if (idx < ownSpots.lastIndex) HorizontalDivider(color = BorderColor, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxWidth().background(Color.White).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Anchor, contentDescription = null, tint = TextHint, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("Belum punya spot", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
                    Text("Tekan tombol + di bawah untuk menambah spot pertamamu", fontSize = 12.sp, color = TextHint)
                }
            }
            Spacer(Modifier.height(12.dp))

            Column(modifier = Modifier.fillMaxWidth().background(Color.White).padding(vertical = 8.dp)) {
                Text("Pengaturan Akun", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
                SettingRow(icon = Icons.Default.Person, iconBg = FishGreenSurface, iconTint = FishGreen, label = "Edit Profil", onClick = { showEditDialog = true })
                HorizontalDivider(color = BorderColor, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                SettingRow(icon = Icons.Default.Logout, iconBg = Color(0xFFFFF3E0), iconTint = Color(0xFFE65100), label = "Keluar", onClick = onLogoutClick)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun EditProfilDialog(currentName: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var namaInput by remember { mutableStateOf(currentName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profil", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { Text("Nama Tampil", fontSize = 13.sp, color = TextSecondary); OutlinedTextField(value = namaInput, onValueChange = { namaInput = it }, singleLine = true, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FishGreen, unfocusedBorderColor = BorderColor)) } },
        confirmButton = { TextButton(onClick = { if (namaInput.isNotBlank()) onSave(namaInput) }, colors = ButtonDefaults.textButtonColors(contentColor = FishGreen)) { Text("Simpan", fontWeight = FontWeight.SemiBold) } },
        dismissButton = { TextButton(onClick = onDismiss, colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)) { Text("Batal") } }
    )
}

@Composable
private fun SettingRow(icon: ImageVector, iconBg: Color, iconTint: Color, label: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(modifier = Modifier.size(38.dp).clip(RoundedCornerShape(8.dp)).background(iconBg), contentAlignment = Alignment.Center) { Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp)) }
        Text(label, fontSize = 15.sp, color = TextPrimary, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextHint, modifier = Modifier.size(20.dp))
    }
}