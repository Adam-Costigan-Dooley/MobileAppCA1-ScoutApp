package ie.setu.scouting.event

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import ie.setu.scouting.databinding.ActivityEventBinding
import ie.setu.scouting.models.EventModel
import timber.log.Timber.i
import ie.setu.scouting.main.MainApp
class EventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventBinding
    private var event = EventModel()
    private lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp
        i("Event Activity started...")

        binding.btnAdd.setOnClickListener { view ->
            event.title = binding.eventTitle.text.toString()
            event.description = binding.description.text.toString()

            if (event.title.isNotEmpty()) {
                app.events.add(event.copy())
                i("add Button Pressed: $event")

                // (optional) log contents
                for (idx in app.events.indices) {
                    i("Event[$idx]: ${app.events[idx]}")
                }

                setResult(RESULT_OK)
                finish()
            } else {
                Snackbar.make(view, "Please Enter a title", Snackbar.LENGTH_LONG).show()
            }
        }
    }
}