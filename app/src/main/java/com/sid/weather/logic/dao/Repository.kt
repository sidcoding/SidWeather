package com.sid.weather.logic.dao


import androidx.lifecycle.liveData
import com.sid.weather.logic.model.Place
import com.sid.weather.logic.model.Weather
import com.sid.weather.logic.network.SidWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

object Repository {

    /**
     * 這個liveData函數是lifecycle-livedata-ktx庫提供的一個方法，
     * 可以自動構建並返回一個livedata對象，然後在代碼塊中提供挂起函數的上下文
     */
    fun searchPlaces(query : String) = fire(Dispatchers.IO) {
        val placeResponse = SidWeatherNetwork.searchPlaces(query)
        if(placeResponse.status == "ok"){
            val places = placeResponse.places
            Result.success(places)
        } else {
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }
    }

//            liveData(Dispatchers.IO) {
//        val result = try {
//            val placeResponse = SidWeatherNetwork.searchPlaces(query)
//            if(placeResponse.status == "ok"){
//                val places = placeResponse.places
//                Result.success(places)
//            } else {
//                Result.failure(RuntimeException("response status is ${placeResponse.status}"))
//            }
//        } catch (e : Exception){
//            Result.failure<Place>(e)
//        }
//        emit(result)
//    }

    fun refreshWeather(lng : String, lat : String) = fire(Dispatchers.IO) {
        coroutineScope {
            val deferredRealtime = async {
                SidWeatherNetwork.getRealtimeWeather(lng, lat)
            }
            val deferredDaily = async {
                SidWeatherNetwork.getDailyWeather(lng, lat)
            }
            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()
            if(realtimeResponse.status == "ok" && dailyResponse.status == "ok"){
                val weather = Weather(realtimeResponse.result.realtime, dailyResponse.result.daily)
                Result.success(weather)
            } else {
                Result.failure(RuntimeException("realtime response status is ${realtimeResponse.status} + " +
                        "daily response status is ${dailyResponse.status}"))
            }

        }
    }


//
//            liveData(Dispatchers.IO){
//        val result = try {
//            coroutineScope {
//                val deferredRealtime = async {
//                    SidWeatherNetwork.getRealtimeWeather(lng, lat)
//                }
//                val deferredDaily = async {
//                    SidWeatherNetwork.getDailyWeather(lng, lat)
//                }
//                val realtimeResponse = deferredRealtime.await()
//                val dailyResponse = deferredDaily.await()
//                if(realtimeResponse.status == "ok" && dailyResponse.status == "ok"){
//                    val weather = Weather(realtimeResponse.result.realtime, dailyResponse.result.daily)
//                    Result.success(weather)
//                } else {
//                    Result.failure(RuntimeException("realtime response status is ${realtimeResponse.status} + " +
//                            "daily response status is ${dailyResponse.status}"))
//                }
//            }
//        } catch (e : Exception){
//            Result.failure<Weather>(e)
//        }
//        emit(result)
//    }

    /**
     * fire是一个按照liveData()函数的参数接收标准定义的一个高阶函数，在fire()函数内部先调用liveData()函数，
     * 然后在liveData()函数的代码块中统一处理了try catch，并在try语句中调用传入的lambda表达式，最终将结果emit
     *
     * 需要在函数类型前声明一个suspend，表示所有传入的lambda表达式中的代码也是拥有挂起函数上下文的
     */
    private fun <T> fire(context : CoroutineContext, block : suspend () -> Result<T>)
                = liveData<Result<T>>(context) {
        val result = try {
            block()
        } catch (e : Exception) {
            Result.failure<T>(e)
        }
        emit(result)
    }
}