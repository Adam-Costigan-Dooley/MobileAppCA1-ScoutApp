package ie.setu.scouting.main

import android.app.Application
import ie.setu.scouting.models.EventModel
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    val events = ArrayList<EventModel>()

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Timber.i("Scouting app started")
        // Optionally seed test items (remove after verifying RecyclerView):
        // events.add(EventModel("One", "About one..."))
        // events.add(EventModel("Two", "About two..."))
        // events.add(EventModel("Three", "About three..."))
    }
}