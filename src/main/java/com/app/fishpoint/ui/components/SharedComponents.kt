package com.app.fishpoint.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.app.fishpoint.R
import com.app.fishpoint.data.model.FishingSpot
import com.app.fishpoint.navigation.Screen
import com.app.fishpoint.ui.theme.*

@Composable
fun fishPointTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = FishGreen,
    unfocusedBorderColor = BorderColor,
    unfocusedContainerColor = Color.White,
    focusedContainerColor = Color.White,
)

@Composable
fun LabeledField(label: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
        Spacer(Modifier.height(5.dp))
        content()
    }
}

@Composable
fun FishPointLogo(size: Float = 1f) {
    Box(
        modifier = Modifier
            .size((64 * size).dp)
            .clip(CircleShape)
            .border(2.dp, BorderColor, CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = R.drawable.img_logo_utama,
            contentDescription = "FishPoint Logo",
            modifier = Modifier.size((40 * size).dp),
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
fun SpotCoverImage(coverUrl: String?, modifier: Modifier = Modifier, contentDescription: String? = null) {
    if (!coverUrl.isNullOrBlank()) {
        AsyncImage(
            model = coverUrl, contentDescription = contentDescription, modifier = modifier,
            contentScale = ContentScale.Crop,
            error = painterResource(id = R.drawable.img_placeholder_spot),
            placeholder = painterResource(id = R.drawable.img_placeholder_spot),
        )
    } else {
        AsyncImage(
            model = R.drawable.img_placeholder_spot, contentDescription = contentDescription,
            modifier = modifier, contentScale = ContentScale.Crop,
        )
    }
}

@Composable
fun StarRatingDisplay(rating: Double?, reviewCount: Int, starSize: androidx.compose.ui.unit.Dp = 12.dp) {
    if (rating == null) {
        Text("Belum ada rating", fontSize = 11.sp, color = TextHint)
        return
    }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(Icons.Default.Star, contentDescription = null, tint = RatingYellow, modifier = Modifier.size(starSize))
        Text("%.1f".format(rating), fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
        Text("· $reviewCount Ulasan", fontSize = 11.sp, color = TextSecondary)
    }
}

@Composable
fun StarRatingInput(selectedRating: Int, onRatingChange: (Int) -> Unit, starSize: androidx.compose.ui.unit.Dp = 28.dp) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        for (star in 1..5) {
            Icon(
                imageVector = if (star <= selectedRating) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = "Beri rating $star bintang",
                tint = if (star <= selectedRating) RatingYellow else TextHint,
                modifier = Modifier.size(starSize).clickable { onRatingChange(star) }
            )
        }
    }
}

@Composable
fun FishPointBottomBar(currentRoute: String, onBeranda: () -> Unit, onTambah: () -> Unit, onProfil: () -> Unit, showAddButton: Boolean = true) {
    val isBerandaSelected = currentRoute == Screen.Beranda.route
    val isProfilSelected = currentRoute == Screen.Profil.route

    Box(
        modifier = Modifier.fillMaxWidth().background(NavBackground)
            .border(0.5.dp, BorderColor, shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp))
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(64.dp), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onBeranda).padding(8.dp)) {
                Icon(Icons.Default.Home, contentDescription = "Beranda", tint = if (isBerandaSelected) NavSelected else NavUnselected, modifier = Modifier.size(24.dp))
                Text("Beranda", fontSize = 11.sp, color = if (isBerandaSelected) NavSelected else NavUnselected, fontWeight = FontWeight.Medium)
            }
            if (showAddButton) {
                FloatingActionButton(onClick = onTambah, containerColor = FabBackground, shape = CircleShape, modifier = Modifier.size(52.dp), elevation = FloatingActionButtonDefaults.elevation(4.dp)) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Spot", tint = Color.White)
                }
            } else { Spacer(Modifier.size(52.dp)) }
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onProfil).padding(8.dp)) {
                Icon(Icons.Default.Person, contentDescription = "Profil", tint = if (isProfilSelected) NavSelected else NavUnselected, modifier = Modifier.size(24.dp))
                Text("Profil", fontSize = 11.sp, color = if (isProfilSelected) NavSelected else NavUnselected, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun SpotCard(spot: FishingSpot, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.height(90.dp)) {
            SpotCoverImage(coverUrl = spot.coverPhotoUrl, contentDescription = spot.name, modifier = Modifier.width(90.dp).fillMaxHeight())
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 8.dp), verticalArrangement = Arrangement.SpaceBetween) {
                Text(spot.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary, maxLines = 1)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(12.dp))
                    Text("${"%.4f".format(spot.latitude)}, ${"%.4f".format(spot.longitude)}", fontSize = 11.sp, color = TextSecondary, maxLines = 1)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    SpotTag(spot.waterType ?: "")
                    if (!spot.category.isNullOrEmpty()) SpotTag(spot.category ?: "", isHighlight = spot.category == "Terpopuler")
                }
                StarRatingDisplay(rating = spot.averageRating, reviewCount = spot.reviewCount)
            }
        }
    }
}

@Composable
fun SpotTag(text: String, isHighlight: Boolean = false) {
    Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(if (isHighlight) FishGreenLight else Color(0xFFEEEEEE)).padding(horizontal = 6.dp, vertical = 2.dp)) {
        Text(text, fontSize = 10.sp, color = if (isHighlight) FishGreenDark else TextSecondary, fontWeight = FontWeight.Medium, maxLines = 1)
    }
}

fun spotPlaceholderColor(id: Int): Color = when (id % 4) {
    0 -> Color(0xFFE53935); 1 -> Color(0xFF43A047); 2 -> Color(0xFFFFB300); else -> Color(0xFF1E88E5)
}