package cz.uhk.fim.zlesak.radioapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.uhk.fim.zlesak.radioapp.api.ApiResult
import cz.uhk.fim.zlesak.radioapp.api.IRadioApi
import cz.uhk.fim.zlesak.radioapp.data.RadioStation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RadioViewModel (private val radioApi : IRadioApi) :ViewModel(){
    private val _radio = MutableStateFlow<ApiResult<List<RadioStation>>>(ApiResult.Loading)
    val radio : StateFlow<ApiResult<List<RadioStation>>> = _radio.asStateFlow()
    private val _radioList = MutableStateFlow<ApiResult<List<RadioStation>>>(ApiResult.Loading)
    val radioList : StateFlow<ApiResult<List<RadioStation>>> = _radioList.asStateFlow()

    fun getRadioByUuid(uuid : String){
        viewModelScope.launch {
            _radio.value = ApiResult.Loading
            try {
                val response = radioApi.getRadioStationDetails(uuid)
                if(response.isSuccessful){
                    val data : List<RadioStation>? = response.body()

                    if(data != null){
                        _radio.value = ApiResult.Success(data)
                    }else{
                        _radio.value = ApiResult.Error("Data null")
                    }
                }else{
                    _radio.value = ApiResult.Error("Error while getting data from api: ${response.message()}")
                }
            }
            catch (e : Exception){
                _radio.value = ApiResult.Error("Exception happened when fetching data: ${e.message}")
            }
        }
    }

    fun getRadioStations(offset : Number = 0, limit : Number = 15){
        viewModelScope.launch {
            _radioList.value = ApiResult.Loading
            try {
                val response = radioApi.getAllRadioStations(offset, limit)
                if(response.isSuccessful){
                    val data : List<RadioStation>? = response.body()

                    if(data != null){
                        _radioList.value = ApiResult.Success(data)
                    }else{
                        _radioList.value = ApiResult.Error("Data null")
                    }
                }else{
                    _radioList.value = ApiResult.Error("Error while getting data from api: ${response.message()}")
                }
            }
            catch (e : Exception){
                _radioList.value = ApiResult.Error("Exception happened when fetching data: ${e.message}")
            }
        }
    }


}