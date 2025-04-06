package cz.uhk.fim.zlesak.radioapp.api

import android.util.Log
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class RadioApiInterceptor(
    private val baseUrls: List<String>
) : Interceptor {

    @Volatile
    private var workingBaseUrl: String? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val urlsToTry = mutableListOf<String>()
        workingBaseUrl?.let { urlsToTry.add(it) }
        urlsToTry.addAll(baseUrls.filterNot { it == workingBaseUrl })

        for (baseUrl in urlsToTry) {
            val newUrl = originalRequest.url.newBuilder()
                .scheme(baseUrl.toHttpUrlOrNull()!!.scheme)
                .host(baseUrl.toHttpUrlOrNull()!!.host)
                .port(baseUrl.toHttpUrlOrNull()!!.port)
                .build()

            val newRequest = originalRequest.newBuilder()
                .url(newUrl)
                .build()

            try {
                val response = chain.proceed(newRequest)
                if (response.isSuccessful) {
                    workingBaseUrl = baseUrl
                    return response
                } else {
                    Log.d(this::class.toString(), "$baseUrl API response is unsuccessfully returned.")
                    response.close()
                }
            } catch (e: Exception) {
                Log.d(this::class.toString(), "$baseUrl API error.")
                // continue to next mirror
            }
        }
        Log.e(this::class.toString(), "There is no working api from defined list at this moment!")
        throw IOException("All Radio Browser API mirrors failed.")
    }
}
