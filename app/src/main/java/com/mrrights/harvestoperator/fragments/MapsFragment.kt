package com.mrrights.harvestoperator.fragments

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.libraries.places.api.Places
import com.mrrights.harvestoperator.R
import com.mrrights.harvestoperator.utils.Constants.Companion.DENIED
import com.mrrights.harvestoperator.utils.Constants.Companion.LOCATION_CODE
import com.mrrights.harvestoperator.utils.Constants.Companion.LOCATION_PERMISSIONS
import com.mrrights.harvestoperator.utils.PermissionChecker


private const val TAG = "MapsFragment"

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    private var locationManager: LocationManager? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    var isMyLocationPermitted = false

    /* private val callback = OnMapReadyCallback { googleMap ->
         */
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * In this case, we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to
     * install it inside the SupportMapFragment. This method will only be triggered once the
     * user has installed Google Play services and returned to the app.
     *//*
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        Places.initialize(requireContext(), resources.getString(R.string.google_maps_key))

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkLocationPermission()

        checkGpsStatus()

        /* locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager?
         fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())*/


        /* fabLocation.setOnClickListener {

             checkGpsStatus()

             val mLocationRequest =
                 LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(10 * 1000.toLong()).setFastestInterval(1 * 1000.toLong())

             if (locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)!!) {
                 Toast.makeText(requireContext(), "Granted", Toast.LENGTH_SHORT).show()
             } else {
                 Toast.makeText(requireContext(), "Denied", Toast.LENGTH_SHORT).show()
             }

         }*/
    }


    //Android Q version has permission to access location in background
    @RequiresApi(Build.VERSION_CODES.Q)
    //checking for granted permissions
    private fun checkLocationPermission() {

        val permissionChecker = PermissionChecker(requireContext())

        if (permissionChecker.check(LOCATION_PERMISSIONS) == DENIED) {
            ActivityCompat.requestPermissions(
                requireActivity(), LOCATION_PERMISSIONS, LOCATION_CODE
            )
        }
    }


    //permission request
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Toast.makeText(requireContext(), "SSS", Toast.LENGTH_SHORT).show()
        when (requestCode) {
            LOCATION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(requireContext(), "SSSSSSS", Toast.LENGTH_SHORT).show()
                    if (ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Log.i(TAG, "granted")
                        Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    override fun onMapReady(map: GoogleMap?) {
        map?.let {
            googleMap = it
        }
    }

    private fun checkGpsStatus() {

        var gpsStatus: Boolean = false

        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        gpsStatus = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (gpsStatus) {
            Toast.makeText(requireContext(), "Ena", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Dis", Toast.LENGTH_SHORT).show()
        }
    }

    private fun enableLocation() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 30 * 1000.toLong()
        locationRequest.fastestInterval = 5 * 1000.toLong()
    }

}
