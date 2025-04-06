package cz.uhk.fim.zlesak.radioapp.repository

import cz.uhk.fim.zlesak.radioapp.data.RadioStation
import cz.uhk.fim.zlesak.radioapp.data.RadioStationFavoriteEntity
import cz.uhk.fim.zlesak.radioapp.data.RadioStationFavoriteEntity_
import io.objectbox.Box
import io.objectbox.query.QueryBuilder

class RadioFavoriteRepository(private val radioFavoriteBox : Box<RadioStationFavoriteEntity>) {
    fun addFavoriteRadio(radio :RadioStation){
        val query = radioFavoriteBox.query()
            .equal(RadioStationFavoriteEntity_.uuid, radio.stationuuid, QueryBuilder.StringOrder.CASE_SENSITIVE)
            .build()
        val result = query.findFirst()
        if(result == null) {
            val rad = RadioStationFavoriteEntity(uuid = radio.stationuuid, name = radio.name)
            radioFavoriteBox.put(rad)
        }
        query.close()
    }
    fun removeFavoriteRadio(uuid : String){
        val query = radioFavoriteBox.query()
            .equal(RadioStationFavoriteEntity_.uuid, uuid, QueryBuilder.StringOrder.CASE_SENSITIVE)
            .build()
        val result = query.findFirst()
        if(result != null)
            radioFavoriteBox.remove(result)
        query.close()
    }
    fun getAllFavoriteRadios() : List<RadioStationFavoriteEntity> {
        return radioFavoriteBox.all
    }
}