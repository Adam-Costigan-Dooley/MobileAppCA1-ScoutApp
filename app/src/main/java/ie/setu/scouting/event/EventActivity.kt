package ie.setu.scouting.event

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import ie.setu.scouting.R
import ie.setu.scouting.databinding.ActivityEventBinding
import ie.setu.scouting.models.EventModel
import timber.log.Timber.i
import ie.setu.scouting.main.MainApp

class EventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventBinding
    private var event = EventModel()
    private lateinit var app: MainApp
    private var editMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp
        i("Event Activity started...")

        // Edit mode?
        if (intent.hasExtra("event_edit")) {
            editMode = true
            event = intent.extras?.getParcelable("event_edit")!!
            binding.eventTitle.setText(event.title)
            binding.description.setText(event.description)
            binding.leadersNeeded.setText(event.leadersNeeded.toString())
            binding.parentVolunteersAllowed.isChecked = event.parentVolunteersAllowed
            binding.btnAdd.text = getString(R.string.button_save_event)
        } else {
            binding.btnAdd.text = getString(R.string.button_add_event)
        }

        binding.btnAdd.setOnClickListener { view ->
            val title = binding.eventTitle.text.toString()
            val desc  = binding.description.text.toString()
            val leaders = binding.leadersNeeded.text.toString().trim().toInt()
            val parents = binding.parentVolunteersAllowed.isChecked

            if (title.isEmpty()) {
                Snackbar.make(view, getString(R.string.snackbar_enter_title), Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            event.title = title
            event.description = desc
            event.leadersNeeded = leaders
            event.parentVolunteersAllowed = parents

            if (editMode) {
                app.events.update(event)
            } else {
                app.events.create(event.copy())
            }

            setResult(RESULT_OK)
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_event, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}