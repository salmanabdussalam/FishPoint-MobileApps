package com.app.fishpoint.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fishpoint.data.model.Review
import com.app.fishpoint.ui.components.SpotCoverImage
import com.app.fishpoint.ui.components.StarRatingDisplay
import com.app.fishpoint.ui.components.StarRatingInput
import com.app.fishpoint.ui.theme.*
import com.app.fishpoint.ui.viewmodel.SpotViewModel

@Composable
fun DetailSpotScreen(spotId: Int, currentUsername: String?, currentUserId: Int?, viewModel: SpotViewModel, onBackClick: () -> Unit, onProfilClick: () -> Unit, onRequireLogin: () -> Unit) {
    val spot = viewModel.spots.find { it.id == spotId } ?: return
    var showAddReview by remember { mutableStateOf(false) }
    var reviewText by remember { mutableStateOf("") }
    var selectedRating by remember { mutableIntStateOf(0) }

    Scaffold(containerColor = Color.White) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState())) {
            Row(modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.clickable(onClick = onBackClick)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = TextPrimary, modifier = Modifier.size(20.dp))
                    Text("Detail Spot", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }
                Icon(Icons.Default.AccountCircle, contentDescription = "Profil", tint = FishGreen, modifier = Modifier.size(30.dp).clickable(onClick = onProfilClick))
            }
            HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
            Spacer(Modifier.height(12.dp))

            val pagerState = androidx.compose.foundation.pager.rememberPagerState(pageCount = { if (spot.photos.isEmpty()) 1 else spot.photos.size })
            Box(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().height(190.dp).clip(RoundedCornerShape(10.dp))) {
                androidx.compose.foundation.pager.HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                    if (spot.photos.isEmpty()) {
                        SpotCoverImage(coverUrl = spot.coverPhotoUrl, modifier = Modifier.fillMaxSize())
                    } else {
                        val photo = spot.photos[page]
                        SpotCoverImage(coverUrl = photo.remoteUrl ?: photo.localUri, modifier = Modifier.fillMaxSize())
                    }
                }
                
                // Indikator halaman
                if (spot.photos.size > 1) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp)
                            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        repeat(spot.photos.size) { iteration ->
                            val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.5f)
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column {
                    Text(spot.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(Color(0xFFEEEEEE)).padding(horizontal = 8.dp, vertical = 3.dp)) { Text(spot.waterType ?: "", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium) }
                        if (!spot.targetFish.isNullOrBlank()) { Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(Color(0xFFEEEEEE)).padding(horizontal = 8.dp, vertical = 3.dp)) { Text(spot.targetFish, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium) } }
                    }
                    Spacer(Modifier.height(6.dp))
                    StarRatingDisplay(rating = spot.averageRating, reviewCount = spot.reviewCount, starSize = 14.dp)
                }
            }
            Spacer(Modifier.height(12.dp))

            val context = LocalContext.current
            Button(onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("geo:${spot.latitude},${spot.longitude}?q=${spot.latitude},${spot.longitude}(${Uri.encode(spot.name)})"))) }, modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().height(44.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = FishGreen)) {
                Icon(Icons.Default.Map, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Arahkan ke lokasi (Google Maps)", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = BorderColor)
            Spacer(Modifier.height(12.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("DESKRIPSI", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary, letterSpacing = 1.sp)
                Spacer(Modifier.height(8.dp))
                Text(spot.description?.ifBlank { "Belum ada deskripsi." } ?: "Belum ada deskripsi.", fontSize = 14.sp, color = TextSecondary, lineHeight = 22.sp)
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = BorderColor)
            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("ULASAN", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary, letterSpacing = 1.sp)
                OutlinedButton(onClick = { if (currentUsername == null) onRequireLogin() else showAddReview = !showAddReview }, shape = RoundedCornerShape(6.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp), modifier = Modifier.height(32.dp)) { Text(if (currentUsername == null) "Login untuk Ulasan" else "Tambah Ulasan", fontSize = 12.sp) }
            }

            if (showAddReview && currentUsername != null) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text("Beri Rating", fontSize = 12.sp, color = TextSecondary)
                    Spacer(Modifier.height(4.dp))
                    StarRatingInput(selectedRating = selectedRating, onRatingChange = { selectedRating = it })
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = reviewText, onValueChange = { reviewText = it }, placeholder = { Text("Tulis ulasan kamu...", color = TextHint, fontSize = 13.sp) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FishGreen, unfocusedBorderColor = BorderColor), maxLines = 3)
                    Spacer(Modifier.height(6.dp))
                    Button(onClick = { if (reviewText.isNotBlank() && selectedRating > 0 && currentUserId != null) { viewModel.addReview(spotId = spotId, userId = currentUserId, rating = selectedRating, comment = reviewText, onSuccess = { reviewText = ""; selectedRating = 0; showAddReview = false }, onError = { }) } }, shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = FishGreen), modifier = Modifier.align(Alignment.End), enabled = reviewText.isNotBlank() && selectedRating > 0) { Text("Kirim", fontSize = 13.sp, color = Color.White) }
                }
            }

            Spacer(Modifier.height(8.dp))

            if (spot.reviews.isEmpty()) {
                Text("Belum ada ulasan untuk spot ini.", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), fontSize = 13.sp, color = TextHint)
            } else {
                spot.reviews.forEach { review ->
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF333333)), contentAlignment = Alignment.Center) { Text(review.avatarInitial, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(review.username ?: "Unknown", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                Row { repeat(5) { i -> Icon(if (i < review.rating) Icons.Default.Star else Icons.Default.StarBorder, contentDescription = null, tint = RatingYellow, modifier = Modifier.size(12.dp)) } }
                            }
                            Spacer(Modifier.height(3.dp))
                            Text("\"${review.text}\"", fontSize = 13.sp, color = TextSecondary, lineHeight = 20.sp)
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = BorderColor, thickness = 0.5.dp)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
