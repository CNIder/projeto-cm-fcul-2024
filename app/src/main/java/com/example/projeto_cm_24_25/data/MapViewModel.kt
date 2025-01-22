package com.example.projeto_cm_24_25.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projeto_cm_24_25.data.model.AlertData
import com.example.projeto_cm_24_25.data.model.ItemMarker
import com.example.projeto_cm_24_25.data.repository.AlertRepository
import com.example.projeto_cm_24_25.data.repository.MapRepository
import kotlinx.coroutines.launch

class MapViewModel: ViewModel() {
    private val mapRepository : MapRepository = MapRepository()
    private val alertRepository : AlertRepository = AlertRepository()

    private val _markersData = MutableLiveData<List<ItemMarker>>()
    val markerData : LiveData<List<ItemMarker>> = _markersData

    private val _userAlerts = MutableLiveData<List<AlertData>>()
    val userAlerts : LiveData<List<AlertData>> = _userAlerts

    private val _showAlertNotification = MutableLiveData(true)
    val showAlertNotification : LiveData<Boolean> = _showAlertNotification

    fun getMarkers() {
        viewModelScope.launch {
            val markerResult = mapRepository.fetchMapData()
            _markersData.value = markerResult
        }
    }

    fun addMarker(marker: ItemMarker) {
        mapRepository.addMapData(marker)
    }

    fun addUserAlert(alertData: AlertData) {
        alertRepository.addUserAlert(alertData)
    }

    fun getAlerts() {
        viewModelScope.launch {
            val alertResult = alertRepository.fetchAlerts()
            _userAlerts.value = alertResult
        }
    }

    fun disableUserNotification() {
        _showAlertNotification.value = false
    }
}