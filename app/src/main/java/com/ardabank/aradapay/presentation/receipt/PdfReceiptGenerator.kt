package com.ardabank.aradapay.presentation.receipt

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.ardabank.aradapay.util.QrCodeHelper
import java.io.File
import java.io.FileOutputStream

object PdfReceiptGenerator {

    /**
     * Generates an official Turkish Banking Standard A4 PDF receipt.
     */
    fun generatePdfReceipt(context: Context, receipt: AradaPayReceipt): File {
        val pdfDocument = PdfDocument()
        val pageWidth = 595 // Standard A4 points at 72 DPI (595 x 842)
        val pageHeight = 842
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint().apply { isAntiAlias = true }

        // 1. Background
        canvas.drawColor(Color.WHITE)

        // 2. Header Emerald Accent Top Bar
        paint.color = Color.parseColor("#008542") // AradaPay Emerald
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 12f, paint)

        // 3. Header Bank & System Info
        paint.color = Color.parseColor("#0F172A")
        paint.textSize = 20f
        paint.isFakeBoldText = true
        canvas.drawText("AradaPay", 40f, 50f, paint)

        paint.textSize = 10f
        paint.isFakeBoldText = false
        paint.color = Color.parseColor("#64748B")
        canvas.drawText("ArdaBank A.Ş. • Dijital Ödeme & Hesap Hizmetleri", 40f, 65f, paint)
        canvas.drawText("AradaPay Akıllı Fitleşme Sistemi", 40f, 78f, paint)

        // Right Header (Document Title)
        paint.textAlign = Paint.Align.RIGHT
        paint.color = Color.parseColor("#008542")
        paint.textSize = 14f
        paint.isFakeBoldText = true
        canvas.drawText("ARADAPAY İŞLEM DEKONTU", (pageWidth - 40).toFloat(), 50f, paint)

        paint.color = Color.parseColor("#64748B")
        paint.textSize = 9f
        paint.isFakeBoldText = false
        canvas.drawText("SHA-256 Dijital Doğrulama Mührü", (pageWidth - 40).toFloat(), 65f, paint)
        canvas.drawText("Dekont Ref: ${receipt.referenceNo}", (pageWidth - 40).toFloat(), 78f, paint)

        // Reset Text Align
        paint.textAlign = Paint.Align.LEFT

        // Divider Line
        paint.color = Color.parseColor("#E2E8F0")
        paint.strokeWidth = 1f
        canvas.drawLine(40f, 95f, (pageWidth - 40).toFloat(), 95f, paint)

        // 4. Hero Summary Card (Amount & Status)
        val heroRect = RectF(40f, 110f, (pageWidth - 40).toFloat(), 190f)
        paint.color = Color.parseColor("#F8FAFC")
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(heroRect, 8f, 8f, paint)

        paint.color = Color.parseColor("#CBD5E1")
        paint.style = Paint.Style.STROKE
        canvas.drawRoundRect(heroRect, 8f, 8f, paint)
        paint.style = Paint.Style.FILL

        // Hero Content
        paint.color = Color.parseColor("#64748B")
        paint.textSize = 10f
        paint.isFakeBoldText = true
        canvas.drawText(receipt.type.title.uppercase(), 60f, 135f, paint)

        paint.color = Color.parseColor("#0F172A")
        paint.textSize = 24f
        paint.isFakeBoldText = true
        canvas.drawText("${String.format("%.2f", receipt.totalAmount)} TL", 60f, 168f, paint)

        // Status Badge on Right
        val statusRect = RectF((pageWidth - 170).toFloat(), 130f, (pageWidth - 60).toFloat(), 165f)
        paint.color = Color.parseColor("#D1FAE5")
        canvas.drawRoundRect(statusRect, 6f, 6f, paint)

        paint.color = Color.parseColor("#008542")
        paint.textSize = 11f
        paint.isFakeBoldText = true
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("İŞLEM ONAYLANDI", statusRect.centerX(), statusRect.centerY() + 4f, paint)
        paint.textAlign = Paint.Align.LEFT

