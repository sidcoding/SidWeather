package com.sid.weather.logic.network

import com.sid.weather.SidWeatherApplication
import com.sid.weather.logic.model.DailyResponse
import com.sid.weather.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherService {

    @GET("v2.5/${SidWeatherApplication.TOKEN}/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(@Path("lng") lng : String, @Path("lat") lat : String) : Call<RealtimeResponse>

    @GET("v2.5/${SidWeatherApplication.TOKEN}/{lng},{lat}/daily.json")
    fun getDailyWeather(@Path("lng") lng : String, @Path("lat") lat : String) : Call<DailyResponse>
}