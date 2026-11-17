package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.ui.theme.WaterFillBottom
import com.example.ui.theme.WaterFillTop
import com.example.ui.theme.WaterGlassOutline
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun WaterGlassGraphic(
  waterCups: Int,
  goalCups: Int,
  contentDescriptionText: String,
  modifier: Modifier = Modifier,
) {
  val targetRatio =
    when {
      waterCups <= 0 -> 0f
      waterCups >= goalCups -> 1f
      else -> (0.12f + (waterCups.toFloat() / goalCups.toFloat()) * 0.88f).coerceIn(0f, 1f)
    }

  val animatedLevel by
    animateFloatAsState(
      targetValue = targetRatio,
      animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
      label = "waterLevelAnimation",
    )

  val infiniteTransition = rememberInfiniteTransition(label = "waterWave")
  val wavePhase by
    infiniteTransition.animateFloat(
      initialValue = 0f,
      targetValue = (2f * PI).toFloat(),
      animationSpec =
        infiniteRepeatable(
          animation = tween(durationMillis = 2400, easing = LinearEasing),
          repeatMode = RepeatMode.Restart,
        ),
      label = "wavePhase",
    )

  Canvas(
    modifier =
      modifier
        .size(width = 96.dp, height = 114.dp)
        .semantics { contentDescription = contentDescriptionText }
  ) {
    val w = size.width
    val h = size.height
    val strokeWidth = 4.5.dp.toPx()

    val topInset = w * 0.10f
    val bottomInset = w * 0.21f
    val topY = h * 0.08f
    val bottomY = h * 0.92f
    val cornerRadius = 10.dp.toPx()

    // Glass interior shape (tapered drinking cup)
    val glassPath =
      Path().apply {
        moveTo(topInset, topY)
        lineTo(w - topInset, topY)
        lineTo(w - bottomInset, bottomY - cornerRadius)
        quadraticTo(w - bottomInset, bottomY, w - bottomInset - cornerRadius, bottomY)
        lineTo(bottomInset + cornerRadius, bottomY)
        quadraticTo(bottomInset, bottomY, bottomInset, bottomY - cornerRadius)
        close()
      }

    // Subtle glass backdrop fill
    drawPath(path = glassPath, color = Color.White.copy(alpha = 0.85f))

    // Draw animated water inside glass bounds
    if (animatedLevel > 0.005f) {
      clipPath(glassPath) {
        val usableHeight = bottomY - topY - 4.dp.toPx()
        val waterSurfaceY = bottomY - (usableHeight * animatedLevel)
        val waveAmplitude = if (animatedLevel >= 0.99f) 2.dp.toPx() else 3.5.dp.toPx()

        val waterPath =
          Path().apply {
            moveTo(0f, bottomY + 10f)
            lineTo(0f, waterSurfaceY)
            val steps = 24
            for (i in 0..steps) {
              val x = (w / steps) * i
              val angle = (i.toFloat() / steps) * (2f * PI).toFloat() + wavePhase
              val y = waterSurfaceY + sin(angle) * waveAmplitude
              lineTo(x, y)
            }
            lineTo(w, bottomY + 10f)
            close()
          }

        drawPath(
          path = waterPath,
          brush =
            Brush.verticalGradient(
              colors = listOf(WaterFillTop, WaterFillBottom),
              startY = waterSurfaceY,
              endY = bottomY,
            ),
        )

        // Bright water surface highlight line
        val highlightPath =
          Path().apply {
            val steps = 24
            for (i in 0..steps) {
              val x = (w / steps) * i
              val angle = (i.toFloat() / steps) * (2f * PI).toFloat() + wavePhase
              val y = waterSurfaceY + sin(angle) * waveAmplitude
              if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
          }
        drawPath(
          path = highlightPath,
          color = Color.White.copy(alpha = 0.55f),
          style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round),
        )
      }
    }

    // Measurement ticks along the left wall
    val tickCount = 3
    for (i in 1..tickCount) {
      val fraction = i / (tickCount + 1f)
      val tickY = bottomY - (bottomY - topY) * fraction
      val wallX = bottomInset + (topInset - bottomInset) * fraction
      drawLine(
        color = WaterGlassOutline.copy(alpha = 0.45f),
        start = Offset(wallX + 6.dp.toPx(), tickY),
        end = Offset(wallX + 14.dp.toPx(), tickY),
        strokeWidth = 2.dp.toPx(),
        cap = StrokeCap.Round,
      )
    }

    // Specular reflection highlight on right side of glass
    drawLine(
      color = Color.White.copy(alpha = 0.65f),
      start = Offset(w - topInset - 11.dp.toPx(), topY + 14.dp.toPx()),
      end = Offset(w - bottomInset - 8.dp.toPx(), bottomY - 14.dp.toPx()),
      strokeWidth = 3.dp.toPx(),
      cap = StrokeCap.Round,
    )

    // Bold glass outline
    drawPath(
      path = glassPath,
      color = WaterGlassOutline,
      style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round),
    )
  }
}
