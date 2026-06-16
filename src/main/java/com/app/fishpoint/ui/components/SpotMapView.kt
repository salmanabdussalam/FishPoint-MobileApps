package com.app.fishpoint.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.app.fishpoint.data.model.FishingSpot
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun SpotsOverviewMap(
    spots: List<FishingSpot>,
    onSpotClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            Configuration.getInstance().userAgentValue = ctx.packageName
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(12.0)

                if (spots.isNotEmpty()) {
                    controller.setCenter(GeoPoint(spots.first().latitude, spots.first().longitude))
                }
            }
        },
        update = { mapView ->
            mapView.overlays.clear()

            spots.forEach { spot ->
                val marker = Marker(mapView).apply {
                    position = GeoPoint(spot.latitude, spot.longitude)
                    title = spot.name
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    setOnMarkerClickListener { _, _ ->
                        onSpotClick(spot.id)
                        true
                    }
                }
                mapView.overlays.add(marker)
            }
            mapView.invalidate()
            mapView.onResume()
        }
    )
}