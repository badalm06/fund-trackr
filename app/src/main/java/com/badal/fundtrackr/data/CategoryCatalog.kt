package com.badal.fundtrackr.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector
import kotlin.collections.get

object CategoryCatalog {

    // Map of display name -> icon
    private val categoryIconMap: Map<String, ImageVector> = mapOf(
        // Core
        "Clothing" to Icons.Default.Checkroom,
        "Education" to Icons.Default.School,
        "Entertainment" to Icons.Default.Movie,
        "Food & Dining" to Icons.Default.Restaurant,
        "Games" to Icons.Default.SportsEsports,
        "Groceries" to Icons.Default.LocalGroceryStore,
        "Health & Fitness" to Icons.Default.FitnessCenter,
        "Home" to Icons.Default.Home,
        "Insurance" to Icons.Default.Security,
        "Personal Care" to Icons.Default.Face,
        "Rent" to Icons.Default.Home,
        "Savings" to Icons.Default.Savings,
        "Shopping" to Icons.Default.ShoppingCart,
        "Subscriptions" to Icons.Default.Subscriptions,
        "Taxes" to Icons.Default.Receipt,
        "Technology" to Icons.Default.PhoneAndroid,
        "Transportation" to Icons.Default.DirectionsCar,
        "Travel" to Icons.Default.Flight,
        "Utilities" to Icons.Default.Wifi,
        "Gifts & Donations" to Icons.Default.CardGiftcard,

        // Fun / extra
        "Movies & Series" to Icons.Default.Tv,
        "Music & Concerts" to Icons.Default.MusicNote,
        "Sports & Outdoor Activities" to Icons.Default.Sports,
        "Pet Care" to Icons.Default.Pets,
        "Party & Events" to Icons.Default.Celebration,

        // Brand-ish / legacy (map to closest)
        "Netflix" to Icons.Default.Subscriptions,
        "Starbucks" to Icons.Default.LocalCafe,
        "Upwork" to Icons.Default.Work,
        "Paypal" to Icons.Default.Payment,

        // Income-y
        "Salary" to Icons.Default.Work,
        "Freelancing" to Icons.Default.Laptop,
        "Business" to Icons.Default.Business,
        "Rental Income" to Icons.Default.Apartment,
        "Other Income" to Icons.Default.AttachMoney,
        "Investments (Stocks, Mutual Funds)" to Icons.AutoMirrored.Filled.TrendingUp
    )

    /** Sorted deduped list you can feed into dropdowns. */
    val allCategories: List<String> = categoryIconMap.keys.sorted()

    /** Return icon for category (case-insensitive match, fallback generic). */
    fun iconFor(raw: String?): ImageVector {
        if (raw == null) return Icons.Default.Category
        // case-insensitive lookup
        val match = categoryIconMap.keys.firstOrNull { it.equals(raw, ignoreCase = true) }
        return categoryIconMap[match] ?: Icons.Default.Category
    }
}