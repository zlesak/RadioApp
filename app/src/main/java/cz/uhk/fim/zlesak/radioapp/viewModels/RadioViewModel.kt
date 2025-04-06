package cz.uhk.fim.zlesak.radioapp.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.uhk.fim.zlesak.radioapp.api.ApiResult
import cz.uhk.fim.zlesak.radioapp.api.IRadioApi
import cz.uhk.fim.zlesak.radioapp.data.Country
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
    private val _gpsRadioList = MutableStateFlow<ApiResult<List<RadioStation>>>(ApiResult.Loading)
    val gpsRadioList : StateFlow<ApiResult<List<RadioStation>>> = _gpsRadioList.asStateFlow()
    private val _searchedRadioList = MutableStateFlow<ApiResult<List<RadioStation>>>(ApiResult.Loading)
    val searchedRadioList : StateFlow<ApiResult<List<RadioStation>>> = _searchedRadioList.asStateFlow()
    private val _radioCountryList = MutableStateFlow<ApiResult<List<Country>>>(ApiResult.Loading)
    val radioCountryList : StateFlow<ApiResult<List<Country>>> = _radioCountryList.asStateFlow()

    private val _radioCountryName = MutableStateFlow("")
//    val radioCountryName : StateFlow<String> = _radioCountryName.asStateFlow()

    fun selectedCountry(country : String) {
        _radioCountryName. value = country
    }

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

//    fun getRadioStations(offset : Number = 0, limit : Number = 15){
//        viewModelScope.launch {
//            _radioList.value = ApiResult.Loading
//            try {
//                val response = radioApi.getAllRadioStations(offset, limit)
//                if(response.isSuccessful){
//                    val data : List<RadioStation>? = response.body()
//
//                    if(data != null){
//                        _radioList.value = ApiResult.Success(data)
//                    }else{
//                        _radioList.value = ApiResult.Error("Data null")
//                    }
//                }else{
//                    _radioList.value = ApiResult.Error("Error while getting data from api: ${response.message()}")
//                }
//            }
//            catch (e : Exception){
//                _radioList.value = ApiResult.Error("Exception happened when fetching data: ${e.message}")
//            }
//        }
//    }

    fun getSearchedRadioStations(offset: Int = 0, limit: Int = 15, name : String, country :String = _radioCountryName.value, language :String = "", tagList :String = "" ){
        viewModelScope.launch {
            _searchedRadioList.value = ApiResult.Loading
            try {
                val response = radioApi.getSearchedRadioStations(offset, limit, name, country, language, tagList)
                if(response.isSuccessful){
                    val data : List<RadioStation>? = response.body()

                    if(data != null){
                        _searchedRadioList.value = ApiResult.Success(data)
                    }else{
                        _searchedRadioList.value = ApiResult.Error("Data null")
                    }
                }else{
                    _searchedRadioList.value = ApiResult.Error("Error while getting data from api: ${response.message()}")
                }
            }
            catch (e : Exception){
                _searchedRadioList.value = ApiResult.Error("Exception happened when fetching data: ${e.message}")
            }
        }
    }

    fun getTopClickRadioStations(offset: Int = 0, limit: Int = 15){
        viewModelScope.launch {
            _radioList.value = ApiResult.Loading
            try {
                val response = radioApi.getTopClickRadioStations(offset, limit)
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

    fun getRadioStationsFromLocation(offset: Int = 0, limit: Int = 15, lat : Double, long : Double, distance : Number = 10000){
        viewModelScope.launch {
            _gpsRadioList.value = ApiResult.Loading
            try {
                val response = radioApi.getRadioStationsFromLocation(offset, limit, lat, long, distance)
                if(response.isSuccessful){
                    val data : List<RadioStation>? = response.body()

                    if(data != null){
                        _gpsRadioList.value = ApiResult.Success(data)
                        Log.d("RadioViewModel", "Data for getRadioStationsFromLocation fetched successfully")
                    }else{
                        _gpsRadioList.value = ApiResult.Error("Data null")
                    }
                }else{
                    _gpsRadioList.value = ApiResult.Error("Error while getting data from api: ${response.message()}")
                }
            }
            catch (e : Exception){
                _gpsRadioList.value = ApiResult.Error("Exception happened when fetching data: ${e.message}")
            }
        }
    }

    fun clearSearchedRadioList(){
        viewModelScope.launch {
            _searchedRadioList.value = ApiResult.Loading
            Log.d("RadioViewModel", "Cleared searched radio data")
        }
    }

    fun getRadioStationsCountries(){
        viewModelScope.launch {
            try {
                val response = radioApi.getRadioStationsCountryCodes()
                if(response.isSuccessful){
                    val data : List<Country>? = response.body()

                    if(data != null){
                        _radioCountryList.value = ApiResult.Success(data)
                        Log.d(this::class.toString(), "Data for getRadioStationsCountryCodes fetched successfully")
                    }else{
                        _radioCountryList.value = ApiResult.Error("Data null")
                    }
                }else{
                    _radioCountryList.value = ApiResult.Error("Error while getting data from getRadioStationsCountryCodes api: ${response.message()}")
                    Log.d(this::class.toString(), "Error while getting data from getRadioStationsCountryCodes api")
                }
            }
            catch (e : Exception){
                _radioCountryList.value = ApiResult.Error("Exception happened when fetching data: ${e.message}")
                Log.d(this::class.toString(), "Exception happened when fetching data")
            }
        }
    }
}