package cz.uhk.fim.zlesak.radioapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cz.uhk.fim.zlesak.radioapp.api.ApiResult
import cz.uhk.fim.zlesak.radioapp.ui.items.RadioStationItem
import cz.uhk.fim.zlesak.radioapp.viewModels.RadioFavoriteViewModel
import cz.uhk.fim.zlesak.radioapp.viewModels.RadioViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun RadioHomeScreen(
    navController: NavController,
    radioViewModel: RadioViewModel = koinViewModel(),
    viewModel: RadioFavoriteViewModel = koinViewModel()

){
    val radioList by radioViewModel.radioList.collectAsState()
    val favorites by viewModel.radioFavoriteList.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) { //to only happen once
        coroutineScope.launch {
            radioViewModel.getTopClickRadioStations()
            viewModel.loadFavoriteRadios()
        }
    }
    Column (modifier = Modifier.padding(16.dp)){
        Text(
            text = "Check 15 top voted radio stations:",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(8.dp).align(Alignment.CenterHorizontally)

        )
        Spacer(modifier = Modifier.height(16.dp))
        when(radioList){
            is ApiResult.Error -> {
                val errorMessage = (radioList as ApiResult.Error).message
                Text(text = "Error: $errorMessage")
            }
            is ApiResult.Loading -> {
                CircularProgressIndicator()
            }
            is ApiResult.Success -> {
                val list = (radioList as ApiResult.Success).data

                LazyColumn {
                    items(list){radio ->
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