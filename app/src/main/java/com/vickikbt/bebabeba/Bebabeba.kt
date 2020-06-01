package com.vickikbt.bebabeba

import android.app.Application

class Bebabeba : Application() {


    override fun onCreate() {
        super.onCreate()

        initKoin()
        initStetho()
        initTimber()
    }

    /*
    *  We  will initialize Koin modules here to enable dependency injection
    *  We are starting Modules in the di/Modules.kt
    * */
    private fun initKoin() {
        TODO("Not yet implemented")
    }

    /*
    *  We  Stetho to debug our room db persistence storage
    * */
    private fun initStetho() {
        TODO("Not yet implemented")
    }

    /*
    * We are using timber for Logging
    *
    * */
    private fun initTimber() {
        TODO("Not yet implemented")
    }

}