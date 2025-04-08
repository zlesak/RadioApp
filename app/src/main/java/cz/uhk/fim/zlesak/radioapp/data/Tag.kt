package cz.uhk.fim.zlesak.radioapp.data;

import com.google.gson.annotations.SerializedName

data class Tag (
    @SerializedName("name") val name : String,
    @SerializedName("stationcount") val stationcount : Number,
)
