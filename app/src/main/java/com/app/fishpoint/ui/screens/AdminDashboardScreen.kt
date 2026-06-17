package com.app.fishpoint.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.Logout
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
import com.app.fishpoint.data.model.Review
import com.app.fishpoint.data.model.User
import com.app.fishpoint.ui.components.SpotCoverImage
import com.app.fishpoint.ui.theme.*
import com.app.fishpoint.ui.viewmodel.AuthViewModel
import com.app.fishpoint.ui.viewmodel.SpotViewModel

@Composable
fun AdminDashboardScreen(
    onLogoutClick: () -> Unit,
    authViewModel: AuthViewModel,
    spotViewModel: SpotViewModel
) {
    var activeTab by remember { mutableStateOf(AdminTab.SPOT) }

    Scaffold(
        containerColor = PageBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AdminHeader(
                adminName   = authViewModel.currentUser?.fullName ?: "Admin",
                onLogout    = onLogoutClick
            )

            AdminStats(spotViewModel, authViewModel)

            AdminTabRow(
                activeTab = activeTab,
                onTabChange = { activeTab = it }
            )

            when (activeTab) {
                AdminTab.SPOT -> AdminSpotTab(spotViewModel)
                AdminTab.USER -> AdminUserTab(authViewModel)
            }
        }
    }
}

private enum class AdminTab { SPOT, USER }

@Composable
private fun AdminHeader(adminName: String, onLogout: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(FishGreenDark)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Dashboard Admin", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(adminName, fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
        }
        IconButton(onClick = onLogout) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = Color.White)
        }
    }
}

@Composable
private fun AdminStats(spotViewModel: SpotViewModel, authViewModel: AuthViewModel) {
    val totalSpot  = spotViewModel.spots.size
    val totalUser  = authViewModel.allUsers.size
    val totalUlasan = spotViewModel.spots.sumOf { it.reviews.size }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard(value = totalSpot.toString(),   label = "Spot",   icon = Icons.Default.Anchor,   modifier = Modifier.weight(1f))
        StatCard(value = totalUser.toString(),   label = "Member", icon = Icons.Default.People,   modifier = Modifier.weight(1f))
        StatCard(value = totalUlasan.toString(), label = "Ulasan", icon = Icons.AutoMirrored.Filled.Comment,  modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(value: String, label: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(FishGreenSurface)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = FishGreen, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(4.dp))
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = FishGreenDark)
        Text(label, fontSize = 11.sp, color = TextSecondary)
    }
}

@Composable
private fun AdminTabRow(activeTab: AdminTab, onTabChange: (AdminTab) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AdminTab.entries.forEach { tab ->
            val isActive = tab == activeTab
            val label = if (tab == AdminTab.SPOT) "Kelola Spot" else "Kelola User"
            val icon  = if (tab == AdminTab.SPOT) Icons.Default.Anchor else Icons.Default.People

            FilterChip(
                selected = isActive,
                onClick  = { onTabChange(tab) },
                label    = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp))
                        Text(label, fontSize = 13.sp)
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor    = FishGreen,
                    selectedLabelColor        = Color.White,
                    selectedLeadingIconColor  = Color.White,
                )
            )
        }
    }
    HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
}

@Composable
private fun AdminSpotTab(spotViewModel: SpotViewModel) {
    val spots = spotViewModel.spots
    var deleteSpotTarget by remember { mutableStateOf<FishingSpot?>(null) }
    var deleteReviewTarget by remember { mutableStateOf<Pair<Int, Review>?>(null) }
    val expandedSpotIds = remember { mutableStateListOf<Int>() }

    val context = androidx.compose.ui.platform.LocalContext.current

    deleteSpotTarget?.let { spot ->
        DeleteConfirmDialog(
            title   = "Hapus Spot",
            message = "Yakin menghapus \"${spot.name}\"? Tindakan ini tidak bisa dibatalkan.",
            onConfirm = {
                spotViewModel.deleteSpot(spot.id,
                    onSuccess = { deleteSpotTarget = null },
                    onError = { android.widget.Toast.makeText(context, it, android.widget.Toast.LENGTH_SHORT).show() }
                )
            },
            onDismiss = { deleteSpotTarget = null }
        )
    }

    deleteReviewTarget?.let { (spotId, review) ->
        DeleteConfirmDialog(
            title   = "Hapus Ulasan",
            message = "Hapus ulasan dari \"${review.username}\"? Konten tidak akan ditampilkan lagi.",
            onConfirm = {
                spotViewModel.deleteReview(review.id,
                    onSuccess = { deleteReviewTarget = null },
                    onError = { android.widget.Toast.makeText(context, it, android.widget.Toast.LENGTH_SHORT).show() }
                )
            },
            onDismiss = { deleteReviewTarget = null }
        )
    }

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 8.dp)) {
        if (spots.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    Text("Tidak ada spot.", color = TextHint, fontSize = 14.sp)
                }
            }
        }

        items(items = spots, key = { it.id }) { spot ->
            val isExpanded = spot.id in expandedSpotIds
            AdminSpotRow(
                spot       = spot,
                isExpanded = isExpanded,
                onDelete   = { deleteSpotTarget = spot },
                onToggleExpand = { if (isExpanded) expandedSpotIds.remove(spot.id) else expandedSpotIds.add(spot.id) }
            )

            if (isExpanded) {
                if (spot.reviews.isEmpty()) {
                    Text("Belum ada ulasan.", fontSize = 12.sp, color = TextHint, modifier = Modifier.padding(start = 76.dp, end = 16.dp, bottom = 8.dp))
                } else {
                    spot.reviews.forEach { review ->
                        AdminReviewRow(review = review, onDelete = { deleteReviewTarget = spot.id to review })
                    }
                }
            }
            HorizontalDivider(color = BorderColor, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

@Composable
private fun AdminReviewRow(review: Review, onDelete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 76.dp, end = 16.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(review.username ?: "Unknown", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(10.dp))
                Text("${review.rating}", fontSize = 11.sp, color = TextSecondary)
            }
            Text("\"${review.text ?: ""}\"", fontSize = 11.sp, color = TextSecondary, maxLines = 1)
        }
        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color(0xFFE53935), modifier = Modifier.size(16.dp).clickable(onClick = onDelete))
    }
}

