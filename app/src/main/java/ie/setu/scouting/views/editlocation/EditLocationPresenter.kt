package ie.setu.scouting.views.editlocation

import android.app.Activity
import android.content.Intent
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import ie.setu.scouting.models.Location
import timber.log.Timber

class EditLocationPresenter(private val view: EditLocationView) {

    var location = Location()

    init {
        location = view.location
    }

    fun initMap(map: GoogleMap) {
        val loc = LatLng(location.lat, location.lng)
        
        Timber.i("Map initialized at: lat=${location.lat}, lng=${location.lng}, zoom=${location.zoom}")
        
        val options = MarkerOptions()
            .title("Event Location")
            .snippet("GPS : $loc")
            .draggable(true)
            .position(loc)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)) // Make it RED
        
        val marker = map.addMarker(options)
        Timber.i("Marker added: $marker")
        
        // Zoom to the location
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, location.zoom))
        map.setOnMarkerDragListener(view)
        map.setOnMarkerClickListener(view)
        
        // Show marker info on tap
        marker?.showInfoWindow()
    }

    fun doUpdateLocation(lat: Double, lng: Double, zoom: Float) {
        location.lat = lat
        location.lng = lng
        location.zoom = zoom
        Timber.i("Location updated: lat=$lat, lng=$lng, zoom=$zoom")
    }

    fun doOnBackPressed() {
        val resultIntent = Intent()
        resultIntent.putExtra("location", location)
        view.setResult(Activity.RESULT_OK, resultIntent)
        Timber.i("Returning location: $location")
        view.finish()
    }

    fun doUpdateMarker(marker: Marker) {
        val loc = LatLng(location.lat, location.lng)
        marker.snippet = "GPS : $loc"
    }
}
