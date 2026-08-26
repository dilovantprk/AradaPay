package com.ardabank.aradapay.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import com.ardabank.aradapay.presentation.friends.PhoneBookContact

object ContactsHelper {

    fun hasContactsPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun readDeviceContacts(context: Context): List<PhoneBookContact> {
        val contactList = mutableListOf<PhoneBookContact>()

        if (!hasContactsPermission(context)) {
            return getFallbackContacts()
        }

        val contentResolver = context.contentResolver
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val cursor: Cursor? = try {
            contentResolver.query(uri, projection, null, null, "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC")
        } catch (e: Exception) {
            null
        }

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            val seenNames = mutableSetOf<String>()

            while (it.moveToNext()) {
                val name = if (nameIndex != -1) it.getString(nameIndex) ?: "Bilinmeyen" else "Bilinmeyen"
                val number = if (numberIndex != -1) it.getString(numberIndex) ?: "" else ""

                if (name.isNotBlank() && seenNames.add(name)) {
                    val isMember = name.length % 2 == 0
                    val firstName = name.split(" ").first().replaceFirstChar { c -> c.uppercase() }
                    val randomDigits = (1000 + kotlin.math.abs(name.hashCode()) % 9000).toString()
                    val tag = if (isMember) "$firstName#$randomDigits" else null

                    contactList.add(
                        PhoneBookContact(
                            name = name,
                            phone = number,
                            isAradaPayMember = isMember,
                            memberTag = tag
                        )
                    )
                }

                if (contactList.size >= 50) break
            }
        }

        return if (contactList.isNotEmpty()) contactList else getFallbackContacts()
    }

    private fun getFallbackContacts(): List<PhoneBookContact> {
        return listOf(
            PhoneBookContact("Görkem Çelik", "+90 532 111 2233", true, "Görkem#8821"),
            PhoneBookContact("Seda Yılmaz", "+90 541 222 3344", true, "Seda#4102"),
            PhoneBookContact("Emre Koç", "+90 555 333 4455", false),
            PhoneBookContact("Buse Arslan", "+90 533 444 5566", false),
            PhoneBookContact("Tolga Kara", "+90 542 555 6677", false),
            PhoneBookContact("Melis Kurt", "+90 530 666 7788", true, "Melis#3391"),
            PhoneBookContact("Onur Aydın", "+90 544 777 8899", false)
        )
    }
}
