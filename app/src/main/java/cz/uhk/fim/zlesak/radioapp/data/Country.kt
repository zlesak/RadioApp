package cz.uhk.fim.zlesak.radioapp.data

import com.google.gson.annotations.SerializedName

data class Country (
    @SerializedName("name") val name : String,
    @SerializedName("iso_3166_1") val iso_3166_1 : String,
    @SerializedName("stationcount") val stationcount : Number,
)