        // 5. Transaction Key-Value Details Table
        var currentY = 220f
        paint.color = Color.parseColor("#0F172A")
        paint.textSize = 13f
        paint.isFakeBoldText = true
        canvas.drawText("İşlem Detayları", 40f, currentY, paint)

        currentY += 15f
        paint.color = Color.parseColor("#E2E8F0")
        canvas.drawLine(40f, currentY, (pageWidth - 40).toFloat(), currentY, paint)

        currentY += 20f

        val details = mutableListOf(
            Pair("İşlem / Harcama Tanımı", receipt.title),
            Pair("İşlem Referans No", receipt.referenceNo),
            Pair("İşlem Tarihi & Saati", "${receipt.date} ${receipt.time}"),
            Pair("Gönderen / Ödeyen", receipt.senderName),
            Pair("Alıcı / Muhatap", receipt.receiverName),
            Pair("Ödeme Kanalı", "TCMB FAST • 7/24 Anlık Fon Transferi"),
            Pair("Harcama Kategorisi", receipt.category),
            Pair("İşlem Ücreti & Komisyon", "0,00 TL (Ücretsiz / Muaf)")
        )

        receipt.senderIban?.let { details.add(3, Pair("Gönderen IBAN", it)) }
        receipt.receiverIban?.let { details.add(5, Pair("Alıcı IBAN", it)) }
        receipt.savingsAmount?.let { details.add(Pair("Borç Sadeleştirme Tasarrufu", "+ ${String.format("%.2f", it)} TL")) }
        receipt.note?.let { details.add(Pair("Açıklama / Not", it)) }

        details.forEachIndexed { index, (label, value) ->
            if (index % 2 == 0) {
                paint.color = Color.parseColor("#F8FAFC")
                canvas.drawRect(40f, currentY - 14f, (pageWidth - 40).toFloat(), currentY + 8f, paint)
            }

            paint.color = Color.parseColor("#64748B")
            paint.textSize = 10f
            paint.isFakeBoldText = false
            canvas.drawText(label, 50f, currentY, paint)

            paint.color = Color.parseColor("#0F172A")
            paint.textSize = 10f
            paint.isFakeBoldText = true
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText(value, (pageWidth - 50).toFloat(), currentY, paint)
            paint.textAlign = Paint.Align.LEFT

            currentY += 22f
        }

        // 6. Group Participants Breakdown (If Present)
        if (receipt.participants.isNotEmpty()) {
            currentY += 10f
            paint.color = Color.parseColor("#0F172A")
            paint.textSize = 13f
            paint.isFakeBoldText = true
            canvas.drawText("Katılımcı Payları & Bölüşüm Tablosu", 40f, currentY, paint)

            currentY += 15f
            paint.color = Color.parseColor("#E2E8F0")
            canvas.drawLine(40f, currentY, (pageWidth - 40).toFloat(), currentY, paint)

            currentY += 20f

            receipt.participants.forEach { p ->
                paint.color = Color.parseColor("#64748B")
                paint.textSize = 10f
                paint.isFakeBoldText = false
                canvas.drawText("${p.name} (${p.tag})", 50f, currentY, paint)

                val statusText = if (p.isPaid) "Tahsil Edildi (Ödendi)" else "Ödeme Bekleniyor"
                val statusColor = if (p.isPaid) Color.parseColor("#008542") else Color.parseColor("#DC2626")

                paint.color = statusColor
                paint.textSize = 10f
                paint.isFakeBoldText = true
                paint.textAlign = Paint.Align.RIGHT
                canvas.drawText("${String.format("%.2f", p.shareAmount)} TL • $statusText", (pageWidth - 50).toFloat(), currentY, paint)
                paint.textAlign = Paint.Align.LEFT

                currentY += 20f
            }
        }

