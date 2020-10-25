package com.example.kmpstarter

import android.app.Application
import com.example.common.actions.MoviesActions
import com.example.common.createStore
import com.example.common.preferences.AppUserDefaults
import com.example.common.preferences.settings
import com.example.common.services.APIService
import kotlinx.coroutines.Dispatchers
import ru.pocketbyte.kydra.log.KydraLog
import ru.pocketbyte.kydra.log.LogLevel
import ru.pocketbyte.kydra.log.initDefault

val store = createStore()
lateinit var movieActions: MoviesActions

class App: Application() {
    private val apiService = APIService(Dispatchers.IO, Dispatchers.Main)
    private lateinit var appUserDefaults: AppUserDefaults

    override fun onCreate() {
        super.onCreate()
        if (!BuildConfig.DEBUG) {
            // No need to write debug logs in production build
            KydraLog.initDefault(LogLevel.INFO)
        } else {
            KydraLog.initDefault(LogLevel.DEBUG)
        }
        appUserDefaults = AppUserDefaults(settings(this))
        movieActions = MoviesActions(apiService, appUserDefaults)
    }
}