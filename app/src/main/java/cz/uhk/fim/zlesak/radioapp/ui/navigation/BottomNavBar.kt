package cz.uhk.fim.zlesak.radioapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import cz.uhk.fim.zlesak.radioapp.router.Routes


sealed class BottomNavItem(val title: String, val icon : ImageVector, val screenRoute : String ) {
    object RadioHome : BottomNavItem("Home", Icons.Filled.Home, Routes.RadioHome)
    object RadioFavorite : BottomNavItem("Favorite", Icons.Filled.FavoriteBorder, Routes.RadioFavorites)
    object RadioSearch : BottomNavItem ("Search", Icons.Filled.Search, Routes.RadioSearch)
    object RadioHistory : BottomNavItem("History", Icons.Filled.Menu, Routes.RadioHistory)
}