@Composable
private fun AdminSpotRow(spot: FishingSpot, isExpanded: Boolean, onDelete: () -> Unit, onToggleExpand: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onToggleExpand).padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SpotCoverImage(coverUrl = spot.coverPhotoUrl, contentDescription = spot.name, modifier = Modifier.size(48.dp).clip(RoundedCornerShape(6.dp)))
        Column(modifier = Modifier.weight(1f)) {
            Text(spot.name, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary, maxLines = 1)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(spot.waterType ?: "", fontSize = 11.sp, color = TextSecondary)
                Text("· @${spot.ownerUsername ?: "unknown"}", fontSize = 11.sp, color = TextHint)
                Text("·", fontSize = 11.sp, color = TextHint)
                if (spot.averageRating != null) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(11.dp))
                    Text("%.1f".format(spot.averageRating), fontSize = 11.sp, color = TextSecondary)
                } else {
                    Text("Belum ada rating", fontSize = 11.sp, color = TextHint)
                }
            }
        }
        Icon(if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
        IconButton(onClick = onDelete, modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFFFEBEE))) {
            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color(0xFFE53935), modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun AdminUserTab(authViewModel: AuthViewModel) {
    LaunchedEffect(Unit) {
        authViewModel.fetchUsers()
    }
    
    val members = authViewModel.allUsers
    var banTarget by remember { mutableStateOf<User?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current

    banTarget?.let { user ->
        DeleteConfirmDialog(
            title   = if (user.isBanned) "Unban ${user.fullName}" else "Ban ${user.fullName}",
            message = if (user.isBanned) "Aktifkan kembali akun \"${user.username}\"?" else "Nonaktifkan akun \"${user.username}\"?",
            onConfirm = {
                val action = if (user.isBanned) "unban" else "ban"
                authViewModel.manageUser(action, user.id,
                    onSuccess = { banTarget = null },
                    onError = { android.widget.Toast.makeText(context, it, android.widget.Toast.LENGTH_SHORT).show() }
                )
            },
            onDismiss = { banTarget = null }
        )
    }

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 8.dp)) {
        if (members.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    if (authViewModel.isUsersLoading) {
                        CircularProgressIndicator(color = FishGreen, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Belum ada data pengguna.", color = TextHint, fontSize = 14.sp)
                    }
                }
            }
        } else {
            items(items = members, key = { it.id }) { user ->
                AdminUserRow(user = user, isBanned = user.isBanned, onBanToggle = { banTarget = user })
                HorizontalDivider(color = BorderColor, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Composable
private fun AdminUserRow(user: User, isBanned: Boolean, onBanToggle: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(44.dp).clip(CircleShape).background(if (isBanned) Color(0xFFBDBDBD) else FishGreenSurface),
            contentAlignment = Alignment.Center
        ) {
            Text(user.avatarInitial, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (isBanned) TextHint else FishGreenDark)
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(user.fullName ?: "No Name", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = if (isBanned) TextHint else TextPrimary, maxLines = 1)
                if (isBanned) {
                    Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(Color(0xFFFFCDD2)).padding(horizontal = 6.dp, vertical = 1.dp)) {
                        Text("Banned", fontSize = 9.sp, color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
                    }
                }
            }
            Text("@${user.username} · Member sejak ${user.memberSince}", fontSize = 11.sp, color = TextHint)
        }
        if (user.role != com.app.fishpoint.data.model.UserRole.ADMIN) {
            OutlinedButton(
                onClick = onBanToggle, modifier = Modifier.height(32.dp), shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = if (isBanned) FishGreen else Color(0xFFE53935)),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true), contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
            ) {
                Text(if (isBanned) "Unban" else "Ban", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun DeleteConfirmDialog(title: String, message: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = { Text(message, fontSize = 14.sp, color = TextSecondary) },
        confirmButton = {
            TextButton(onClick = onConfirm, colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFE53935))) {
                Text("Ya, Lanjutkan", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)) {
                Text("Batal")
            }
        }
    )
}