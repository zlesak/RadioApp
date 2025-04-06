package cz.uhk.fim.zlesak.radioapp

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import cz.uhk.fim.zlesak.radioapp.api.IRadioApi
import cz.uhk.fim.zlesak.radioapp.api.RadioApiInterceptor
import cz.uhk.fim.zlesak.radioapp.data.MyObjectBox
import cz.uhk.fim.zlesak.radioapp.data.RadioStationFavoriteEntity
import cz.uhk.fim.zlesak.radioapp.data.RadioStationHistoryEntity
import cz.uhk.fim.zlesak.radioapp.repository.RadioFavoriteRepository
import cz.uhk.fim.zlesak.radioapp.repository.RadioHistoryRepository
import cz.uhk.fim.zlesak.radioapp.viewModels.RadioFavoriteViewModel
import cz.uhk.fim.zlesak.radioapp.viewModels.RadioHistoryViewModel
import cz.uhk.fim.zlesak.radioapp.viewModels.RadioSearchViewModel
import cz.uhk.fim.zlesak.radioapp.viewModels.RadioViewModel
import io.objectbox.BoxStore
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val repositoryModule = module {
    single { RadioFavoriteRepository(get(named("favoritesBox"))) }
    single { RadioHistoryRepository(get(named("historyBox"))) }
}
val viewModelModule = module {
    viewModel { RadioFavoriteViewModel(get(), get()) }
    viewModel { RadioHistoryViewModel(get(), get()) }
    viewModel { RadioViewModel(get()) }
    viewModel { RadioSearchViewModel() }
}

val networkModule = module {
    single { provideOkHttpClient() }
    single { provideRetrofit(get()) }
    single { provideRadioApi(get()) }
}
val objectBoxModule = module {
    single {
        MyObjectBox.builder()
            .androidContext(androidContext())
            .build()
    }
    single (named("favoritesBox")){ get<BoxStore>().boxFor(RadioStationFavoriteEntity::class.java) }
    single (named("historyBox")){ get<BoxStore>().boxFor(RadioStationHistoryEntity::class.java) }
}
val imageModule = module{
    single {
        provideImageLoader(androidContext())
    }
}

fun provideOkHttpClient(): OkHttpClient {
    val radioApis = listOf(
        "https://de1.api.radio-browser.info/json/",
        "https://at1.api.radio-browser.info/json/",
        "https://fi1.api.radio-browser.info/json/",
        "https://de2.api.radio-browser.info/json/"
    )
    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    val userAgentInterceptor = Interceptor { chain ->
        val request = chain.request()
            .newBuilder()
            .header("User-Agent", "RadioApp/1.0 (Android; Kotlin)")
            .build()
        chain.proceed(request)
    }

    return OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(userAgentInterceptor)
        .addInterceptor(RadioApiInterceptor(radioApis))
        .build()
}

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl("http://at1.radio-browser.info/json/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun provideRadioApi(retrofit: Retrofit): IRadioApi {
    return retrofit.create(IRadioApi::class.java)
}

fun provideImageLoader(androidContext: Context) : ImageLoader {
    return ImageLoader.Builder(androidContext)
        .memoryCache {
            MemoryCache.Builder(androidContext)
                .maxSizePercent(0.25)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(androidContext.cacheDir.resolve("image_cache"))
                .maxSizePercent(0.02)
                .build()
        }
        .build()
}