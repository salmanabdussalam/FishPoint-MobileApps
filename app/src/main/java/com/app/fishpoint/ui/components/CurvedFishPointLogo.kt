package com.app.fishpoint.ui.components

import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fishpoint.R

@Composable
fun CurvedFishPointLogo(modifier: Modifier = Modifier) {
    Box(contentAlignment = Alignment.Center, modifier = modifier.size(160.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.width / 3f
            val centerX = size.width / 2f
            val centerY = size.height / 2f

            drawCircle(
                color = Color.LightGray.copy(alpha = 0.6f),
                radius = radius,
                style = Stroke(width = 3.dp.toPx())
            )

            drawIntoCanvas { canvas ->
                val path = Path()
                path.addArc(
                    centerX - radius,
                    centerY - radius,
                    centerX + radius,
                    centerY + radius,
                    180f,
                    180f
                )

                val textToDraw = "FishPoint"
                val paint = Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 28.sp.toPx()
                    textAlign = Paint.Align.LEFT
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                    letterSpacing = 0.05f
                }

                val pathLength = (Math.PI * radius).toFloat()
                val textWidth = paint.measureText(textToDraw)

                val hOffset = (pathLength - textWidth) / 2f
                val vOffset = -25f

                canvas.nativeCanvas.drawTextOnPath(textToDraw, path, hOffset, vOffset, paint)
            }
        }
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = R.drawable.img_logo_utama),
                contentDescription = "Logo FishPoint",
                modifier = Modifier.size(72.dp)
            )
        }
    }
}