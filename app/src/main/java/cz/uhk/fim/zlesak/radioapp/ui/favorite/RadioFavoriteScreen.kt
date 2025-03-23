package cz.uhk.fim.zlesak.radioapp.ui.favorite

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cz.uhk.fim.zlesak.radioapp.api.ApiResult
import cz.uhk.fim.zlesak.radioapp.ui.items.RadioStationItem
import cz.uhk.fim.zlesak.radioapp.viewModels.RadioFavoriteViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun RadioFavoriteScreen(navController: NavController, viewModel: RadioFavoriteViewModel = koinViewModel()){
    val radioFavoriteList by viewModel.radioFavoriteList.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadFavoriteRadios()
    }
    Column (
        modifier = Modifier.padding(16.dp)
    ){
        Text(text = "Favorite crypto screen")
        when(radioFavoriteList){
            is ApiResult.Error -> {
                val errorMessage = (radioFavoriteList as ApiResult.Error).message
                Text(text = "Error: $errorMessage")
            }
            ApiResult.Loading -> {
                CircularProgressIndicator()
            }
            is ApiResult.Success -> {
                val list = (radioFavoriteList as ApiResult.Success).data
                LazyColumn {
                    items(list){radio ->
                        RadioStationItem(radio, navController, true)
                    }
                }
            }
        }
    }
}