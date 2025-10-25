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
            val title = binding.eventTitle.text.toString()
            val description = binding.description.text.toString()
            val leadersNeededText = binding.leadersNeeded.text.toString().trim()
            val leadersNeeded = leadersNeededText.toIntOrNull() ?: 0
            val parentAllowed = binding.parentVolunteersAllowed.isChecked

            if (title.isNotEmpty()) {
                event.title = title
                event.description = description
                event.leadersNeeded = leadersNeeded
                event.parentVolunteersAllowed = parentAllowed

                app.events.add(event.copy())
                i("add Button Pressed: $event")

                setResult(RESULT_OK)
                finish()
            } else {
                Snackbar.make(view, "Please Enter a title", Snackbar.LENGTH_LONG).show()
            }
        }
    }
}