package ie.setu.scouting.views.eventlist

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import ie.setu.scouting.main.MainApp
import ie.setu.scouting.models.EventModel
import ie.setu.scouting.views.event.EventView
import timber.log.Timber

class EventListPresenter(private val view: EventListView) {

    var app: MainApp = view.application as MainApp
    private lateinit var refreshIntentLauncher: ActivityResultLauncher<Intent>

    init {
        registerRefreshCallback()
    }

    fun getEvents(): List<EventModel> {
        return app.events.findAll()
    }

    fun doAddEvent() {
        val launcherIntent = Intent(view, EventView::class.java)
        refreshIntentLauncher.launch(launcherIntent)
    }

    fun doEditEvent(event: EventModel) {
        val intent = Intent(view, EventView::class.java)
        intent.putExtra("event_edit", event)
        refreshIntentLauncher.launch(intent)
    }

    private fun registerRefreshCallback() {
        refreshIntentLauncher =
            view.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == android.app.Activity.RESULT_OK) {
                    Timber.i("Event list refreshing after result")
                    view.refreshEventList()
                }
            }
    }
}
