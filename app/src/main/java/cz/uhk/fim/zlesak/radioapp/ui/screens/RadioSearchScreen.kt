package cz.uhk.fim.zlesak.radioapp.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cz.uhk.fim.zlesak.radioapp.R
import cz.uhk.fim.zlesak.radioapp.api.ApiResult
import cz.uhk.fim.zlesak.radioapp.ui.items.RadioStationItem
import cz.uhk.fim.zlesak.radioapp.viewModels.RadioFavoriteViewModel
import cz.uhk.fim.zlesak.radioapp.viewModels.RadioSearchViewModel
import cz.uhk.fim.zlesak.radioapp.viewModels.RadioViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun RadioSearchScreen(
    navController: NavController,
    radioViewModel: RadioViewModel = koinViewModel(),
    viewModel: RadioFavoriteViewModel = koinViewModel(),
    radioSearchViewModel: RadioSearchViewModel = koinViewModel(),
    context: Context
) {
    val gpsRadioList by radioViewModel.gpsRadioList.collectAsState()
    val radioList by radioViewModel.searchedRadioList.collectAsState()
    val favorites by viewModel.radioFavoriteList.collectAsState()

    var searchText by remember { mutableStateOf(TextFieldValue("")) }

    val loc by radioSearchViewModel.loc.collectAsState()

    var empty = true

    LaunchedEffect(Unit) {
        viewModel.loadFavoriteRadios()
        radioSearchViewModel.getPosition(context)
        radioViewModel.clearSearchedRadioList()
    }
    LaunchedEffect(loc) {
        if (loc != null) {
            radioViewModel.getRadioStationsFromLocation(lat = loc!!.latitude, long = loc!!.longitude)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Row {
            Column {
                Text(
                    text = "Explore radio stations",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        Row(modifier = Modifier.conditional(!empty) {
            weight(2f)
        }
        ) {
            Column {
                Row(modifier = Modifier.fillMaxWidth())
                {
                    TextField(
                        modifier = Modifier
                            .height(60.dp)
                            .weight(1f),
                        value = searchText,
                        onValueChange = { searchText = it },
                        label = { Text(stringResource(R.string.search_text)) },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Search
                        )
                    )
                    IconButton(
                        modifier = Modifier
                            .width(60.dp)
                            .height(60.dp),
                        onClick = {
                            if(searchText.text.isNotEmpty() && (radioList is ApiResult.Loading || radioList is ApiResult.Error))
                                radioViewModel.getSearchedRadioStations(name = searchText.text)
                            else {
                                searchText = TextFieldValue("")
                                radioViewModel.clearSearchedRadioList()
                            }
                        }
                    ) {
                        if(radioList is ApiResult.Loading || radioList is ApiResult.Error)
                            Icon(Icons.Filled.Search, stringResource(R.string.search_icon))
                        else
                            Icon(Icons.Filled.Clear, stringResource(R.string.search_icon))
                    }
                }
                Row {
                    when (radioList) {
                        is ApiResult.Error -> {
                            val errorMessage = (radioList as ApiResult.Error).message
                            Text(text = "Error: $errorMessage")
                        }

                        is ApiResult.Loading -> {}

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
                            empty = list.isEmpty()
                        }
                    }
                }
            }
        }
        Row(modifier = Modifier.weight(1f)) {
            Column {
                Text(
                    text = stringResource(R.string.radio_search_radius_info), //TODO replace all texts to be as resources
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterHorizontally)

                )
                when (gpsRadioList) {
                    is ApiResult.Error -> {
                        val errorMessage = (gpsRadioList as ApiResult.Error).message
                        Text(text = "Error: $errorMessage")
                    }

                    is ApiResult.Loading -> {
                        CircularProgressIndicator()
                    }

                    is ApiResult.Success -> {
                        val list = (gpsRadioList as ApiResult.Success).data
                        if(list.isNotEmpty()) {
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
                        }else{
                            Text("There are no radio stations near you.")
                            Text("Try searching for some using search option.")
                        }
                    }
                }
                HorizontalDivider(thickness = 4.dp)
            }
        }
    }
}

fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}