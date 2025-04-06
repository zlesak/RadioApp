package cz.uhk.fim.zlesak.radioapp.viewModels

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class RadioSearchViewModel() : ViewModel() {
    private val _loc = MutableStateFlow<Location?>(null)
    val loc: StateFlow<Location?> = _loc.asStateFlow()

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
                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token).addOnSuccessListener { location ->
                    if (location != null) {
                        Log.d("RadioSearchViewModel", "Location fetched successfully ${location}")
                        location.let {
                            _loc.value = location
                        }
                    } else {
                        _loc.value = null
                        Log.e("RadioSearchViewModel", "Last location is null")
                    }
                }.addOnFailureListener {
                    Log.e("RadioSearchViewModel", "Last location fetch failed")

                }
            }
        }
    }
}