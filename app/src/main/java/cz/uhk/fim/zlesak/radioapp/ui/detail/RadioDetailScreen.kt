package cz.uhk.fim.zlesak.radioapp.ui.detail

import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import cz.uhk.fim.zlesak.radioapp.R
import cz.uhk.fim.zlesak.radioapp.api.ApiResult
import cz.uhk.fim.zlesak.radioapp.viewModels.RadioViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import cz.uhk.fim.zlesak.radioapp.viewModels.RadioFavoriteViewModel

@OptIn(UnstableApi::class)
@Composable
fun RadioDetailScreen(
    navController: NavController,
    radioUuid: String,
    radioViewModel: RadioViewModel = koinViewModel(),
    radioFavoriteViewModel: RadioFavoriteViewModel = koinViewModel(),
    imageLoader: ImageLoader = koinInject()

) {
    val context = LocalContext.current
    val favoriteRadio by radioFavoriteViewModel.radioFavoriteList.collectAsState()
    val exoPlayer: ExoPlayer? by remember { mutableStateOf(ExoPlayer.Builder(context).build()) }
    var isPlaying by remember { mutableStateOf(false) }
    val radioDetail by radioViewModel.radio.collectAsState()


    LaunchedEffect(Unit) {
        radioViewModel.getRadioByUuid(radioUuid)
    }
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer?.release()
        }
    }

   fun playStream(url: String) {
        try {
            exoPlayer?.apply {
                setMediaItem(MediaItem.fromUri(url))
                prepare()
                play()
            }
            isPlaying = true
        } catch (e: Exception) {
            isPlaying = false
            Log.e("RadioPlayer", "Error playing stream", e)
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (radioDetail) {
            is ApiResult.Error -> {
                val errorMessage = (radioDetail as ApiResult.Error).message
                Text(text = "Error: $errorMessage")
            }

            is ApiResult.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            is ApiResult.Success -> {
                val radioData = (radioDetail as ApiResult.Success).data.first()
                val isFavourite = if (favoriteRadio is ApiResult.Success) {
                    (favoriteRadio as ApiResult.Success).data.any { it.stationuuid == radioData.stationuuid }
                } else {
                    false
                }
                AsyncImage(
                    model = radioData.favicon,
                    imageLoader = imageLoader,
                    contentDescription = "${radioData.name} icon",
                    modifier = Modifier.size(48.dp)
                )
                Text(text = radioData.name, fontWeight = FontWeight.Bold)
                Text(text = "${stringResource(R.string.radio_website)}: ${radioData.homepage}") //TODO add html to make clickable URL
                Text(text = "${stringResource(R.string.country_name)}: ${radioData.country} (${radioData.countrycode})")
                Text(text = "${stringResource(R.string.click_count)}: ${radioData.clickcount} ")
                Button(
                    onClick = {
                        if (isPlaying.not()) {
                            playStream(radioData.url)
                        } else {
                            exoPlayer?.apply {
                                stop()
                                release()
                            }
                            isPlaying = false
                        }
                    }) {
                    if (isPlaying) {
                        Icon(Icons.Filled.Pause, contentDescription = "Pause")
                        Text("Pause")
                    } else {
                        Icon(
                            Icons.Filled.PlayArrow,
                            contentDescription = stringResource(R.string.play_button_description)
                        )
                        Text(text = stringResource(R.string.play_radio_station))
                    }
                }
                IconButton(onClick = {
                    if(isFavourite)
                        radioFavoriteViewModel.removeFavoriteRadio(radioData.stationuuid)
                    else
                        radioFavoriteViewModel.addFavoriteRadio(radioData)
                }) {
                    if (isFavourite) Icon(
                        Icons.Filled.Favorite,
                        contentDescription = stringResource(R.string.favorite_icon_full)
                    )
                    else Icon(
                        Icons.Filled.FavoriteBorder,
                        contentDescription = stringResource(R.string.favorite_icon_empty)
                    )
                }
                Button(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.go_back))
                    Text(stringResource(R.string.go_back))
                }
            }
        }
    }
}
