package com.sid.weather.logic.dao


import androidx.lifecycle.liveData
import com.sid.weather.logic.model.Place
import com.sid.weather.logic.network.SidWeatherNetwork
import kotlinx.coroutines.Dispatchers
import java.lang.Exception
import java.lang.RuntimeException

object Repository {

    /**
     * 這個liveData函數是lifecycle-livedata-ktx庫提供的一個方法，
     * 可以自動構建並返回一個livedata對象，然後在代碼塊中提供挂起函數的上下文
     */
    fun searchPlaces(query : String) = liveData(Dispatchers.IO) {
        val result = try {
            val placeResponse = SidWeatherNetwork.searchPlaces(query)
            if(placeResponse.status == "ok"){
                val places = placeResponse.places
                Result.success(places)
            } else {
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))
            }
        } catch (e : Exception){
            Result.failure<Place>(e)
        }
        emit(result)
    }
}