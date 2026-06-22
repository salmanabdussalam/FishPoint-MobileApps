package com.app.fishpoint.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fishpoint.navigation.Screen
import com.app.fishpoint.ui.components.*
import com.app.fishpoint.ui.theme.*
import com.app.fishpoint.ui.viewmodel.SpotViewModel

@Composable
fun BerandaScreen(isGuest: Boolean, viewModel: SpotViewModel, onSpotClick: (Int) -> Unit, onTambahClick: () -> Unit, onProfilClick: () -> Unit) {
    LaunchedEffect(Unit) {
        if (!viewModel.isLoading) {
            viewModel.fetchSpots()
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    val allSpots = viewModel.spots
    val filteredSpots = remember(searchQuery, allSpots) {
        if (searchQuery.isBlank()) allSpots else allSpots.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(
        bottomBar = { FishPointBottomBar(currentRoute = Screen.Beranda.route, onBeranda = {}, onTambah = onTambahClick, onProfil = onProfilClick, showAddButton = !isGuest) },
        containerColor = PageBackground
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(innerPadding), contentPadding = PaddingValues(bottom = 16.dp)) {
            item {
                Row(modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    FishPointLogo(size = 0.55f)
                    Text("FishPoint", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    if (isGuest) {
                        OutlinedButton(onClick = onProfilClick, shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = FishGreen), contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp), modifier = Modifier.height(32.dp)) { Text("Masuk", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                    } else {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profil", tint = FishGreen, modifier = Modifier.size(32.dp).clickable(onClick = onProfilClick))
                    }
                }
                HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
            }

            item {
                Spacer(Modifier.height(12.dp))
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    OutlinedTextField(value = searchQuery, onValueChange = { searchQuery = it }, placeholder = { Text("Cari spot (ex: Rawa Pening)...", color = TextHint, fontSize = 14.sp) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FishGreen, unfocusedBorderColor = BorderColor, unfocusedContainerColor = Color.White, focusedContainerColor = Color.White), leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextHint) }, singleLine = true)
                }
                Spacer(Modifier.height(12.dp))
            }

            item {
                SpotsOverviewMap(spots = filteredSpots, onSpotClick = onSpotClick, modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().height(200.dp).clip(RoundedCornerShape(10.dp)))
                Spacer(Modifier.height(16.dp))
            }

            item {
                Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Anchor, contentDescription = null, tint = FishGreen, modifier = Modifier.size(18.dp))
                    Text("Spot Terpopuler", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Spacer(Modifier.height(8.dp))
            }

            items(items = filteredSpots, key = { it.id }) { spot ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) { SpotCard(spot = spot, onClick = { onSpotClick(spot.id) }) }
            }

            if (filteredSpots.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) { Text("Spot tidak ditemukan", color = TextSecondary, fontSize = 14.sp) }
                }
            }
        }
    }
}
