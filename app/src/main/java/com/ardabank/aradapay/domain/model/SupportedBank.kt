package com.ardabank.aradapay.domain.model

enum class SupportedBank(
    val bankName: String,
    val shortName: String,
    val packageName: String,
    val primaryColorHex: Long,
    val iconInitials: String
) {
    GARANTI(
        bankName = "Garanti BBVA",
        shortName = "Garanti",
        packageName = "com.garanti.cepsubesi",
        primaryColorHex = 0xFF008542,
        iconInitials = "GB"
    ),
    IS_BANKASI(
        bankName = "İş Bankası (İşCep)",
        shortName = "İş Bankası",
        packageName = "com.isbank.iscep",
        primaryColorHex = 0xFF003D7C,
        iconInitials = "İŞ"
    ),
    AKBANK(
        bankName = "Akbank Direkt",
        shortName = "Akbank",
        packageName = "com.akbank.android.apps.akbank_direkt",
        primaryColorHex = 0xFFE30613,
        iconInitials = "AK"
    ),
    YAPI_KREDI(
        bankName = "Yapı Kredi Mobil",
        shortName = "Yapı Kredi",
        packageName = "com.ykb.android",
        primaryColorHex = 0xFF002D72,
        iconInitials = "YK"
    ),
    ZIRAAT(
        bankName = "Ziraat Mobil",
        shortName = "Ziraat",
        packageName = "com.ziraat.ziraatmobil",
        primaryColorHex = 0xFFE30613,
        iconInitials = "ZR"
    ),
    VAKIFBANK(
        bankName = "VakıfBank Mobil",
        shortName = "VakıfBank",
        packageName = "com.vakifbank.mobile",
        primaryColorHex = 0xFFFDB913,
        iconInitials = "VB"
    ),
    ENPARA(
        bankName = "Enpara.com Cep",
        shortName = "Enpara",
        packageName = "com.enpara.mobile",
        primaryColorHex = 0xFF602880,
        iconInitials = "EN"
    ),
    QNB(
        bankName = "QNB Mobil",
        shortName = "QNB",
        packageName = "com.finansbank.mobile.cepsube",
        primaryColorHex = 0xFF6B1A6B,
        iconInitials = "QN"
    ),
    PAPARA(
        bankName = "Papara",
        shortName = "Papara",
        packageName = "com.papara",
        primaryColorHex = 0xFF0F172A,
        iconInitials = "PP"
    ),
    KUVEYT_TURK(
        bankName = "Kuveyt Türk Mobil",
        shortName = "Kuveyt Türk",
        packageName = "com.kuveytturk.mobil",
        primaryColorHex = 0xFF005830,
        iconInitials = "KT"
    ),
    TEB(
        bankName = "CEPTETEB",
        shortName = "TEB",
        packageName = "com.teb",
        primaryColorHex = 0xFF00833E,
        iconInitials = "TB"
    ),
    DENIZBANK(
        bankName = "MobilDeniz",
        shortName = "DenizBank",
        packageName = "com.denizbank.mobildeniz",
        primaryColorHex = 0xFF003865,
        iconInitials = "DB"
    ),
    HALKBANK(
        bankName = "Halkbank Mobil",
        shortName = "Halkbank",
        packageName = "com.halkbank.sube",
        primaryColorHex = 0xFF005BA6,
        iconInitials = "HB"
    )
}
