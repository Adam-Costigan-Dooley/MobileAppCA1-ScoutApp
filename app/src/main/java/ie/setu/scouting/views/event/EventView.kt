package ie.setu.scouting.views.event

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import ie.setu.scouting.R
import ie.setu.scouting.databinding.ActivityEventBinding
import ie.setu.scouting.models.EventModel
import timber.log.Timber.i
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EventView : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityEventBinding
    private lateinit var presenter: EventPresenter
    var event = EventModel()
    private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    private var mapPreview: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        // Initialize presenter FIRST
        presenter = EventPresenter(this)

        i("Event Activity started...")

        // Initialize map preview AFTER presenter
        binding.mapPreview.onCreate(savedInstanceState)
        binding.mapPreview.getMapAsync(this)

        // Hide delete button initially (presenter will show it if in edit mode)
        binding.btnDelete.visibility = if (presenter.edit) View.VISIBLE else View.GONE

        // Image picker button
        binding.chooseImage.setOnClickListener {
            presenter.cacheEvent(
                binding.eventTitle.text.toString(),
                binding.description.text.toString(),
                binding.leadersNeeded.text.toString().toIntOrNull() ?: 0,
                binding.parentVolunteersAllowed.isChecked,
                binding.eventDate.text.toString()
            )
            presenter.doSelectImage()
        }

        // Location picker button
        binding.eventLocation.setOnClickListener {
            presenter.cacheEvent(
                binding.eventTitle.text.toString(),
                binding.description.text.toString(),
                binding.leadersNeeded.text.toString().toIntOrNull() ?: 0,
                binding.parentVolunteersAllowed.isChecked,
                binding.eventDate.text.toString()
            )
            presenter.doSetLocation()
        }

        // Date picker
        val openDatePicker = {
            val initialDate = if (!binding.eventDate.text.isNullOrEmpty()) {
                runCatching { LocalDate.parse(binding.eventDate.text.toString(), dateFormatter) }
                    .getOrElse { LocalDate.now() }
            } else {
                LocalDate.now()
            }

            val year = initialDate.year
            val monthZeroBased = initialDate.monthValue - 1
            val day = initialDate.dayOfMonth

            DatePickerDialog(this, { _, y, m, d ->
                val picked = LocalDate.of(y, m + 1, d)
                binding.eventDate.setText(picked.format(dateFormatter))
            }, year, monthZeroBased, day).show()
        }

        binding.eventDate.setOnClickListener { openDatePicker() }

        // Add/Save button
        binding.btnAdd.setOnClickListener { v ->
            val title = binding.eventTitle.text.toString()
            val description = binding.description.text.toString()
            val leadersNeeded = binding.leadersNeeded.text.toString().toIntOrNull() ?: 0
            val parentVolunteersAllowed = binding.parentVolunteersAllowed.isChecked
            val date = binding.eventDate.text.toString()

            if (title.isEmpty()) {
                Snackbar.make(v, getString(R.string.snackbar_enter_title), Snackbar.LENGTH_LONG).show()
            } else {
                presenter.doAddOrSave(title, description, leadersNeeded, parentVolunteersAllowed, date)
            }
        }

        // Delete button
        binding.btnDelete.setOnClickListener {
            presenter.doDelete()
        }

        // Update button text based on mode
        binding.btnAdd.text = if (presenter.edit) {
            getString(R.string.button_save_event)
        } else {
            getString(R.string.button_add_event)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_event, menu)
        val deleteMenu: MenuItem = menu.findItem(R.id.item_delete)
        deleteMenu.isVisible = presenter.edit
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> {
                presenter.doCancel()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapPreview = googleMap

        // Disable all map interactions
        googleMap.uiSettings.isScrollGesturesEnabled = false
        googleMap.uiSettings.isZoomGesturesEnabled = false
        googleMap.uiSettings.isTiltGesturesEnabled = false
        googleMap.uiSettings.isRotateGesturesEnabled = false
        googleMap.uiSettings.setAllGesturesEnabled(false)

        // Now that map is ready, update it with current location
        updateMapPreview()
    }

    fun showEvent(event: EventModel) {
        binding.eventTitle.setText(event.title)
        binding.description.setText(event.description)
        binding.leadersNeeded.setText(event.leadersNeeded.toString())
        binding.parentVolunteersAllowed.isChecked = event.parentVolunteersAllowed
        binding.eventDate.setText(event.date)

        // Show image if present
        if (event.image != Uri.EMPTY) {
            Picasso.get()
                .load(event.image)
                .into(binding.eventImage)
            binding.chooseImage.setText(R.string.change_event_image)
        }

        // Update map preview (will only work if map is ready)
        updateMapPreview()
    }

    fun updateImage(image: Uri) {
        i("Image updated")
        Picasso.get()
            .load(image)
            .into(binding.eventImage)
        binding.chooseImage.setText(R.string.change_event_image)
    }

    fun updateMapPreview() {
        // Check if presenter is initialized (avoid crash during initialization)
        if (!::presenter.isInitialized) {
            i("Map preview skipped: presenter not initialized yet")
            return
        }

        val event = presenter.event

        if (event.lat != 0.0 && event.lng != 0.0) {
            // Location is set - show map preview
            binding.mapPreviewContainer.visibility = View.VISIBLE
            binding.eventLocation.text = getString(R.string.button_change_location)

            // Update map if ready
            mapPreview?.let { map ->
                map.clear()
                val location = LatLng(event.lat, event.lng)
                map.addMarker(
                    MarkerOptions()
                        .position(location)
                        .title("Event Location")
                )
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
            }

            i("Map preview updated: ${event.lat}, ${event.lng}")
        } else {
            // No location set - hide map preview
            binding.mapPreviewContainer.visibility = View.GONE
            binding.eventLocation.text = getString(R.string.button_set_location)

            i("Map preview hidden: no location set")
        }
    }

    // MapView lifecycle methods
    override fun onResume() {
        super.onResume()
        binding.mapPreview.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapPreview.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapPreview.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapPreview.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapPreview.onSaveInstanceState(outState)
    }
}