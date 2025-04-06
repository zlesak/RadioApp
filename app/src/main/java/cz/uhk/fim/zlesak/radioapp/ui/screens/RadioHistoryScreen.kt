package cz.uhk.fim.zlesak.radioapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cz.uhk.fim.zlesak.radioapp.api.ApiResult
import cz.uhk.fim.zlesak.radioapp.ui.composeItems.CPI
import cz.uhk.fim.zlesak.radioapp.ui.items.RadioStationItem
import cz.uhk.fim.zlesak.radioapp.viewModels.RadioFavoriteViewModel
import cz.uhk.fim.zlesak.radioapp.viewModels.RadioHistoryViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun RadioHistoryScreen(navController: NavController, radioHistoryViewModel: RadioHistoryViewModel = koinViewModel(), viewModel: RadioFavoriteViewModel = koinViewModel()){
    val radioHistoryList by radioHistoryViewModel.radioHistoryList.collectAsState()
    val favorites by viewModel.radioFavoriteList.collectAsState()

    LaunchedEffect(Unit) {
        radioHistoryViewModel.getHistory()
    }
    Column {
        Text(
            text = "Check your last visited radios",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))
        when (radioHistoryList) {
            is ApiResult.Error -> {
                val errorMessage = (radioHistoryList as ApiResult.Error).message
                Text(text = "Error: $errorMessage")
            }

            is ApiResult.Loading -> {
                CPI()
            }

            is ApiResult.Success -> {
                val list = (radioHistoryList as ApiResult.Success).data
                if (list.isNotEmpty()) {
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
                } else {
                    Text("There are no recent radio stations.")
                    Text("Try exploring some to see your history later.")
                }
            }
        }
    }
}