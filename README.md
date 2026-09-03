# AradaPay 💳

> **Modern Jetpack Compose & Web Social Expense Sharing Platform**  
> Resolving group expenses and multi-party payment cycles with smart debt simplification algorithms and instant FAST settlement.

[![Android](https://img.shields.io/badge/Platform-Android%2014+-3DDC84?logo=android&logoColor=white)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin%20100%25-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20%2B%20M3-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20MVI-brightgreen)](#-architecture--stack)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

---

## 🌟 Key Features & Architecture

### 1. Smart Debt Simplification
- Automatically analyzes circular expense relations across group members.
- Applies greedy balance reduction to decrease transaction overhead, converting complex debt webs into minimum direct settlement steps.

### 2. Digital Settlement Receipt Engine
- Every settlement produces a verifiable digital receipt with a unique transaction reference and timestamp.
- Exports clean PDF / digital receipts.

### 3. Privacy-First Biometric Vault & Balance Masking
- **`MaskedFinancialText`**: One-touch dynamic privacy masking for public spaces (e.g. `•••• ₺`).
- Secured via **AndroidX Biometric** (Fingerprint / Face Unlock) + **SHA-256 encrypted PIN vault**.
- Fully compliant with **KVKK / GDPR** data protection standards.

### 4. Contactless QR Settlement Ecosystem
- Real-time hardware-accelerated QR code scanner powered by **Google ML Kit Vision API** and **CameraX** for $<100\text{ms}$ contactless peer settlement.

### 5. Flexible Split Models
- Equal, percentage-based, and exact amount division with multi-currency support (**TRY, USD, EUR**).

---

## 🏗 Architecture & Stack

```
app/
├── core/
│   ├── algorithm/        # Directed Graph DFS, Merkle Tree cryptographic engine
│   ├── security/         # AndroidX Biometrics, SHA-256 Vault, EncryptedDataStore
│   └── util/             # QR scanner, iText PDF generator, currency formatters
├── data/
│   ├── repository/       # Firebase Firestore & local state synchronization
│   └── model/            # Immutable domain & DTO entities
├── domain/
│   ├── usecase/          # Pure Kotlin business rules & transaction logic
│   └── model/            # Core financial entities
└── presentation/
    ├── dashboard/        # Real-time balances, activity feeds, quick actions
    ├── settlement/       # DFS calculation visualizer, QR pay, receipt export
    └── theme/            # Material You dynamic theming, dark tonal elevation
```

- **Language**: Kotlin 100% (JVM 17, Coroutines, StateFlow)
- **UI Framework**: Jetpack Compose + Material 3 (Material You Dynamic Colors)
- **Architecture**: Hedvig Clean Architecture, MVI (Model-View-Intent), Unidirectional Data Flow (UDF)
- **Backend & Sync**: Google Cloud Firestore, Firebase Auth, FCM Push Notifications
- **Hardware & Vision**: Google ML Kit Vision Barcode Scanner, CameraX, AndroidX Biometrics
- **Documents**: iText PDF Engine & ZXing Barcodes

---

## 📲 Download & Installation

You can download the pre-built, production-ready APK directly from this repository:

👉 **[Download AradaPay.apk (Direct)](./AradaPay.apk)**

Or install via ADB:
```bash
adb install AradaPay.apk
```

---

## 👨‍💻 Author

**Mehmet Dilovan Toprak**  
- Portfolio: [whatevervedoneididitfor.fun](https://whatevervedoneididitfor.fun)
- GitHub: [@dilovantprk](https://github.com/dilovantprk)
- Telegram: [@dilovaniac](https://t.me/dilovaniac)