        // 7. Security Seal & QR Code Footer Section
        val footerY = (pageHeight - 160).toFloat()
        paint.color = Color.parseColor("#F1F5F9")
        canvas.drawRect(40f, footerY, (pageWidth - 40).toFloat(), (pageHeight - 40).toFloat(), paint)

        paint.color = Color.parseColor("#CBD5E1")
        paint.style = Paint.Style.STROKE
        canvas.drawRect(40f, footerY, (pageWidth - 40).toFloat(), (pageHeight - 40).toFloat(), paint)
        paint.style = Paint.Style.FILL

        // QR Code
        val sealCode = ReceiptCryptographicVerifier.generateSealCode(receipt)
        val qrBitmap: Bitmap? = QrCodeHelper.generateQrBitmap(
            content = "https://aradapay.com/verify?ref=${receipt.referenceNo}&seal=$sealCode&amt=${receipt.totalAmount}",
            size = 200,
            foregroundColor = Color.parseColor("#0F172A"),
            backgroundColor = Color.WHITE
        )

        qrBitmap?.let {
            val qrDest = RectF(55f, footerY + 15f, 145f, footerY + 105f)
            canvas.drawBitmap(it, null, qrDest, paint)
        }

        // Security / Verification Text
        paint.color = Color.parseColor("#0F172A")
        paint.textSize = 10f
        paint.isFakeBoldText = true
        canvas.drawText("DİJİTAL DOĞRULAMA VE İŞLEM MÜHRÜ", 160f, footerY + 25f, paint)

        paint.color = Color.parseColor("#475569")
        paint.textSize = 8.5f
        paint.isFakeBoldText = false
        canvas.drawText("Güvenlik Doğrulama Kodu: $sealCode", 160f, footerY + 40f, paint)
        canvas.drawText("SHA-256 Dijital Özet: ${ReceiptCryptographicVerifier.generateSignature(receipt).take(32)}...", 160f, footerY + 52f, paint)
        canvas.drawText("Bu dekont AradaPay sistemi üzerinde güvenli dijital özet ile kaydedilmiştir.", 160f, footerY + 70f, paint)
        canvas.drawText("AradaPay (ArdaBank A.Ş.) Bilgi Teknolojileri ve Güvenlik Altyapısı", 160f, footerY + 82f, paint)
        canvas.drawText("Müşteri Hizmetleri: 0850 000 00 00 • www.aradapay.com", 160f, footerY + 95f, paint)

        pdfDocument.finishPage(page)

        // Save PDF to App Cache Directory
        val receiptsDir = File(context.cacheDir, "receipts").apply { mkdirs() }
        val pdfFile = File(receiptsDir, "AradaPay_Dekont_${receipt.referenceNo}.pdf")
        val outputStream = FileOutputStream(pdfFile)
        pdfDocument.writeTo(outputStream)
        outputStream.flush()
        outputStream.close()
        pdfDocument.close()

        return pdfFile
    }

    /**
     * Opens the generated PDF receipt using Android's native PDF viewer.
     */
    fun openPdfReceipt(context: Context, receipt: AradaPayReceipt) {
        try {
            val pdfFile = generatePdfReceipt(context, receipt)
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(intent, "Dekontu Görüntüle")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, "PDF dekont açılırken hata oluştu: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Shares the PDF receipt as a standard PDF attachment across Android apps (WhatsApp, Mail, Drive, etc.).
     */
    fun sharePdfReceipt(context: Context, receipt: AradaPayReceipt) {
        try {
            val pdfFile = generatePdfReceipt(context, receipt)
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "AradaPay Dekont - ${receipt.referenceNo}")
                putExtra(Intent.EXTRA_TEXT, "AradaPay İşlem Dekontu ektedir.\nİşlem: ${receipt.title}\nTutar: ${String.format("%.2f", receipt.totalAmount)} TL\nReferans: ${receipt.referenceNo}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "PDF Dekontu Paylaş")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, "PDF paylaşılırken hata oluştu: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}
