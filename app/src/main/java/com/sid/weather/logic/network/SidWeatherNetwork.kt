package com.sid.weather.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object SidWeatherNetwork {
    /**
     * 使用普通模式创建placeService
     */
//    private val placeService = ServiceCreator.create(PlaceService::class.java)
    /**
     * 使用泛型实例化功能创建placeService
     */
    private val placeService = ServiceCreator.create<PlaceService>()

    suspend fun searchPlaces(query : String) = placeService.searchPlaces(query).await()

    private suspend fun <T> Call<T>.await() : T {
        return suspendCoroutine {
            enqueue(object : Callback<T>{
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if(body!=null)
                        it.resume(body)
                    else
                        it.resumeWithException(RuntimeException("response body is null"))
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    it.resumeWithException(t)
                }

            })
        }
    }
}