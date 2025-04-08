package cz.uhk.fim.zlesak.radioapp.ui.composeItems

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.google.android.gms.common.api.Api
import cz.uhk.fim.zlesak.radioapp.api.ApiResult
import cz.uhk.fim.zlesak.radioapp.data.RadioStation
import cz.uhk.fim.zlesak.radioapp.ui.items.RadioStationItem

@Composable
fun RadioListCompose(list : List<RadioStation>, favorites :  ApiResult<List<RadioStation>>, navController: NavController) {
    LazyColumn {
        items(list) { radio ->
            val isFavorite = if (favorites is ApiResult.Success) {
                favorites.data.any { it.stationuuid == radio.stationuuid }
            } else {
                false
            }
            RadioStationItem(
                radio,
                navController,
                isFavorite = isFavorite
            )
            HorizontalDivider()
        }
    }
}