package cz.uhk.fim.zlesak.radioapp.data

import com.google.gson.annotations.SerializedName

data class RadioStation (
    @SerializedName("changeuuid") val changeuuid : String,
    @SerializedName("stationuuid") val stationuuid : String,
    @SerializedName("name") val name : String,
    @SerializedName("url") val url : String,
    @SerializedName("url_resolved") val url_resolved : String,
    @SerializedName("homepage") val homepage : String,
    @SerializedName("favicon") val favicon : String,
    @SerializedName("tags") val tags : String,
    @SerializedName("country") val country : String,
    @SerializedName("countrycode") val countrycode : String,
    @SerializedName("state") val state : String,
    @SerializedName("language") val language : String,
    @SerializedName("votes") val votes : Number,
    @SerializedName("lastchangetime") val lastchangetime : String,
    @SerializedName("codec") val codec : String,
    @SerializedName("bitrate") val bitrate : Number,
    @SerializedName("lastcheckok") val lastcheckok : Number,
    @SerializedName("lastchecktime") val lastchecktime : String,
    @SerializedName("lastcheckoktime") val lastcheckoktime : String,
    @SerializedName("clickcount") val clickcount : Number,
    @SerializedName("clicktrend") val clicktrend : Number,
    @SerializedName("ssl_error") val ssl_error : Number,
    @SerializedName("geo_lat") val geo_lat : Number,
    @SerializedName("geo_long") val geo_long : Number,
    @SerializedName("geo_distance") val geo_distance : Number
)