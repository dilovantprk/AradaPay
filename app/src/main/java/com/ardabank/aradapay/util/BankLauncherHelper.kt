package com.ardabank.aradapay.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.ardabank.aradapay.domain.model.SupportedBank
import java.util.Locale

object BankLauncherHelper {

    /**
     * Checks if the specified banking application is installed on the device.
     */
    fun isBankInstalled(context: Context, packageName: String): Boolean {
        return try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            intent != null
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Returns the official application icon drawable if installed, or null otherwise.
     */
    fun getBankAppIcon(context: Context, packageName: String): android.graphics.drawable.Drawable? {
        return try {
            context.packageManager.getApplicationIcon(packageName)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Copies clean IBAN, formatted Amount and Transfer Note to clipboard.
     */
    fun copyTransferDetails(
        context: Context,
        iban: String,
        amount: Double = 0.0,
        note: String = "",
        recipientName: String = ""
    ) {
        val cleanIban = iban.replace(" ", "").trim()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("AradaPay FAST IBAN", cleanIban)
        clipboard.setPrimaryClip(clip)
    }

    /**
     * Launches the banking application or redirects to Google Play Store if not installed.
     */
    fun openBankOrStore(
        context: Context,
        bank: SupportedBank,
        iban: String,
        amount: Double,
        note: String,
        recipientName: String = ""
    ) {
        // 1. Copy IBAN to clipboard
        copyTransferDetails(context, iban, amount, note, recipientName)

        val cleanIban = iban.replace(" ", "").trim()
        val amountStr = if (amount > 0) " (${String.format(Locale.US, "%.2f", amount)} ₺)" else ""

        // 2. Check if installed
        val launchIntent = context.packageManager.getLaunchIntentForPackage(bank.packageName)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(launchIntent)
                Toast.makeText(
                    context,
                    "IBAN ($cleanIban)$amountStr kopyalandı! ${bank.shortName} açılıyor...",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "${bank.bankName} başlatılamadı: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            // Redirect to Google Play Store
            try {
                val storeIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=${bank.packageName}")
                ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                context.startActivity(storeIntent)
            } catch (e: Exception) {
                val webIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=${bank.packageName}")
                ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                context.startActivity(webIntent)
            }

            Toast.makeText(
                context,
                "IBAN kopyalandı. ${bank.shortName} cihazda yüklü değil, Play Store açılıyor.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
