package ie.setu.scouting.main

import android.app.Application
import ie.setu.scouting.models.EventMemStore
import ie.setu.scouting.models.EventJSONStore
import ie.setu.scouting.models.EventStore
import timber.log.Timber

class MainApp : Application() {

    lateinit var events: EventStore


    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Timber.i("Scouting app started")
        events = EventJSONStore(this)
    }
}