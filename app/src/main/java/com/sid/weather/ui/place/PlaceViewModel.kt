package com.sid.weather.ui.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sid.weather.logic.dao.Repository
import com.sid.weather.logic.model.Place

class PlaceViewModel : ViewModel() {

    //被觀察者，值得變化由 searchPlaces來控制
    private val searchLiveData = MutableLiveData<String>()

    val placeList = ArrayList<Place>()

    // 觀察者，觀察searchLiveData對象
    val placeLiveData = Transformations.switchMap(searchLiveData) {
        Repository.searchPlaces(it)
    }

    fun searchPlaces(query : String){
        searchLiveData.value = query
    }
}