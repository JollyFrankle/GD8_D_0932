package com.example.gd8_d_0932

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import com.example.gd8_d_0932.databinding.ActivityMainBinding
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapController
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem
import java.nio.charset.StandardCharsets

class MainActivity : AppCompatActivity() {
    var modelMainList: MutableList<ModelMain> = ArrayList()
    lateinit var mapController: MapController
    lateinit var overlayItem: ArrayList<OverlayItem>
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        val geoPoint = GeoPoint( -7.779433, 110.415782)
        binding.mapView.setMultiTouchControls(true)
        binding.mapView.controller.animateTo(geoPoint)
        binding.mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.DEFAULT_TILE_SOURCE)
        binding.mapView.zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER)

        mapController = binding.mapView.controller as MapController
        mapController.setCenter(geoPoint)
        mapController.setZoom(18.0)

        getLocationMarker()
    }

    private fun getLocationMarker() {
        try {
            val stream = assets.open("sample_maps.json")
            val size = stream.available()
            val buffer = ByteArray(size)

            stream.read(buffer)
            stream.close()

            val strContent = String(buffer, StandardCharsets.UTF_8)
            try {
                val jsonObject = JSONObject(strContent)
                val jsonArrayResult = jsonObject.getJSONArray("results")
                for (i in 0 until jsonArrayResult.length()) {
                    val jsonObjectResult = jsonArrayResult.getJSONObject(i)
                    val modelMain = ModelMain()
                    modelMain.strName = jsonObjectResult.getString("name")
                    modelMain.strVicinity = jsonObjectResult.getString("vicinity")

                    // get lat lng
                    val jsonObjectGeometry = jsonObjectResult.getJSONObject("geometry")
                    val jsonObjectLocation = jsonObjectGeometry.getJSONObject("location")
                    modelMain.latLoc = jsonObjectLocation.getDouble("lat")
                    modelMain.lngLoc = jsonObjectLocation.getDouble("lng")

                    modelMainList.add(modelMain)
                }
                initMarker(modelMainList)
            } catch (e: Exception) {
                e.printStackTrace()
                // show using alert dialog
                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage(e.message)
                    .setPositiveButton("OK") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                this@MainActivity,
                "Error: " + e.message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun initMarker(modelList: List<ModelMain>) {
        for(i in modelList.indices) {
            overlayItem = ArrayList()
            overlayItem.add(
                OverlayItem(
                    modelList[i].strName,
                    modelList[i].strVicinity,
                    GeoPoint(modelList[i].latLoc, modelList[i].lngLoc)
                )
            )

            val info = ModelMain()
            info.strName = modelList[i].strName
            info.strVicinity = modelList[i].strVicinity

            val marker = Marker(binding.mapView)
            val markerIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_location_on_24, null)
            // set color of markerIcon
            markerIcon?.setTint(ResourcesCompat.getColor(resources, androidx.appcompat.R.color.material_blue_grey_900, null))

            marker.icon = markerIcon
            marker.position = GeoPoint(modelList[i].latLoc, modelList[i].lngLoc)
            marker.relatedObject = info
            marker.infoWindow = CustomInfoWindow(binding.mapView)
            marker.setOnMarkerClickListener { item, arg1 ->
                item.showInfoWindow()
                true
            }

            binding.mapView.overlayManager.add(marker)
            binding.mapView.invalidate()
        }
    }

    override fun onResume() {
        super.onResume()

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        if(binding.mapView != null) {
            binding.mapView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()

        Configuration.getInstance().save(this, PreferenceManager.getDefaultSharedPreferences(this))
        if(binding.mapView != null) {
            binding.mapView.onPause()
        }
    }
}