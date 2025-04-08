package cz.uhk.fim.zlesak.radioapp.data

import com.google.gson.annotations.SerializedName

data class Language (
    @SerializedName("name") val name : String,
    @SerializedName("iso_639") val iso_639 : String,
    @SerializedName("stationcount") val stationcount : Number,
)