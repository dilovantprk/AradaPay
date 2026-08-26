package com.ardabank.aradapay.presentation.expense

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Commute
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.RoomService
import androidx.compose.material.icons.filled.Sanitizer
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.ardabank.aradapay.domain.model.ExpenseCategory

data class CategoryGroup(
    val id: String,
    val title: String,
    val englishTitle: String,
    val bgTint: Color,
    val iconTint: Color,
    val items: List<DetailedCategoryItem>
)

data class DetailedCategoryItem(
    val id: String,
    val name: String,
    val englishName: String,
    val icon: ImageVector,
    val parentCategory: ExpenseCategory,
    val bgTint: Color,
    val iconTint: Color
)

object ExpenseCategoryCatalog {

    val groups: List<CategoryGroup> = listOf(
        // 1. Entertainment / Eğlence
        CategoryGroup(
            id = "entertainment",
            title = "Eğlence",
            englishTitle = "Entertainment",
            bgTint = Color(0xFFF3E8FF),
            iconTint = Color(0xFF7C3AED),
            items = listOf(
                DetailedCategoryItem("games", "Oyunlar", "Games", Icons.Default.SportsEsports, ExpenseCategory.ENTERTAINMENT, Color(0xFFF3E8FF), Color(0xFF7C3AED)),
                DetailedCategoryItem("movies", "Sinema & Film", "Movies", Icons.Default.Movie, ExpenseCategory.ENTERTAINMENT, Color(0xFFF3E8FF), Color(0xFF7C3AED)),
                DetailedCategoryItem("music", "Müzik & Konser", "Music", Icons.Default.MusicNote, ExpenseCategory.ENTERTAINMENT, Color(0xFFF3E8FF), Color(0xFF7C3AED)),
                DetailedCategoryItem("sports", "Spor & Etkinlik", "Sports", Icons.Default.SportsSoccer, ExpenseCategory.ENTERTAINMENT, Color(0xFFF3E8FF), Color(0xFF7C3AED)),
                DetailedCategoryItem("other_entertainment", "Diğer Eğlence", "Other", Icons.Default.Celebration, ExpenseCategory.ENTERTAINMENT, Color(0xFFF3E8FF), Color(0xFF7C3AED))
            )
        ),

        // 2. Food and drink / Yeme & İçme
        CategoryGroup(
            id = "food_drink",
            title = "Yeme & İçme",
            englishTitle = "Food and drink",
            bgTint = Color(0xFFDCFCE7),
            iconTint = Color(0xFF16A34A),
            items = listOf(
                DetailedCategoryItem("dining_out", "Dışarıda Yemek", "Dining out", Icons.Default.Restaurant, ExpenseCategory.DINING, Color(0xFFDCFCE7), Color(0xFF16A34A)),
                DetailedCategoryItem("groceries", "Market & Bakkal", "Groceries", Icons.Default.ShoppingCart, ExpenseCategory.GROCERIES, Color(0xFFDCFCE7), Color(0xFF16A34A)),
                DetailedCategoryItem("liquor", "İçki & Bar", "Liquor", Icons.Default.LocalBar, ExpenseCategory.DINING, Color(0xFFDCFCE7), Color(0xFF16A34A)),
                DetailedCategoryItem("other_food", "Diğer Yeme-İçme", "Other", Icons.Default.Fastfood, ExpenseCategory.DINING, Color(0xFFDCFCE7), Color(0xFF16A34A))
            )
        ),

        // 3. Home / Ev & Yaşam
        CategoryGroup(
            id = "home",
            title = "Ev & Yaşam",
            englishTitle = "Home",
            bgTint = Color(0xFFFEF9C3),
            iconTint = Color(0xFFCA8A04),
            items = listOf(
                DetailedCategoryItem("electronics", "Elektronik", "Electronics", Icons.Default.Bolt, ExpenseCategory.HOUSING, Color(0xFFFEF9C3), Color(0xFFCA8A04)),
                DetailedCategoryItem("furniture", "Mobilya", "Furniture", Icons.Default.Chair, ExpenseCategory.HOUSING, Color(0xFFFEF9C3), Color(0xFFCA8A04)),
                DetailedCategoryItem("household", "Ev İhtiyaçları", "Household supplies", Icons.Default.Sanitizer, ExpenseCategory.HOUSING, Color(0xFFFEF9C3), Color(0xFFCA8A04)),
                DetailedCategoryItem("maintenance", "Tamirat & Bakım", "Maintenance", Icons.Default.Handyman, ExpenseCategory.HOUSING, Color(0xFFFEF9C3), Color(0xFFCA8A04)),
                DetailedCategoryItem("mortgage", "Konut Kredisi", "Mortgage", Icons.Default.HomeWork, ExpenseCategory.HOUSING, Color(0xFFFEF9C3), Color(0xFFCA8A04)),
                DetailedCategoryItem("pets", "Evcil Hayvan", "Pets", Icons.Default.Pets, ExpenseCategory.HOUSING, Color(0xFFFEF9C3), Color(0xFFCA8A04)),
                DetailedCategoryItem("rent", "Kira", "Rent", Icons.Default.Home, ExpenseCategory.HOUSING, Color(0xFFFEF9C3), Color(0xFFCA8A04)),
                DetailedCategoryItem("services", "Hizmetler", "Services", Icons.Default.RoomService, ExpenseCategory.HOUSING, Color(0xFFFEF9C3), Color(0xFFCA8A04)),
                DetailedCategoryItem("other_home", "Diğer Ev", "Other", Icons.Default.Build, ExpenseCategory.HOUSING, Color(0xFFFEF9C3), Color(0xFFCA8A04))
            )
        ),

        // 4. Life / Kişisel & Yaşam
        CategoryGroup(
            id = "life",
            title = "Kişisel & Yaşam",
            englishTitle = "Life",
            bgTint = Color(0xFFFFEDD5),
            iconTint = Color(0xFFEA580C),
            items = listOf(
                DetailedCategoryItem("childcare", "Çocuk Giderleri", "Childcare", Icons.Default.ChildCare, ExpenseCategory.OTHER, Color(0xFFFFEDD5), Color(0xFFEA580C)),
                DetailedCategoryItem("clothing", "Giyim & Moda", "Clothing", Icons.Default.Checkroom, ExpenseCategory.SHOPPING, Color(0xFFFFEDD5), Color(0xFFEA580C)),
                DetailedCategoryItem("education", "Eğitim & Kurs", "Education", Icons.Default.School, ExpenseCategory.OTHER, Color(0xFFFFEDD5), Color(0xFFEA580C)),
                DetailedCategoryItem("gifts", "Hediye & Kutlama", "Gifts", Icons.Default.CardGiftcard, ExpenseCategory.SHOPPING, Color(0xFFFFEDD5), Color(0xFFEA580C)),
                DetailedCategoryItem("insurance", "Sigorta & Kasko", "Insurance", Icons.Default.HealthAndSafety, ExpenseCategory.OTHER, Color(0xFFFFEDD5), Color(0xFFEA580C)),
                DetailedCategoryItem("medical", "Sağlık & İlaç", "Medical expenses", Icons.Default.MedicalServices, ExpenseCategory.OTHER, Color(0xFFFFEDD5), Color(0xFFEA580C)),
                DetailedCategoryItem("taxes", "Vergi & Harç", "Taxes", Icons.Default.AccountBalance, ExpenseCategory.OTHER, Color(0xFFFFEDD5), Color(0xFFEA580C)),
                DetailedCategoryItem("other_life", "Diğer Yaşam", "Other", Icons.Default.ReceiptLong, ExpenseCategory.OTHER, Color(0xFFFFEDD5), Color(0xFFEA580C))
            )
        ),

        // 5. Transportation / Ulaşım & Seyahat
        CategoryGroup(
            id = "transportation",
            title = "Ulaşım & Seyahat",
            englishTitle = "Transportation",
            bgTint = Color(0xFFFCE7F3),
            iconTint = Color(0xFFDB2777),
            items = listOf(
                DetailedCategoryItem("bicycle", "Bisiklet & Scooter", "Bicycle", Icons.Default.DirectionsBike, ExpenseCategory.TRAVEL, Color(0xFFFCE7F3), Color(0xFFDB2777)),
                DetailedCategoryItem("bus_train", "Otobüs & Tren", "Bus/train", Icons.Default.DirectionsTransit, ExpenseCategory.TRAVEL, Color(0xFFFCE7F3), Color(0xFFDB2777)),
                DetailedCategoryItem("car", "Araç Masrafları", "Car", Icons.Default.DirectionsCar, ExpenseCategory.TRAVEL, Color(0xFFFCE7F3), Color(0xFFDB2777)),
                DetailedCategoryItem("fuel", "Akaryakıt & Yakıt", "Gas/fuel", Icons.Default.LocalGasStation, ExpenseCategory.TRAVEL, Color(0xFFFCE7F3), Color(0xFFDB2777)),
                DetailedCategoryItem("hotel", "Otel & Konaklama", "Hotel", Icons.Default.Hotel, ExpenseCategory.TRAVEL, Color(0xFFFCE7F3), Color(0xFFDB2777)),
                DetailedCategoryItem("parking", "Otopark", "Parking", Icons.Default.LocalParking, ExpenseCategory.TRAVEL, Color(0xFFFCE7F3), Color(0xFFDB2777)),
                DetailedCategoryItem("plane", "Uçak Bileti", "Plane", Icons.Default.Flight, ExpenseCategory.TRAVEL, Color(0xFFFCE7F3), Color(0xFFDB2777)),
                DetailedCategoryItem("taxi", "Taksi & Ulaşım", "Taxi", Icons.Default.LocalTaxi, ExpenseCategory.TRAVEL, Color(0xFFFCE7F3), Color(0xFFDB2777)),
                DetailedCategoryItem("other_transport", "Diğer Ulaşım", "Other", Icons.Default.Commute, ExpenseCategory.TRAVEL, Color(0xFFFCE7F3), Color(0xFFDB2777))
            )
        ),

        // 6. Utilities / Faturalar & Abonelikler
        CategoryGroup(
            id = "utilities",
            title = "Faturalar & Abonelikler",
            englishTitle = "Utilities",
            bgTint = Color(0xFFCCFBF1),
            iconTint = Color(0xFF0D9488),
            items = listOf(
                DetailedCategoryItem("cleaning", "Temizlik", "Cleaning", Icons.Default.CleaningServices, ExpenseCategory.UTILITIES, Color(0xFFCCFBF1), Color(0xFF0D9488)),
                DetailedCategoryItem("electricity", "Elektrik", "Electricity", Icons.Default.Lightbulb, ExpenseCategory.UTILITIES, Color(0xFFCCFBF1), Color(0xFF0D9488)),
                DetailedCategoryItem("heat_gas", "Doğalgaz & Isınma", "Heat/gas", Icons.Default.LocalFireDepartment, ExpenseCategory.UTILITIES, Color(0xFFCCFBF1), Color(0xFF0D9488)),
                DetailedCategoryItem("trash", "Aidat & Çöp", "Trash", Icons.Default.Delete, ExpenseCategory.UTILITIES, Color(0xFFCCFBF1), Color(0xFF0D9488)),
                DetailedCategoryItem("internet_phone", "TV / Telefon / İnternet", "TV/Phone/Internet", Icons.Default.Wifi, ExpenseCategory.UTILITIES, Color(0xFFCCFBF1), Color(0xFF0D9488)),
                DetailedCategoryItem("water", "Su", "Water", Icons.Default.WaterDrop, ExpenseCategory.UTILITIES, Color(0xFFCCFBF1), Color(0xFF0D9488)),
                DetailedCategoryItem("other_utility", "Diğer Fatura", "Other", Icons.Default.FlashOn, ExpenseCategory.UTILITIES, Color(0xFFCCFBF1), Color(0xFF0D9488))
            )
        ),

        // 7. Uncategorized / Genel Harcama
        CategoryGroup(
            id = "uncategorized",
            title = "Genel & Diğer",
            englishTitle = "Uncategorized",
            bgTint = Color(0xFFF1F5F9),
            iconTint = Color(0xFF64748B),
            items = listOf(
                DetailedCategoryItem("general", "Genel Harcama", "General", Icons.Default.Receipt, ExpenseCategory.OTHER, Color(0xFFF1F5F9), Color(0xFF64748B))
            )
        )
    )

    val allItems: List<DetailedCategoryItem> = groups.flatMap { it.items }

    fun findItemById(id: String): DetailedCategoryItem? = allItems.find { it.id == id }

    fun findDefaultItemForCategory(category: ExpenseCategory): DetailedCategoryItem {
        return allItems.firstOrNull { it.parentCategory == category } ?: allItems.first()
    }
}
