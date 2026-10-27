package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.data.model.DetectionBox
import com.example.ui.theme.AgriDangerRed
import com.example.ui.theme.AgriFreshGreen
import com.example.ui.theme.AgriWarningAmber

@Composable
fun ScanBoundingBoxOverlay(
    boxes: List<DetectionBox>,
    modifier: Modifier = Modifier
) {
    if (boxes.isEmpty()) return

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height

            boxes.forEach { box ->
                val left = box.left * canvasW
                val top = box.top * canvasH
                val right = box.right * canvasW
                val bottom = box.bottom * canvasH
                val width = right - left
                val height = bottom - top

                val boxColor = when (box.severity) {
                    "Critical", "High" -> AgriDangerRed
                    "Moderate" -> AgriWarningAmber
                    else -> AgriFreshGreen
                }

                // Draw bounding rectangle with dash
                drawRoundRect(
                    color = boxColor,
                    topLeft = Offset(left, top),
                    size = Size(width, height),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx()),
                    style = Stroke(
                        width = 3.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
                    )
                )

                // Draw targeting corner ticks
                val cornerLen = 16.dp.toPx()
                val tickWidth = 4.dp.toPx()

                // Top-Left
                drawLine(boxColor, Offset(left, top), Offset(left + cornerLen, top), tickWidth)
                drawLine(boxColor, Offset(left, top), Offset(left, top + cornerLen), tickWidth)

                // Top-Right
                drawLine(boxColor, Offset(right, top), Offset(right - cornerLen, top), tickWidth)
                drawLine(boxColor, Offset(right, top), Offset(right, top + cornerLen), tickWidth)

                // Bottom-Left
                drawLine(boxColor, Offset(left, bottom), Offset(left + cornerLen, bottom), tickWidth)
                drawLine(boxColor, Offset(left, bottom), Offset(left, bottom - cornerLen), tickWidth)

                // Bottom-Right
                drawLine(boxColor, Offset(right, bottom), Offset(right - cornerLen, bottom), tickWidth)
                drawLine(boxColor, Offset(right, bottom), Offset(right, bottom - cornerLen), tickWidth)

                // Center crosshair dot
                val centerX = left + width / 2
                val centerY = top + height / 2
                drawCircle(boxColor, radius = 4.dp.toPx(), center = Offset(centerX, centerY))
            }
        }
    }
}
