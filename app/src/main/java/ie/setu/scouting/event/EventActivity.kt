package ie.setu.scouting.event

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import ie.setu.scouting.R
import ie.setu.scouting.databinding.ActivityEventBinding
import ie.setu.scouting.main.MainApp
import ie.setu.scouting.models.EventModel
import timber.log.Timber.i
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventBinding
    private lateinit var app: MainApp

    // Your event object + a flag to track edit mode
    private var event = EventModel()
    private var editMode = false

    // ✅ Date formatting used by the picker + display
    private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        // App reference
        app = application as MainApp
        i("Event Activity started...")

        // Delete hidden by default (only visible in edit mode)
        binding.btnDelete.visibility = View.GONE

        // ✅ If launched for edit, prefill fields and enable Delete
        if (intent.hasExtra("event_edit")) {
            editMode = true
            event = intent.extras?.getParcelable("event_edit")!!

            binding.eventTitle.setText(event.title)
            binding.description.setText(event.description)
            binding.leadersNeeded.setText(event.leadersNeeded.toString())
            binding.parentVolunteersAllowed.isChecked = event.parentVolunteersAllowed

            if (event.date.isNotEmpty()) {
                binding.eventDate.setText(event.date)
            }

            binding.btnAdd.text = getString(R.string.button_save_event)

            // Show delete in edit mode (immediate delete, no confirm)
            binding.btnDelete.visibility = View.VISIBLE
            binding.btnDelete.setOnClickListener {
                app.events.delete(event)
                setResult(Activity.RESULT_OK)
                finish()
            }
        } else {
            binding.btnAdd.text = getString(R.string.button_add_event)
        }

        // ✅ DATE PICKER: open when clicking field or the "Pick Date" button
        val openDatePicker = {
            // Use current value if present, otherwise today
            val initialDate = if (!binding.eventDate.text.isNullOrEmpty()) {
                runCatching { LocalDate.parse(binding.eventDate.text.toString(), dateFormatter) }
                    .getOrElse { LocalDate.now() }
            } else {
                LocalDate.now()
            }

            val year = initialDate.year
            val monthZeroBased = initialDate.monthValue - 1 // DatePickerDialog needs 0..11
            val day = initialDate.dayOfMonth

            DatePickerDialog(this, { _, y, m, d ->
                val picked = LocalDate.of(y, m + 1, d)
                binding.eventDate.setText(picked.format(dateFormatter))
            }, year, monthZeroBased, day).show()
        }

        binding.eventDate.setOnClickListener { openDatePicker() }
        binding.btnPickDate.setOnClickListener { openDatePicker() }

        // ✅ SAVE (Add or Update) — now includes date
        binding.btnAdd.setOnClickListener { v ->
            event.title = binding.eventTitle.text.toString()
            event.description = binding.description.text.toString()
            event.leadersNeeded = binding.leadersNeeded.text.toString().toInt() // or toIntOrNull() ?: 0
            event.parentVolunteersAllowed = binding.parentVolunteersAllowed.isChecked
            event.date = binding.eventDate.text.toString()

            if (event.title.isNotEmpty()) {
                if (editMode) {
                    app.events.update(event)
                } else {
                    app.events.create(event.copy())
                }
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                // Use your existing string resource if you’ve added it
                Snackbar.make(
                    v,
                    getString(R.string.snackbar_enter_title), // or "Please enter a title"
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    // If you still have the Cancel menu (like the lab):
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_event, menu) // your cancel menu resource
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_cancel) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
