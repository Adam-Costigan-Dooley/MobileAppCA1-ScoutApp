package ie.setu.scouting.main

import android.app.Application
import ie.setu.scouting.models.EventMemStore
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    val events = EventMemStore()

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Timber.i("Scouting app started")
    }
}