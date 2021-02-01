package com.sid.weather

import android.app.Application
import android.content.Context

class SidWeatherApplication : Application() {

    companion object{
        const val TOKEN = "FhFClQeXVFY1CteT"
        lateinit var context : Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}