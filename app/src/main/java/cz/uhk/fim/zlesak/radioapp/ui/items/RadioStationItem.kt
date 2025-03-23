package cz.uhk.fim.zlesak.radioapp.ui.items

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.TypedArrayUtils.getText
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import cz.uhk.fim.zlesak.radioapp.R
import cz.uhk.fim.zlesak.radioapp.data.RadioStation
import cz.uhk.fim.zlesak.radioapp.router.Routes
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.math.RoundingMode
import androidx.core.net.toUri
import cz.uhk.fim.zlesak.radioapp.repository.RadioFavoriteRepository
import cz.uhk.fim.zlesak.radioapp.viewModels.RadioFavoriteViewModel

@Composable
fun RadioStationItem(
    radio: RadioStation,
    navController: NavController,
    isFavorite: Boolean = false,
    viewModel: RadioFavoriteViewModel = koinViewModel(),
    imageLoader: ImageLoader = koinInject()
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable {
                    navController.navigate(Routes.radioStationDetail(radio.stationuuid))
                }
    ) {
        if(radio.favicon != ""){
            AsyncImage(
                model = radio.favicon,
                imageLoader = imageLoader,
                contentDescription = "${radio.name} icon",
                modifier = Modifier.size(48.dp)
            )
        }else{
            Icon(Icons.Filled.Radio, contentDescription = stringResource(R.string.radio_icon),
                modifier = Modifier.size(48.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = radio.name, fontSize = 16.sp, modifier = Modifier.height(48.dp).wrapContentHeight(align = Alignment.CenterVertically))
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