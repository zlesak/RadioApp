package cz.uhk.fim.zlesak.radioapp.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.uhk.fim.zlesak.radioapp.api.ApiResult
import cz.uhk.fim.zlesak.radioapp.api.IRadioApi
import cz.uhk.fim.zlesak.radioapp.data.RadioStation
import cz.uhk.fim.zlesak.radioapp.repository.RadioHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RadioHistoryViewModel(
    private val radioRepository: RadioHistoryRepository,
    private val radioApi: IRadioApi
) : ViewModel() {
    private val _radioHistoryList =
        MutableStateFlow<ApiResult<List<RadioStation>>>(ApiResult.Loading)
    val radioHistoryList = _radioHistoryList.asStateFlow()

    init {
        getHistory()
    }

    fun addToHistory(radio: RadioStation) {
        viewModelScope.launch {
            radioRepository.addRadioToHistory(radio)
            getHistory()
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            radioRepository.clearHistory()
            getHistory()
        }
    }

    fun getHistory() {
        viewModelScope.launch {
            try {
                val favoriteEntities = radioRepository.getHistory()
                if (favoriteEntities.isEmpty()) {
                    _radioHistoryList.value = ApiResult.Success(emptyList())
                } else {
                    val uuids = favoriteEntities.joinToString(",") { it.uuid }
                    val result = radioApi.getRadioStationDetails(uuids)
                    if (result.isSuccessful) {
                        val data = result.body()
                        if (data != null) {
                            _radioHistoryList.value = ApiResult.Success(data)
                            Log.i(
                                this::class.toString(),
                                "Data of radio history fetched successfully."
                            )
                        } else {
                            _radioHistoryList.value = ApiResult.Error("Data null")
                            Log.w(this::class.toString(), "Radio history is null")
                        }
                    } else {
                        _radioHistoryList.value =
                            ApiResult.Error("Error while getting radio history from APi ${result.message()}")
                        Log.e(
                            this::class.toString(),
                            "There has been an error when getting radio history"
                        )
                    }
                }
            } catch (ex: Exception) {
                _radioHistoryList.value = ApiResult.Error("Exception ${ex.message}")
            }
        }
    }
}