package cz.uhk.fim.zlesak.radioapp.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.uhk.fim.zlesak.radioapp.api.ApiResult
import cz.uhk.fim.zlesak.radioapp.api.IRadioApi
import cz.uhk.fim.zlesak.radioapp.data.Country
import cz.uhk.fim.zlesak.radioapp.data.Language
import cz.uhk.fim.zlesak.radioapp.data.RadioStation
import cz.uhk.fim.zlesak.radioapp.data.Tag
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
    val radioCountryName : StateFlow<String> = _radioCountryName.asStateFlow()

    private val _radioLanguageList = MutableStateFlow<ApiResult<List<Language>>>(ApiResult.Loading)
    val radioLanguageList : StateFlow<ApiResult<List<Language>>> = _radioLanguageList.asStateFlow()
    private val _radioLanguageName = MutableStateFlow("")
    val radioLanguageName : StateFlow<String> = _radioLanguageName.asStateFlow()

    private val _radioTagList = MutableStateFlow<ApiResult<List<Tag>>>(ApiResult.Loading)
    val radioTagList : StateFlow<ApiResult<List<Tag>>> = _radioTagList.asStateFlow()
    private val _radioTagName = MutableStateFlow("")
    val radioTagName : StateFlow<String> = _radioTagName.asStateFlow()

    fun selectedCountry(country : String) {
        _radioCountryName. value = country
    }
    fun selectedTag(tag : String) {
        _radioTagName. value = tag
    }
    fun selectedLanguage(language : String) {
        _radioLanguageName. value = language
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

    fun getSearchedRadioStations(offset: Int = 0, limit: Int = 15, name : String, country :String = "", language :String = "", tag :String = "" ){
        viewModelScope.launch {
            _searchedRadioList.value = ApiResult.Loading
            try {
                val response = radioApi.getSearchedRadioStations(offset, limit, name, country, language, tag)
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
                        val placeholderCountry = Country(name = "No country selected", iso_3166_1 = "", stationcount = 0)
                        val updatedData = listOf(placeholderCountry) + data
                        _radioCountryList.value = ApiResult.Success(updatedData)
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

    fun getRadioStationsLanguages(){
        viewModelScope.launch {
            try {
                val response = radioApi.getRadioStationsLanguages()
                if(response.isSuccessful){
                    val data : List<Language>? = response.body()

                    if(data != null){
                        val placeholderLanguage = Language(name = "No Language selected", iso_639 = "", stationcount = 0)
                        val updatedData = listOf(placeholderLanguage) + data
                        _radioLanguageList.value = ApiResult.Success(updatedData)
                        Log.d(this::class.toString(), "Data for getRadioStationsLanguages fetched successfully")
                    }else{
                        _radioLanguageList.value = ApiResult.Error("Data null")
                    }
                }else{
                    _radioLanguageList.value = ApiResult.Error("Error while getting data from getRadioStationsLanguages api: ${response.message()}")
                    Log.d(this::class.toString(), "Error while getting data from getRadioStationsLanguages api")
                }
            }
            catch (e : Exception){
                _radioLanguageList.value = ApiResult.Error("Exception happened when fetching data: ${e.message}")
                Log.d(this::class.toString(), "Exception happened when fetching data")
            }
        }
    }

    fun getRadioStationsTags(){
        viewModelScope.launch {
            try {
                val response = radioApi.getRadioStationsTags()
                if(response.isSuccessful){
                    val data : List<Tag>? = response.body()

                    if(data != null){
                        val placeholderTag = Tag(name = "No tag selected", stationcount = 0)
                        val updatedData = listOf(placeholderTag) + data
                        _radioTagList.value = ApiResult.Success(updatedData)
                        Log.d(this::class.toString(), "Data for getRadioStationsTags fetched successfully")
                    }else{
                        _radioTagList.value = ApiResult.Error("Data null")
                    }
                }else{
                    _radioTagList.value = ApiResult.Error("Error while getting data from getRadioStationsTags api: ${response.message()}")
                    Log.d(this::class.toString(), "Error while getting data from getRadioStationsTags api")
                }
            }
            catch (e : Exception){
                _radioTagList.value = ApiResult.Error("Exception happened when fetching data: ${e.message}")
                Log.d(this::class.toString(), "Exception happened when fetching data")
            }
        }
    }
}