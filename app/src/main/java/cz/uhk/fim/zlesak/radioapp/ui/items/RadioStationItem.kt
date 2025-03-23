package cz.uhk.fim.zlesak.radioapp.ui.items

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import cz.uhk.fim.zlesak.radioapp.R
import cz.uhk.fim.zlesak.radioapp.data.RadioStation
import cz.uhk.fim.zlesak.radioapp.router.Routes
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import cz.uhk.fim.zlesak.radioapp.viewModels.RadioFavoriteViewModel

@Composable
fun RadioStationItem(
    radio: RadioStation,
    navController: NavController,
    isFavorite: Boolean = false,
    viewModel: RadioFavoriteViewModel = koinViewModel(),
    imageLoader: ImageLoader = koinInject()
) {
    val isSvg = radio.favicon.contains(".svg", ignoreCase = true) || radio.favicon.contains(".gif", ignoreCase = true)
    var failed by remember { mutableStateOf(false) }
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable {
                    navController.navigate(Routes.radioStationDetail(radio.stationuuid))
                }
    ) {
        if(!failed && !isSvg && radio.favicon != ""){
            SubcomposeAsyncImage(
                model = radio.favicon,
                imageLoader = imageLoader,
                contentDescription = "${radio.name} icon",
                modifier = Modifier.size(48.dp),
                loading = { CircularProgressIndicator() },
                error = {failed = true},
                onError = {
                    Log.d("AsyncImage", "Failed to load image: ${radio.favicon}")
                }
            )
        }
        if(failed || radio.favicon == "" || isSvg){
            Icon(Icons.Filled.Radio, contentDescription = stringResource(R.string.radio_icon),
                modifier = Modifier.size(48.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = radio.name, fontSize = 16.sp, modifier = Modifier
                .height(48.dp)
                .wrapContentHeight(align = Alignment.CenterVertically))
        }
        IconButton(onClick = {
            navController.navigate("stations/${radio.stationuuid}")
        }) {
            Icon(Icons.Filled.Info, contentDescription = stringResource(R.string.info_icon_desc))
        }
        IconButton(onClick = {
            if (!isFavorite)
                viewModel.addFavoriteRadio(radio)
            else
                viewModel.removeFavoriteRadio(radio.stationuuid)
        }) {
            if (isFavorite)
                Icon(Icons.Filled.Favorite, contentDescription = stringResource(R.string.favorite_icon_empty))
            else
                Icon(Icons.Filled.FavoriteBorder, contentDescription = stringResource(R.string.favorite_icon_full))
        }
    }

}