package cz.uhk.fim.zlesak.radioapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cz.uhk.fim.zlesak.radioapp.R
import cz.uhk.fim.zlesak.radioapp.api.ApiResult
import cz.uhk.fim.zlesak.radioapp.ui.composeItems.CPI
import cz.uhk.fim.zlesak.radioapp.ui.items.RadioStationItem
import cz.uhk.fim.zlesak.radioapp.viewModels.RadioFavoriteViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun RadioFavoriteScreen(
    navController: NavController,
    viewModel: RadioFavoriteViewModel = koinViewModel()
) {
    val radioFavoriteList by viewModel.radioFavoriteList.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadFavoriteRadios()
    }
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.favourite_header),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))
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
                    Icon(
                        Icons.Filled.HeartBroken,
                        contentDescription = stringResource(R.string.none_fav_icon),
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.CenterHorizontally)
                            .size(48.dp)
                    )
                    Text(
                        text = stringResource(R.string.none_favourite),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = stringResource(R.string.try_exploring),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}