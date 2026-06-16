package com.app.fishpoint.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.app.fishpoint.data.model.SpotPhoto
import com.app.fishpoint.ui.theme.*

class SpotFormState(
    nama: String = "", jenisAir: String = JENIS_AIR_OPTIONS.first(), lat: Double = -7.5755, lng: Double = 110.8243, targetIkan: String = "", deskripsi: String = "", photos: List<SpotPhoto> = emptyList(),
) {
    var namaSpot by mutableStateOf(nama)
    var jenisAir by mutableStateOf(jenisAir)
    var latitude by mutableDoubleStateOf(lat)
    var longitude by mutableDoubleStateOf(lng)
    var targetIkan by mutableStateOf(targetIkan)
    var deskripsi by mutableStateOf(deskripsi)
    var photos by mutableStateOf(photos)
    var namaErrorMessage by mutableStateOf("")

    fun validate(): Boolean {
        namaErrorMessage = if (namaSpot.isBlank()) "Nama spot tidak boleh kosong" else ""
        return namaErrorMessage.isEmpty()
    }
    companion object { val JENIS_AIR_OPTIONS = listOf("Air Tawar (Sungai)", "Air Tawar (Waduk)", "Air Tawar (Danau)", "Air Laut", "Air Payau") }
}

@Composable
fun rememberSpotFormState(
    nama: String = "", jenisAir: String = SpotFormState.JENIS_AIR_OPTIONS.first(), lat: Double = -7.5755, lng: Double = 110.8243, targetIkan: String = "", deskripsi: String = "", photos: List<SpotPhoto> = emptyList(),
): SpotFormState {
    return rememberSaveable(
        saver = listSaver(
            save = { listOf(it.namaSpot, it.jenisAir, it.latitude, it.longitude, it.targetIkan, it.deskripsi) },
            restore = { SpotFormState(nama = it[0] as String, jenisAir = it[1] as String, lat = it[2] as Double, lng = it[3] as Double, targetIkan = it[4] as String, deskripsi = it[5] as String, photos = photos) }
        )
    ) { SpotFormState(nama, jenisAir, lat, lng, targetIkan, deskripsi, photos) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpotFormFields(state: SpotFormState, onOpenMapPicker: () -> Unit, onPickPhotos: () -> Unit, onRemovePhoto: (SpotPhoto) -> Unit) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    LabeledField(label = "Nama Spot") {
        OutlinedTextField(value = state.namaSpot, onValueChange = { state.namaSpot = it; state.namaErrorMessage = "" }, placeholder = { Text("Masukkan nama spot memancing...", color = TextHint, fontSize = 13.sp) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = fishPointTextFieldColors(), singleLine = true, isError = state.namaErrorMessage.isNotEmpty(), supportingText = if (state.namaErrorMessage.isNotEmpty()) { { Text(state.namaErrorMessage, color = ErrorRed, fontSize = 12.sp) } } else null)
    }
    Spacer(Modifier.height(14.dp))
    LabeledField(label = "Jenis Air") {
        ExposedDropdownMenuBox(expanded = dropdownExpanded, onExpandedChange = { dropdownExpanded = it }) {
            OutlinedTextField(value = state.jenisAir, onValueChange = {}, readOnly = true, modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable), shape = RoundedCornerShape(8.dp), colors = fishPointTextFieldColors(), trailingIcon = { Icon(if (dropdownExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextSecondary) })
            ExposedDropdownMenu(expanded = dropdownExpanded, onDismissRequest = { dropdownExpanded = false }) {
                SpotFormState.JENIS_AIR_OPTIONS.forEach { option -> DropdownMenuItem(text = { Text(option, fontSize = 14.sp) }, onClick = { state.jenisAir = option; dropdownExpanded = false }) }
            }
        }
    }
    Spacer(Modifier.height(14.dp))
    LabeledField(label = "Tentukan Koordinat GPS") {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Lat: ${"%.6f".format(state.latitude)}", fontSize = 12.sp, color = TextSecondary)
                Text("Lng: ${"%.6f".format(state.longitude)}", fontSize = 12.sp, color = TextSecondary)
            }
            OutlinedButton(onClick = onOpenMapPicker, shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = FishGreen)) {
                Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Pilih di Peta", fontSize = 12.sp)
            }
        }
    }
    Spacer(Modifier.height(14.dp))
    LabeledField(label = "Target Ikan Utama") {
        OutlinedTextField(value = state.targetIkan, onValueChange = { state.targetIkan = it }, placeholder = { Text("Contoh: Mujair...", color = TextHint, fontSize = 13.sp) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = fishPointTextFieldColors(), singleLine = true)
    }
    Spacer(Modifier.height(14.dp))
    LabeledField(label = "Deskripsi") {
        OutlinedTextField(value = state.deskripsi, onValueChange = { state.deskripsi = it }, placeholder = { Text("Ceritakan kondisi jalan, arus, dan jam terbaik.", color = TextHint, fontSize = 13.sp) }, modifier = Modifier.fillMaxWidth().height(90.dp), shape = RoundedCornerShape(8.dp), colors = fishPointTextFieldColors(), maxLines = 4)
    }
    Spacer(Modifier.height(14.dp))
    LabeledField(label = "Foto Spot (opsional)") {
        Text("Foto pertama akan jadi cover spot. Jika tidak diisi, akan ditampilkan logo FishPoint sebagai cover.", fontSize = 11.sp, color = TextHint, modifier = Modifier.padding(bottom = 6.dp))
        if (state.photos.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(8.dp)).border(1.5.dp, BorderColor, RoundedCornerShape(8.dp)).background(Color(0xFFF9F9F9)).clickable(onClick = onPickPhotos), contentAlignment = Alignment.Center) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(24.dp))
                    Text("Ketuk untuk pilih foto", fontSize = 13.sp, color = TextSecondary)
                }
            }
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items = state.photos, key = { it.id }) { photo ->
                    Box(modifier = Modifier.size(80.dp)) {
                        AsyncImage(model = photo.localUri ?: photo.remoteUrl, contentDescription = null, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)).border(1.dp, BorderColor, RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                        if (photo.sortOrder == 0) {
                            Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().background(FishGreen.copy(alpha = 0.85f), RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)).padding(vertical = 2.dp), contentAlignment = Alignment.Center) {
                                Text("Cover", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        Icon(Icons.Default.Close, contentDescription = "Hapus foto", tint = Color.White, modifier = Modifier.align(Alignment.TopEnd).padding(2.dp).size(18.dp).clip(RoundedCornerShape(50)).background(Color(0x99000000)).clickable { onRemovePhoto(photo) }.padding(2.dp))
                    }
                }
                item {
                    Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).border(1.5.dp, BorderColor, RoundedCornerShape(8.dp)).background(Color(0xFFF9F9F9)).clickable(onClick = onPickPhotos), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah foto", tint = TextSecondary, modifier = Modifier.size(24.dp))
                    }
                }
            }
        }
    }
}