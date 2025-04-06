package cz.uhk.fim.zlesak.radioapp.repository

import cz.uhk.fim.zlesak.radioapp.data.RadioStation
import cz.uhk.fim.zlesak.radioapp.data.RadioStationHistoryEntity
import cz.uhk.fim.zlesak.radioapp.data.RadioStationHistoryEntity_
import io.objectbox.Box
import io.objectbox.query.QueryBuilder

class RadioHistoryRepository (private val radioHistoryBox : Box<RadioStationHistoryEntity>) {
    fun addRadioToHistory(radio: RadioStation) {
        val query = radioHistoryBox.query()
            .equal(
                RadioStationHistoryEntity_.uuid,
                radio.stationuuid,
                QueryBuilder.StringOrder.CASE_SENSITIVE
            )
            .build()
        val result = query.findFirst()
        if (result != null) {
            radioHistoryBox.remove(result)
        }
        val recentRadio = RadioStationHistoryEntity(uuid = radio.stationuuid, name = radio.name)
        radioHistoryBox.put(recentRadio)

        query.close()
    }

    fun clearHistory() {
        radioHistoryBox.removeAll()
    }

    fun getHistory(): List<RadioStationHistoryEntity> {
        return radioHistoryBox.all.sortedByDescending { it.createdAt }
    }
}