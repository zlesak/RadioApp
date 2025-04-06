package cz.uhk.fim.zlesak.radioapp.ui.screens

import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import cz.uhk.fim.zlesak.radioapp.viewModels.RadioHistoryViewModel

@OptIn(UnstableApi::class)
@Composable
fun RadioDetailScreen(
    navController: NavController,
    radioUuid: String,
    radioViewModel: RadioViewModel = koinViewModel(),
    radioFavoriteViewModel: RadioFavoriteViewModel = koinViewModel(),
    radioHistoryViewModel: RadioHistoryViewModel = koinViewModel(),
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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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
                radioHistoryViewModel.addToHistory(radioData)
                AsyncImage(
                    model = radioData.favicon,
                    imageLoader = imageLoader,
                    contentDescription = "${radioData.name} icon",
                    modifier = Modifier.size(200.dp)
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text(text = radioData.name, fontWeight = FontWeight.Bold, fontSize = 35.sp) //TODO Line height fiddle
                Text(
                    text = "${radioData.country} (${radioData.countrycode})",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
                Text(buildAnnotatedString {
                    append("Radio website: ")
                    withLink(
                        LinkAnnotation.Url(
                            radioData.homepage,
                            TextLinkStyles(style = SpanStyle(color = Color.Blue))
                        )
                    ) {
                        append(" CLICK HERE ")
                    }
                })
                Text(text = "${stringResource(R.string.click_count)}: ${radioData.clickcount} ")
                Row {
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
                        if (isFavourite)
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
                    //TODO AD UPVOTE BUTTON
                }
                Button(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.go_back)
                    )
                    Text(stringResource(R.string.go_back))
                }
            }
        }
    }
}
