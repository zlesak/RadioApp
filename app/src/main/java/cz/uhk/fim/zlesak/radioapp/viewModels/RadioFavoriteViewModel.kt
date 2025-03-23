package cz.uhk.fim.zlesak.radioapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.uhk.fim.zlesak.radioapp.api.ApiResult
import cz.uhk.fim.zlesak.radioapp.api.IRadioApi
import cz.uhk.fim.zlesak.radioapp.data.RadioStation
import cz.uhk.fim.zlesak.radioapp.repository.RadioFavoriteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RadioFavoriteViewModel(private val radioRepository : RadioFavoriteRepository, private val radioApi: IRadioApi) : ViewModel() {
    private val _radioFavoriteList = MutableStateFlow<ApiResult<List<RadioStation>>>(ApiResult.Loading)
    val radioFavoriteList = _radioFavoriteList.asStateFlow()

    init{
        loadFavoriteRadios()
    }

    fun addFavoriteRadio(radio: RadioStation){
        viewModelScope.launch {
            radioRepository.addFavoriteRadio(radio)
            loadFavoriteRadios()
        }
    }
    fun removeFavoriteRadio(uuid: String){
        viewModelScope.launch {
            radioRepository.removeFavoriteRadio(uuid)
            loadFavoriteRadios()
        }
    }

    fun loadFavoriteRadios (){
        viewModelScope.launch {
            try {
                val favoriteEntities = radioRepository.getAllFavoriteRadios()
                if (favoriteEntities.isEmpty()) {
                    _radioFavoriteList.value = ApiResult.Success(emptyList())
                } else {
                    val uuids = favoriteEntities.joinToString(",") { it.uuid }
                    val result = radioApi.getRadioStationDetails(uuids)
                    if (result.isSuccessful) {
                        val data = result.body()
                        if(data != null){
                            _radioFavoriteList.value = ApiResult.Success(data)
                        }else{
                            _radioFavoriteList.value = ApiResult.Error("Data null")
                        }
                    } else {
                        _radioFavoriteList.value =
                            ApiResult.Error("Error while getting favorite radios from APi ${result.message()}")
                    }
                }
            }catch (ex : Exception){
                _radioFavoriteList.value = ApiResult.Error("Exception ${ex.message}")
            }
        }
    }
}