# GEMINI.md - AradaPay Android Geliştirme & Tasarım Standartları

Bu belge, **Gemini / Antigravity AI** asistanının **AradaPay (ArdaBank)** projesinde kod üretirken, mimari tasarlarken ve arayüz geliştirirken uyması gereken resmi **Android Material 3** ve **Hedvig Android Clean Architecture** mühendislik kurallarını tanımlar.

---

## 1. Mimari Prensipler (Hedvig & Modern Android Architecture)

### 1.1. MVI (Model-View-Intent) & Tek Yönlü Veri Akışı (UDF)
- **Akış**: `Kullanıcı Eylemi (Event) -> ViewModel -> UiState (StateFlow) -> Jetpack Compose UI`
- Her ekranın durumu (`UiState`) immutable bir `data class` veya `sealed class` olmalıdır.
- Android Lifecycle uyumu: Compose ekranlarında durumlar `collectAsStateWithLifecycle()` veya `collectAsState()` ile dinlenir.

### 1.2. Navigasyon & Geri Tuşu Yönetimi (Android Back & Invariants)
- `navigateUp()` **kesinlikle yalnızca** ekranların sol üst geri oku (TopAppBar navigation icon) için kullanılır.
- İptal, Kapat, Tamam, İşlem Bitti veya onay butonları her zaman `popBackStack()` çağırır.
- Android Sistem Geri Hareketi (Predictive Back Gesture) ile tam uyumlu `BackHandler` yapısı korunur.

### 1.3. Clean Architecture Katmanları
- **Domain Katmanı**: Saf Kotlin (`algorithm/`, `model/`, `usecase/`). Android bağımlılığı içermez, %100 birim test kapsamındadır.
- **Data Katmanı**: Firebase Firestore, Firebase Auth ve Jetpack Encrypted DataStore implementasyonları.
- **Presentation Katmanı**: Jetpack Compose, Material 3, Dark Tonal Elevation tema.

---

## 2. Tasarım & UI Standartları (Android Material 3 / Material You)

### 2.1. Material 3 Bileşen Hiyerarşisi
- **Top Bar**: Material 3 `TopAppBar` / `CenterAlignedTopAppBar` (Sol başlık, sağ eylem ikonları/chipleri).
- **Navigation Bar**: Resmi Material 3 `NavigationBar` ve `NavigationBarItem` (M3 aktif hap indikatörü, M3 tonal yüzey).
- **Kartlar**: Material 3 `ElevatedCard` ve `OutlinedCard` (Tonal Elevation, yumuşak 20-24dp M3 köşe yuvarlatmaları).
- **Filtreler & Seçimler**: Material 3 `FilterChip`, `AssistChip` ve `SuggestionChip` bileşenleri.
- **Butonlar**: Material 3 `FilledButton`, `FilledTonalButton`, `OutlinedButton` ve `ExtendedFloatingActionButton`.

### 2.2. Durum Çubuğu & Edge-to-Edge Koruması
- Her ekranda `statusBarsPadding()`, alt gezinti çubuğu ve bottom sheet'lerde `navigationBarsPadding()` zorunludur.
- Android sistem jest çubuğu (Home indicator) ile arayüz elemanları asla çakışmaz.

### 2.3. Tipografi & Boşluklar (Material 3 Type Scale)
- Başlıklar: `MaterialTheme.typography.headlineLarge` / `titleLarge`.
- Gövde: `MaterialTheme.typography.bodyLarge` / `bodyMedium`.
- Etiketler: `MaterialTheme.typography.labelMedium` / `labelSmall`.

---

## 3. Güvenlik & KVKK Standartları
- 4 haneli finansal PIN kodları SHA-256 ile hashlenerek saklanır.
- PIN kilitliyken `MaskedFinancialText` tüm bakiyeleri `•••• ₺` şeklinde maskeler.
- KVKK m.11 unutulma hakkı kapsamında tek tıkla tam veri silme olanağı sunulur.
