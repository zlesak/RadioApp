package cz.uhk.fim.zlesak.radioapp.viewModels

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RadioSearchViewModel() : ViewModel() {
    private val _lat = MutableStateFlow<Double?>(null)
    val lat: StateFlow<Double?> = _lat.asStateFlow()
    private val _long = MutableStateFlow<Double?>(null)
    val long: StateFlow<Double?> = _long.asStateFlow()

    fun getPosition(context: Context) {
        viewModelScope.launch {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        val location = task.result
                        location?.let {
                            _lat.value = it.latitude
                            _long.value = it.longitude
                        }
                    } else {
                        Log.e("RadioSearchViewModel", "Last location is null")
                    }
                }
            }
        }
    }
}