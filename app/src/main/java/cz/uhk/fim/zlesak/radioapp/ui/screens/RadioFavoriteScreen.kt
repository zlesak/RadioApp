package cz.uhk.fim.zlesak.radioapp.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.SignalWifiConnectedNoInternet4
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cz.uhk.fim.zlesak.radioapp.R
import cz.uhk.fim.zlesak.radioapp.api.ApiResult
import cz.uhk.fim.zlesak.radioapp.router.NetworkMonitorHelper
import cz.uhk.fim.zlesak.radioapp.ui.composeItems.CPI
import cz.uhk.fim.zlesak.radioapp.ui.composeItems.HeaderComponent
import cz.uhk.fim.zlesak.radioapp.ui.composeItems.SadComponent
import cz.uhk.fim.zlesak.radioapp.ui.items.RadioStationItem
import cz.uhk.fim.zlesak.radioapp.viewModels.RadioFavoriteViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun RadioFavoriteScreen(
    navController: NavController,
    viewModel: RadioFavoriteViewModel = koinViewModel(),
    context: Context
) {
    val radioFavoriteList by viewModel.radioFavoriteList.collectAsState()
    val networkMonitor = remember { NetworkMonitorHelper(context) }
    val isOnline by networkMonitor.isOnline.collectAsState(initial = false)

    LaunchedEffect(isOnline) {
        viewModel.loadFavoriteRadios()
    }
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        HeaderComponent(R.string.favourite_header)
        when (isOnline) {
            false -> {
                SadComponent(
                    Icons.Filled.SignalWifiConnectedNoInternet4,
                    R.string.no_internet,
                    text1 = R.string.no_internet_message,
                    text2 = R.string.no_internet_help
                )
            }

            true -> {
                when (radioFavoriteList) {
                    is ApiResult.Error -> {
                        val errorMessage = (radioFavoriteList as ApiResult.Error).message
                        Text(text = "Error: $errorMessage")
                    }

                    ApiResult.Loading -> {
                        CPI()
                    }

                    is ApiResult.Success -> {
                        val list = (radioFavoriteList as ApiResult.Success).data
                        if (list.isNotEmpty()) {
                            LazyColumn {
                                items(list) { radio ->
                                    RadioStationItem(radio, navController, true)
                                    HorizontalDivider()
                                }
                            }
                        } else {
                            SadComponent(
                                Icons.Filled.HeartBroken,
                                R.string.none_fav_icon,
                                R.string.none_favourite,
                                R.string.try_exploring
                            )
                        }
                    }
                }
            }
        }
    }
}