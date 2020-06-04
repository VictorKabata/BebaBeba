package com.vickikbt.bebabeba

import android.app.Application
import androidx.annotation.Nullable
import com.facebook.stetho.Stetho
import com.vickikbt.bebabeba.di.repositoryModule
import com.vickikbt.bebabeba.di.viewModelModule
import com.vickikbt.bebabeba.utils.CrashlyticsTree
import org.jetbrains.annotations.NotNull
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

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
        startKoin {
            androidContext(this@Bebabeba)
            modules(
                listOf(
                    viewModelModule,
                    repositoryModule
                )
            )

        }
    }

    /*
    *  We  Stetho to debug our room db persistence storage
    * */
    private fun initStetho() {
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
    }

    /*
    * We are using timber for Logging
    *
    * */
    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {
                @Nullable
                override fun createStackElementTag(@NotNull element: StackTraceElement): String? {
                    return super.createStackElementTag(element) + ":" + element.lineNumber
                }
            })
        } else {
            Timber.plant(CrashlyticsTree())
        }
    }
}