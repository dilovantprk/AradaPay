package com.ardabank.aradapay.domain.model

enum class Currency {
    TRY, USD, EUR
}

data class User(
    val id: String = "",
    val email: String = "",
    val username: String = "",
    val fullName: String = "",
    val avatarUrl: String = "",
    val phone: String? = null,
    val iban: String? = null,
    val tag: String? = null, // Örn: 'Kaan#5674' / 'Arda#1453'
    val defaultCurrency: Currency = Currency.TRY,
    val createdAt: String = ""
)
