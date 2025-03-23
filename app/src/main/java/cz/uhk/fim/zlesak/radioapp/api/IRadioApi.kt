package cz.uhk.fim.zlesak.radioapp.api

import cz.uhk.fim.zlesak.radioapp.data.RadioStation
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IRadioApi {
    @GET("stations")
    suspend fun getAllRadioStations(@Query("offset") offset : Number ,@Query("limit") limit : Number) : Response<List<RadioStation>>

    @GET("stations/search") //TODO BASED ON WHAT ALL WANTS TO BE IMPLEMENTED
    suspend fun getSearchedRadioStations() : Response<List<RadioStation>>

    @GET("stations/topvote")
    suspend fun getTopRadioStations(@Query("limit") limit : Number) : Response<List<RadioStation>>

    @GET("stations/byuuid")
    suspend fun getRadioStationDetails(@Query("uuids") uuids : String) : Response<List<RadioStation>>
}
