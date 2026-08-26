package com.ardabank.aradapay.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Splitwise benzeri fasetli/poligonal zümrüt yeşili dağ motifli geometrik header.
 * Sayfaların üst kısmında derinlikli ve şık bir görünüm sunar.
 */
@Composable
fun GeometricHeaderBanner(
    modifier: Modifier = Modifier,
    height: Dp = 150.dp,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        // Poligonal fasetli dağlar arka plan çizimi
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. En arka koyu taban
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F5A3E),
                        Color(0xFF137350),
                        Color(0xFF1B8A62)
                    )
                )
            )

            // 2. Sol arka dağ faseti
            val pathBackLeft = Path().apply {
                moveTo(0f, h * 0.7f)
                lineTo(w * 0.32f, h * 0.12f)
                lineTo(w * 0.52f, h * 0.65f)
                lineTo(0f, h)
                close()
            }
            drawPath(pathBackLeft, color = Color(0xFF159666).copy(alpha = 0.85f))

            // 3. Orta arka zirve faseti (Açık mint/zümrüt)
            val pathCenterPeak = Path().apply {
                moveTo(w * 0.32f, h * 0.12f)
                lineTo(w * 0.65f, h * 0.22f)
                lineTo(w * 0.52f, h * 0.65f)
                close()
            }
            drawPath(pathCenterPeak, color = Color(0xFF34D399).copy(alpha = 0.55f))

            // 4. Sağ arka dağ faseti
            val pathBackRight = Path().apply {
                moveTo(w * 0.65f, h * 0.22f)
                lineTo(w, h * 0.35f)
                lineTo(w, h)
                lineTo(w * 0.52f, h * 0.65f)
                close()
            }
            drawPath(pathBackRight, color = Color(0xFF1D9F6D).copy(alpha = 0.9f))

            // 5. Sol ön kristal faset (Koyu tepe gölgesi)
            val pathFrontLeftDark = Path().apply {
                moveTo(0f, h * 0.28f)
                lineTo(w * 0.32f, h * 0.12f)
                lineTo(w * 0.18f, h * 0.65f)
                lineTo(0f, h * 0.75f)
                close()
            }
            drawPath(pathFrontLeftDark, color = Color(0xFF0A7B52).copy(alpha = 0.95f))

            // 6. Sol alt faset
            val pathFrontLeftMint = Path().apply {
                moveTo(0f, h * 0.75f)
                lineTo(w * 0.18f, h * 0.65f)
                lineTo(w * 0.38f, h * 0.95f)
                lineTo(0f, h)
                close()
            }
            drawPath(pathFrontLeftMint, color = Color(0xFF10B981).copy(alpha = 0.85f))

            // 7. Orta ön aydınlık faset (Açık pastel zümrüt)
            val pathCenterLight = Path().apply {
                moveTo(w * 0.32f, h * 0.12f)
                lineTo(w * 0.65f, h * 0.22f)
                lineTo(w * 0.48f, h)
                lineTo(w * 0.18f, h * 0.65f)
                close()
            }
            drawPath(pathCenterLight, color = Color(0xFFA7F3D0).copy(alpha = 0.45f))

            // 8. Sağ orta faset
            val pathRightFacet1 = Path().apply {
                moveTo(w * 0.65f, h * 0.22f)
                lineTo(w * 0.88f, h * 0.55f)
                lineTo(w * 0.62f, h)
                lineTo(w * 0.48f, h)
                close()
            }
            drawPath(pathRightFacet1, color = Color(0xFF159666).copy(alpha = 0.9f))

            // 9. Sağ alt köşe faseti
            val pathRightFacet2 = Path().apply {
                moveTo(w * 0.65f, h * 0.22f)
                lineTo(w, h * 0.35f)
                lineTo(w, h)
                lineTo(w * 0.88f, h * 0.55f)
                close()
            }
            drawPath(pathRightFacet2, color = Color(0xFF0F7650).copy(alpha = 0.8f))
        }

        // Top navigation bar overlay
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            navigationIcon?.invoke()
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
            actions?.invoke(this)
        }
    }
}
