package com.example.projeto_cm_24_25.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projeto_cm_24_25.data.model.Blog
import com.example.projeto_cm_24_25.data.model.ItemMarker
import com.example.projeto_cm_24_25.data.repository.MapRepository
import kotlinx.coroutines.launch

class MapViewModel: ViewModel() {
    val mapRepository : MapRepository = MapRepository()
    private val _markersData = MutableLiveData<List<ItemMarker>>()
    val markerData : LiveData<List<ItemMarker>> = _markersData

    fun getMarkers() {
        viewModelScope.launch {
            val markerResult = mapRepository.fetchBlogData()
            _markersData.value = markerResult
        }
    }

    fun addMarker(marker: ItemMarker) {
        mapRepository.addBlogData(marker)
    }
}