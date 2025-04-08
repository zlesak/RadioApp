package cz.uhk.fim.zlesak.radioapp.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.GpsNotFixed
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SignalWifiConnectedNoInternet4
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cz.uhk.fim.zlesak.radioapp.R
import cz.uhk.fim.zlesak.radioapp.api.ApiResult
import cz.uhk.fim.zlesak.radioapp.data.Language
import cz.uhk.fim.zlesak.radioapp.router.NetworkMonitorHelper
import cz.uhk.fim.zlesak.radioapp.ui.composeItems.CPI
import cz.uhk.fim.zlesak.radioapp.ui.composeItems.CountrySelector
import cz.uhk.fim.zlesak.radioapp.ui.composeItems.HeaderComponent
import cz.uhk.fim.zlesak.radioapp.ui.composeItems.LanguageSelector
import cz.uhk.fim.zlesak.radioapp.ui.composeItems.RadioListCompose
import cz.uhk.fim.zlesak.radioapp.ui.composeItems.SadComponent
import cz.uhk.fim.zlesak.radioapp.ui.composeItems.TagSelector
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
    val countries by radioViewModel.radioCountryList.collectAsState()
    val tags by radioViewModel.radioTagList.collectAsState()
    val languages by radioViewModel.radioLanguageList.collectAsState()

    val radioCountryName by radioViewModel.radioCountryName.collectAsState()
    val radioLanguageName by radioViewModel.radioLanguageName.collectAsState()
    val radioTagName by radioViewModel.radioTagName.collectAsState()
    var searchText by remember { mutableStateOf(TextFieldValue("")) }

    val loc by radioSearchViewModel.loc.collectAsState()

    var empty = true

    var selectedCountryIndex by remember { mutableIntStateOf(0) }
    var selectedTagIndex by remember { mutableIntStateOf(0) }
    var selectedLanguageIndex by remember { mutableIntStateOf(0) }

    val networkMonitor = remember { NetworkMonitorHelper(context) }
    val isOnline by networkMonitor.isOnline.collectAsState(initial = false)

    LaunchedEffect(isOnline) {
        radioViewModel.getRadioStationsCountries()
        radioViewModel.getRadioStationsTags()
        radioViewModel.getRadioStationsLanguages()

        viewModel.loadFavoriteRadios()
        radioSearchViewModel.getPosition(context)
        radioViewModel.clearSearchedRadioList()
    }
    LaunchedEffect(loc) {
        if (loc != null && isOnline) {
            radioViewModel.getRadioStationsFromLocation(
                lat = loc!!.latitude,
                long = loc!!.longitude
            )
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Row {
            HeaderComponent(R.string.search_headline)
        }
        Row(
            modifier = Modifier.conditional(!empty) { weight(2f) }
        )//search
        {
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
                            if (searchText.text.isNotEmpty() || selectedCountryIndex != 0 || selectedTagIndex != 0 || selectedLanguageIndex != 0) {
                                radioViewModel.getSearchedRadioStations(
                                    name = searchText.text,
                                    country = radioCountryName,
                                    language = radioLanguageName,
                                    tag = radioTagName
                                )
                            }
                            else {
                                searchText = TextFieldValue("")
                                radioViewModel.clearSearchedRadioList()
                            }
                        }
                    ) {
                        if (radioList is ApiResult.Loading || radioList is ApiResult.Error)
                            Icon(Icons.Filled.Search, stringResource(R.string.search_icon))
                        else
                            Icon(Icons.Filled.Clear, stringResource(R.string.search_icon))
                    }
                }
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
                        when (countries) {
                            is ApiResult.Success -> {
                                CountrySelector(
                                    countries = countries,
                                    selectedIndex = selectedCountryIndex,
                                    onCountrySelected = { cn ->
                                        radioViewModel.selectedCountry(cn)
                                        Log.d(this::class.toString(), "$cn is this")
                                    },
                                    onItemSelectedIndex = { ind ->
                                        selectedCountryIndex = ind
                                    }
                                )
                            }

                            is ApiResult.Loading -> {
                                CPI()
                            }

                            is ApiResult.Error -> {
                                Text("Error loading countries")
                            }
                        }
                        when (tags) {
                            is ApiResult.Success -> {
                                TagSelector(
                                    tags = tags,
                                    selectedIndex = selectedTagIndex,
                                    onTagSelected = { cn ->
                                        radioViewModel.selectedTag(cn)
                                        Log.d(this::class.toString(), "$cn is this")
                                    },
                                    onItemSelectedIndex = { ind ->
                                        selectedTagIndex = ind
                                    }
                                )
                            }

                            is ApiResult.Loading -> {
                                CPI()
                            }

                            is ApiResult.Error -> {
                                Text("Error loading countries")
                            }
                        }
                        when (languages) {
                            is ApiResult.Success -> {
                                LanguageSelector (
                                    languages = languages,
                                    selectedIndex = selectedLanguageIndex,
                                    onLanguageSelected = { cn ->
                                        radioViewModel.selectedLanguage(cn)
                                        Log.d(this::class.toString(), "$cn is this")
                                    },
                                    onItemSelectedIndex = { ind ->
                                        selectedLanguageIndex = ind
                                    }
                                )
                            }

                            is ApiResult.Loading -> {
                                CPI()
                            }

                            is ApiResult.Error -> {
                                Text("Error loading countries")
                            }
                        }
                    }
                }
                //listing
                Row {
                    when (radioList) {
                        is ApiResult.Error -> {
                            val errorMessage = (radioList as ApiResult.Error).message
                            Text(text = "Error: $errorMessage")
                        }

                        is ApiResult.Loading -> {}

                        is ApiResult.Success -> {
                            val list = (radioList as ApiResult.Success).data
                            RadioListCompose(list, favorites, navController)
                            empty = list.isEmpty()
                        }
                    }
                }
            }
        }

        //location
        Row(modifier = Modifier.weight(1f)) {
            Column {
                HeaderComponent(R.string.radio_search_radius_info)
                if (!isOnline) {
                    SadComponent(
                        Icons.Filled.SignalWifiConnectedNoInternet4,
                        R.string.no_internet,
                        text1 = R.string.no_internet_message,
                        text2 = R.string.no_internet_help
                    )
                } else {
                    when (gpsRadioList) {
                        is ApiResult.Error -> {
                            val errorMessage = (gpsRadioList as ApiResult.Error).message
                            Text(text = "Error: $errorMessage")
                        }

                        is ApiResult.Loading -> {
                            CPI()
                        }

                        is ApiResult.Success -> {
                            val list = (gpsRadioList as ApiResult.Success).data
                            if (list.isNotEmpty()) {
                                RadioListCompose(list, favorites, navController)
                            } else {
                                SadComponent(
                                    Icons.Filled.GpsNotFixed,
                                    R.string.no_near_icon,
                                    R.string.no_radio_in_near,
                                    R.string.no_radio_near_help
                                )
                            }
                        }
                    }
                }
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