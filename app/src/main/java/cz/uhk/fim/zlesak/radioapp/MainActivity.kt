package cz.uhk.fim.zlesak.radioapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cz.uhk.fim.zlesak.radioapp.router.Routes
import cz.uhk.fim.zlesak.radioapp.ui.screens.RadioDetailScreen
import cz.uhk.fim.zlesak.radioapp.ui.screens.RadioFavoriteScreen
import cz.uhk.fim.zlesak.radioapp.ui.screens.RadioHistoryScreen
import cz.uhk.fim.zlesak.radioapp.ui.screens.RadioHomeScreen
import cz.uhk.fim.zlesak.radioapp.ui.navigation.BottomNavItem
import cz.uhk.fim.zlesak.radioapp.ui.screens.RadioSearchScreen
import cz.uhk.fim.zlesak.radioapp.ui.theme.RadioAppTheme
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        startKoin {
            androidContext(this@MainActivity)
            modules(repositoryModule, viewModelModule, networkModule, objectBoxModule, imageModule)
        }
        setContent {
            RadioAppTheme {
                val navController = rememberNavController()
                MainScreen(navController, this)
            }
        }
        requestPositionPermission()
    }

    private fun requestPositionPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun MainScreen(navController: NavHostController, context : Context) {
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
                                    popUpTo(screenRoute) {
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
        Navigation(navController = navController, innerPadding = innerPadding, context = context)
    }

}

@Composable
fun Navigation(navController: NavHostController, innerPadding: PaddingValues, context: Context) {
    NavHost(
        navController = navController,
        startDestination = Routes.RadioHome,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(Routes.RadioHome) { RadioHomeScreen(navController) }
        composable(Routes.RadioSearch) { RadioSearchScreen(navController, context = context) }
        composable(Routes.RadioDetail) { navBackStackEntry ->
            val radioUuid = navBackStackEntry.arguments?.getString("uuid")
            if (radioUuid != null) {
                RadioDetailScreen(navController, radioUuid)
            }
        }
        composable(Routes.RadioFavorites) { RadioFavoriteScreen(navController) }
        composable(Routes.RadioHistory) { RadioHistoryScreen(navController) }
    }
}