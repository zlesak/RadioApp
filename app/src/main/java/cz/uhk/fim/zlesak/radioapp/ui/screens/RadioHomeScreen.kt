package cz.uhk.fim.zlesak.radioapp.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
import cz.uhk.fim.zlesak.radioapp.viewModels.RadioViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun RadioHomeScreen(
    navController: NavController,
    radioViewModel: RadioViewModel = koinViewModel(),
    viewModel: RadioFavoriteViewModel = koinViewModel(),
    context: Context
) {
    val radioList by radioViewModel.radioList.collectAsState()
    val favorites by viewModel.radioFavoriteList.collectAsState()

    val networkMonitor = remember { NetworkMonitorHelper(context) }
    val isOnline by networkMonitor.isOnline.collectAsState(initial = false)

    LaunchedEffect(isOnline) {
        radioViewModel.getTopClickRadioStations()
        viewModel.loadFavoriteRadios()
    }
    Column(modifier = Modifier.padding(16.dp)) {
        HeaderComponent(R.string.home_header_text)
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
                when (radioList) {
                    is ApiResult.Error -> {
                        val errorMessage = (radioList as ApiResult.Error).message
                        Text(text = "Error: $errorMessage")
                    }

                    is ApiResult.Loading -> {
                        CPI()
                    }

                    is ApiResult.Success -> {
                        val list = (radioList as ApiResult.Success).data

                        LazyColumn {
                            items(list) { radio ->
                                val isFavorite = if (favorites is ApiResult.Success) {
                                    (favorites as ApiResult.Success).data.any { it.stationuuid == radio.stationuuid }
                                } else {
                                    false
                                }
                                RadioStationItem(radio, navController, isFavorite = isFavorite)
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}