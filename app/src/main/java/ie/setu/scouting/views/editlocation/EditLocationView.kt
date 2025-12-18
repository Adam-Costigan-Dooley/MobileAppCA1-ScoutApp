package ie.setu.scouting.views.editlocation

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import ie.setu.scouting.R
import ie.setu.scouting.databinding.ActivityMapBinding
import ie.setu.scouting.models.Location

class EditLocationView : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerDragListener,
    GoogleMap.OnMarkerClickListener {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapBinding
    lateinit var presenter: EditLocationPresenter
    var location = Location()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        presenter = EditLocationPresenter(this)
        
        @Suppress("DEPRECATION")
        location = intent.extras?.getParcelable<Location>("location")!!
        
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Handle system back button/gesture
        onBackPressedDispatcher.addCallback(this) {
            presenter.doOnBackPressed()
        }
        
        // Handle Save Location button click
        binding.btnSaveLocation.setOnClickListener {
            presenter.doOnBackPressed()
        }
        // NOTE: Addition beyond Placemark - explicit Save Location button
        // Testing on Pixel 8 Pro emulator had issues with saving the location when backing out
        // Added explicit button for clear fuctionality regardless of device
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        presenter.initMap(map)
    }

    override fun onMarkerDragStart(marker: Marker) {}

    override fun onMarkerDrag(marker: Marker) {}

    override fun onMarkerDragEnd(marker: Marker) {
        presenter.doUpdateLocation(
            marker.position.latitude,
            marker.position.longitude,
            map.cameraPosition.zoom
        )
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        presenter.doUpdateMarker(marker)
        return false
    }
}
