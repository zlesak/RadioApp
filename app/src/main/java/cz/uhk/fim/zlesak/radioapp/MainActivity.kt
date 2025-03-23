package cz.uhk.fim.zlesak.radioapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cz.uhk.fim.zlesak.radioapp.router.Routes
import cz.uhk.fim.zlesak.radioapp.ui.detail.RadioDetailScreen
import cz.uhk.fim.zlesak.radioapp.ui.favorite.RadioFavoriteScreen
import cz.uhk.fim.zlesak.radioapp.ui.history.RadioHistoryScreen
import cz.uhk.fim.zlesak.radioapp.ui.home.RadioHomeScreen
import cz.uhk.fim.zlesak.radioapp.ui.navigation.BottomNavItem
import cz.uhk.fim.zlesak.radioapp.ui.search.RadioSearchScreen
import cz.uhk.fim.zlesak.radioapp.ui.theme.RadioAppTheme
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        startKoin{
            androidContext(this@MainActivity)
            modules(repositoryModule, viewModelModule, networkModule, objectBoxModule, imageModule)
        }
        setContent {
            RadioAppTheme {
                val navController = rememberNavController()
                MainScreen(navController)
            }
        }
    }
}

@Composable
fun MainScreen(navController: NavHostController) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf(
        BottomNavItem.RadioHome,
        BottomNavItem.RadioSearch,
        BottomNavItem.RadioFavorite,
        BottomNavItem.RadioHistory
    )
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                item.icon,
                                contentDescription = item.title
                            )
                        },
                        label = { Text(item.title) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            navController.navigate(item.screenRoute) {
                                navController.graph.startDestinationRoute?.let { screenRoute ->
                                    popUpTo(screenRoute){
                                        saveState = true
                                    }
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color.LightGray,
                            selectedTextColor = Color.White,
                            unselectedTextColor = Color.LightGray,
                            indicatorColor = MaterialTheme.colorScheme.secondary
                        )
                    )
                }
            }
        }

    ) { innerPadding ->
        Navigation(navController = navController, innerPadding = innerPadding)
    }

}

@Composable
fun Navigation(navController: NavHostController, innerPadding: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = Routes.RadioHome,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(Routes.RadioHome) { RadioHomeScreen(navController) }
        composable(Routes.RadioSearch) { RadioSearchScreen(navController) }
        composable(Routes.RadioDetail) {
                navBackStackEntry -> val radioUuid  = navBackStackEntry.arguments?.getString("uuid")
            if(radioUuid != null){
                RadioDetailScreen(navController, radioUuid)
            }
        }
        composable(Routes.RadioFavorites){ RadioFavoriteScreen(navController) }
        composable(Routes.RadioHistory) { RadioHistoryScreen(navController) }
    }
}
//
//@Preview(showBackground = true)
//@Composable
//fun MainScreenPreview() {
//    RadioAppTheme {
//        MainScreen(rememberNavController())
//    }
//}