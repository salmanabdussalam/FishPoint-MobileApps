package com.app.fishpoint.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fishpoint.data.model.SpotPhoto
import com.app.fishpoint.ui.components.SpotFormFields
import com.app.fishpoint.ui.components.rememberSpotFormState
import com.app.fishpoint.ui.theme.*

@Composable
fun TambahSpotScreen(userId: Int, ownerUsername: String, viewModel: com.app.fishpoint.ui.viewmodel.SpotViewModel, mapPickerResult: Pair<Double, Double>?, onBackClick: () -> Unit, onSimpanSuccess: () -> Unit, onOpenMapPicker: (lat: Double, lng: Double) -> Unit) {
    val formState = rememberSpotFormState()

    LaunchedEffect(mapPickerResult) {
        mapPickerResult?.let { (lat, lng) -> formState.latitude = lat; formState.longitude = lng }
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(viewModel.errorMessage) {
        viewModel.errorMessage?.let { error -> android.widget.Toast.makeText(context, error, android.widget.Toast.LENGTH_SHORT).show() }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetMultipleContents()) { uris ->
        if (uris.isEmpty()) return@rememberLauncherForActivityResult
        val startOrder = formState.photos.size
        val newPhotos = uris.mapIndexed { index, uri -> SpotPhoto(id = (formState.photos.maxOfOrNull { it.id } ?: 0) + index + 1, sortOrder = startOrder + index, localUri = uri.toString()) }
        formState.photos = formState.photos + newPhotos
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = TextPrimary, modifier = Modifier.size(20.dp).clickable(onClick = onBackClick))
            Text("Tambah Spot baru", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        }
        HorizontalDivider(color = BorderColor, thickness = 0.5.dp)

        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 16.dp)) {
            SpotFormFields(state = formState, onOpenMapPicker = { onOpenMapPicker(formState.latitude, formState.longitude) }, onPickPhotos = { imagePickerLauncher.launch("image/*") }, onRemovePhoto = { photo -> formState.photos = formState.photos.filterNot { it.id == photo.id }.mapIndexed { idx, p -> p.copy(sortOrder = idx) } })
        }

        Box(modifier = Modifier.fillMaxWidth().background(Color.White).padding(16.dp)) {
            Button(onClick = { if (formState.validate()) { viewModel.addSpot(context = context, userId = userId, name = formState.namaSpot, waterType = formState.jenisAir, latitude = formState.latitude, longitude = formState.longitude, targetFish = formState.targetIkan, description = formState.deskripsi, photos = formState.photos.mapNotNull { it.localUri }, onSuccess = onSimpanSuccess) } }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = FishGreen)) {
                Text("Simpan Spot", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }
    }
}