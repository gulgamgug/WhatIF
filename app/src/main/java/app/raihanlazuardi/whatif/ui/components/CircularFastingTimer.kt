package app.raihanlazuardi.whatif.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.raihanlazuardi.whatif.data.model.FastingPhase
import app.raihanlazuardi.whatif.data.model.FastingPhaseStatus
import app.raihanlazuardi.whatif.ui.theme.Emerald400
import app.raihanlazuardi.whatif.ui.theme.Emerald500
import app.raihanlazuardi.whatif.ui.theme.Emerald600
import app.raihanlazuardi.whatif.ui.theme.Emerald700
import app.raihanlazuardi.whatif.ui.theme.GoogleSansFlexFontFamily
import app.raihanlazuardi.whatif.ui.theme.Indigo500
import app.raihanlazuardi.whatif.ui.theme.Indigo700
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CircularFastingTimer(
    status: FastingPhaseStatus,
    modifier: Modifier = Modifier,
    size: Dp = 270.dp,
    strokeWidth: Dp = 20.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = status.progress,
        animationSpec = tween(durationMillis = 600),
        label = "TimerProgress"
    )

    val isFasting = status.phase == FastingPhase.FASTING

    val primaryColor by animateColorAsState(
        targetValue = if (isFasting) Indigo500 else Emerald500,
        label = "PrimaryColor"
    )

    val gradientColors = if (isFasting) {
        listOf(Indigo700, Indigo500, Color(0xFF818CF8))
    } else {
        listOf(Emerald700, Emerald500, Emerald400)
    }

    val trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            val arcSize = Size(size.toPx() - strokePx, size.toPx() - strokePx)
            val topLeft = Offset(strokePx / 2, strokePx / 2)

            // Background Track
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Progress Arc
            val sweepAngle = 360f * animatedProgress
            drawArc(
                brush = Brush.sweepGradient(
                    colors = gradientColors,
                    center = Offset(size.toPx() / 2, size.toPx() / 2)
                ),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Indicator bead at the tip of progress arc
            if (sweepAngle > 1f) {
                val angleRad = Math.toRadians((sweepAngle - 90.0))
                val radius = (size.toPx() - strokePx) / 2
                val beadCenter = Offset(
                    x = (size.toPx() / 2 + radius * cos(angleRad)).toFloat(),
                    y = (size.toPx() / 2 + radius * sin(angleRad)).toFloat()
                )
                drawCircle(
                    color = Color.White,
                    radius = strokePx * 0.45f,
                    center = beadCenter
                )
            }
        }

        // Inner typography content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isFasting) "🌙 PUASA" else "🥗 MAKAN",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                ),
                color = primaryColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = String.format("%02dj %02dm", status.hoursRemaining, status.minutesRemaining),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontFamily = GoogleSansFlexFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 38.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = String.format("%02d detik lagi", status.secondsOfMinuteRemaining),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isFasting) "Menuju Jendela Makan" else "Menuju Waktu Puasa",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${(status.progress * 100).toInt()}% Selesai",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = primaryColor
            )
        }
    }
}
