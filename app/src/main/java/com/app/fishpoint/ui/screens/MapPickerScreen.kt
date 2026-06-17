package com.app.fishpoint.ui.screens

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.app.fishpoint.ui.theme.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

@Composable
fun MapPickerScreen(initialLat: Double, initialLng: Double, onLocationPicked: (lat: Double, lng: Double) -> Unit, onBackClick: () -> Unit) {
    var centerLat by remember { mutableDoubleStateOf(initialLat) }
    var centerLng by remember { mutableDoubleStateOf(initialLng) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = TextPrimary, modifier = Modifier.size(20.dp).clickable(onClick = onBackClick))
            Text("Pilih Lokasi di Peta", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        }
        HorizontalDivider(color = BorderColor, thickness = 0.5.dp)

        Box(modifier = Modifier.weight(1f)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    Configuration.getInstance().userAgentValue = ctx.packageName
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(15.0)
                        controller.setCenter(GeoPoint(initialLat, initialLng))
                        addOnFirstLayoutListener { _, _, _, _, _ -> centerLat = mapCenter.latitude; centerLng = mapCenter.longitude }
                        setOnTouchListener { _, event -> if (event.action == MotionEvent.ACTION_UP) { centerLat = mapCenter.latitude; centerLng = mapCenter.longitude }; false }
                    }
                },
                update = { mapView -> mapView.onResume() }
            )

            Icon(Icons.Default.LocationOn, contentDescription = "Titik lokasi terpilih", tint = FishGreen, modifier = Modifier.align(Alignment.Center).size(40.dp).padding(bottom = 20.dp))

            Box(modifier = Modifier.align(Alignment.TopCenter).padding(top = 12.dp).background(Color.White, RoundedCornerShape(8.dp)).padding(horizontal = 14.dp, vertical = 8.dp)) {
                Text("Lat: ${"%.6f".format(centerLat)}   Lng: ${"%.6f".format(centerLng)}", fontSize = 12.sp, color = TextSecondary, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
            }
        }

        Box(modifier = Modifier.fillMaxWidth().background(Color.White).padding(16.dp)) {
            Button(onClick = { onLocationPicked(centerLat, centerLng) }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = FishGreen)) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Pilih Lokasi Ini", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }
    }
}