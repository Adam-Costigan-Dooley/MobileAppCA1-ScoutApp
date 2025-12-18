package ie.setu.scouting.views.event

import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import ie.setu.scouting.main.MainApp
import ie.setu.scouting.models.EventModel
import ie.setu.scouting.models.Location
import ie.setu.scouting.views.editlocation.EditLocationView
import timber.log.Timber

class EventPresenter(private val view: EventView) {

    var event = EventModel()
    var app: MainApp = view.application as MainApp
    private lateinit var imageIntentLauncher: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var mapIntentLauncher: ActivityResultLauncher<Intent>
    var edit = false

    init {
        if (view.intent.hasExtra("event_edit")) {
            edit = true
            @Suppress("DEPRECATION")
            event = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                view.intent.getParcelableExtra("event_edit", EventModel::class.java)!!
            } else {
                view.intent.getParcelableExtra<EventModel>("event_edit")!!
            }
            view.showEvent(event)
        }
        registerImagePickerCallback()
        registerMapCallback()
    }

    fun doAddOrSave(title: String, description: String, leadersNeeded: Int, 
                    parentVolunteersAllowed: Boolean, date: String) {
        event.title = title
        event.description = description
        event.leadersNeeded = leadersNeeded
        event.parentVolunteersAllowed = parentVolunteersAllowed
        event.date = date
        
        if (edit) {
            app.events.update(event)
        } else {
            app.events.create(event.copy())
        }
        view.setResult(RESULT_OK)
        view.finish()
    }

    fun doCancel() {
        view.finish()
    }

    fun doDelete() {
        app.events.delete(event)
        view.setResult(RESULT_OK)
        view.finish()
    }

    fun doSelectImage() {
        val request = PickVisualMediaRequest.Builder()
            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
            .build()
        imageIntentLauncher.launch(request)
    }

    fun doSetLocation() {
        val location = Location(52.2593, -7.1101, 15f)
        if (event.zoom != 0f) {
            location.lat = event.lat
            location.lng = event.lng
            location.zoom = event.zoom
        }
        val launcherIntent = Intent(view, EditLocationView::class.java)
            .putExtra("location", location)
        mapIntentLauncher.launch(launcherIntent)
    }

    fun cacheEvent(title: String, description: String, leadersNeeded: Int, 
                   parentVolunteersAllowed: Boolean, date: String) {
        event.title = title
        event.description = description
        event.leadersNeeded = leadersNeeded
        event.parentVolunteersAllowed = parentVolunteersAllowed
        event.date = date
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher = view.registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) {
            try {
                view.contentResolver.takePersistableUriPermission(
                    it!!,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                event.image = it
                Timber.i("IMG :: ${event.image}")
                view.updateImage(event.image)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun registerMapCallback() {
        mapIntentLauncher =
            view.registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when (result.resultCode) {
                    AppCompatActivity.RESULT_OK -> {
                        if (result.data != null) {
                            Timber.i("Got Location ${result.data.toString()}")
                            @Suppress("DEPRECATION")
                            val location = result.data!!.extras?.getParcelable<Location>("location")!!
                            Timber.i("Location == $location")
                            event.lat = location.lat
                            event.lng = location.lng
                            event.zoom = location.zoom
                            view.updateMapPreview()
                        }
                    }
                    AppCompatActivity.RESULT_CANCELED -> { }
                    else -> { }
                }
            }
    }
}
