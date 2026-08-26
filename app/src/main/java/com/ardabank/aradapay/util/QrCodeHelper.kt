package com.ardabank.aradapay.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.EnumMap

data class QrUserData(
    val userId: String,
    val username: String,
    val fullName: String,
    val tag: String,
    val iban: String
)

object QrCodeHelper {

    fun createQrPayload(user: QrUserData): String {
        return "aradapay://user?id=${user.userId}&username=${user.username}&tag=${user.tag.replace("#", "%23")}&name=${user.fullName.replace(" ", "+")}&iban=${user.iban.replace(" ", "")}"
    }

    fun parseQrPayload(payload: String): QrUserData? {
        return try {
            val clean = payload.trim()
            if (clean.startsWith("aradapay://user")) {
                val queryParams = clean.substringAfter("?").split("&").associate {
                    val parts = it.split("=")
                    val key = parts.getOrNull(0) ?: ""
                    val value = parts.getOrNull(1)?.replace("%23", "#")?.replace("+", " ") ?: ""
                    key to value
                }

                val id = queryParams["id"] ?: System.currentTimeMillis().toString()
                val username = queryParams["username"] ?: "user"
                val tag = queryParams["tag"] ?: "$username#1453"
                val name = queryParams["name"] ?: username.replaceFirstChar { it.uppercase() }
                val iban = queryParams["iban"] ?: "TR64 0006 2000 0000 1122 3344 55"

                QrUserData(
                    userId = id,
                    username = username,
                    fullName = name,
                    tag = tag,
                    iban = iban
                )
            } else if (clean.contains("#") || clean.isNotBlank()) {
                val tag = if (clean.contains("#")) clean else "$clean#${(1000..9999).random()}"
                val name = tag.split("#").first().replaceFirstChar { it.uppercase() }
                val username = name.lowercase()
                QrUserData(
                    userId = System.currentTimeMillis().toString(),
                    username = username,
                    fullName = name,
                    tag = tag,
                    iban = "TR64 0006 2000 0000 " + (1000..9999).random() + " " + (1000..9999).random() + " 99"
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun generateQrBitmap(
        content: String,
        size: Int = 512,
        foregroundColor: Int = Color.parseColor("#0F172A"),
        backgroundColor: Int = Color.WHITE
    ): Bitmap? {
        return try {
            val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
                put(EncodeHintType.CHARACTER_SET, "UTF-8")
                put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H)
                put(EncodeHintType.MARGIN, 1)
            }

            val bitMatrix = QRCodeWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                size,
                size,
                hints
            )

            val width = bitMatrix.width
            val height = bitMatrix.height
            val pixels = IntArray(width * height)

            for (y in 0 until height) {
                val offset = y * width
                for (x in 0 until width) {
                    pixels[offset + x] = if (bitMatrix.get(x, y)) foregroundColor else backgroundColor
                }
            }

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
