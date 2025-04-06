package cz.uhk.fim.zlesak.radioapp.data
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class RadioStationHistoryEntity (
    @Id
    var id :Long = 0,
    var uuid :String,
    var name :String
)
