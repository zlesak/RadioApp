package cz.uhk.fim.zlesak.radioapp.router

object Routes {
    const val RadioHome = "radioHome"
    const val RadioHistory = "radioHistory"
    const val RadioFavorites = "radioFavorite"
    const val RadioSearch = "radioSearch"
    const val RadioDetail = "stations/{uuid}"

    fun radioStationDetail (uuid : String) : String{
        return "stations/$uuid"
    }
}