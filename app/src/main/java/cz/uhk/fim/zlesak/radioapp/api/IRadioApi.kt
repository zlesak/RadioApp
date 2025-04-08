package cz.uhk.fim.zlesak.radioapp.api

import cz.uhk.fim.zlesak.radioapp.data.Country
import cz.uhk.fim.zlesak.radioapp.data.Language
import cz.uhk.fim.zlesak.radioapp.data.RadioStation
import cz.uhk.fim.zlesak.radioapp.data.Tag
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IRadioApi {
    @GET("stations")
    suspend fun getAllRadioStations(@Query("offset") offset : Number ,@Query("limit") limit : Number) : Response<List<RadioStation>>

    @GET("stations/search") //TODO BASED ON WHAT ALL WANTS TO BE IMPLEMENTED
    suspend fun getSearchedRadioStations(
        @Query("offset") offset : Int,
        @Query("limit") limit : Int,
        @Query("name") name: String?,
        @Query("country") country : String?,
        @Query("language") language: String?,
        @Query("tagList") tagList: String?
    ) : Response<List<RadioStation>>

    @GET("stations/topclick")
    suspend fun getTopClickRadioStations( @Query("offset") offset: Int = 0, @Query("limit") limit : Number = 15) : Response<List<RadioStation>>

    @GET("stations/byuuid")
    suspend fun getRadioStationDetails(@Query("uuids") uuids : String) : Response<List<RadioStation>>

    @GET("stations/search")
    suspend fun getRadioStationsFromLocation(
        @Query("offset") offset : Int,
        @Query("limit") limit : Int,
        @Query("geo_lat") lat : Number,
        @Query("geo_long") long : Number,
        @Query("geo_distance") distance : Number
    ) : Response<List<RadioStation>>
    @GET("countries")
    suspend fun getRadioStationsCountryCodes() : Response<List<Country>>
    @GET("languages")
    suspend fun getRadioStationsLanguages() : Response<List<Language>>
    @GET("tags")
    suspend fun getRadioStationsTags() : Response<List<Tag>>
}
