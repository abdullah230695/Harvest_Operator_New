package com.mrrights.harvestoperator.fragments

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.mrrights.harvestoperator.R
import com.mrrights.harvestoperator.utils.Constants
import com.mrrights.harvestoperator.utils.Constants.Companion.DEFAULT_LOCATION
import com.mrrights.harvestoperator.utils.Constants.Companion.DEFAULT_ZOOM
import com.mrrights.harvestoperator.utils.Constants.Companion.DENIED
import com.mrrights.harvestoperator.utils.Constants.Companion.GRANTED
import com.mrrights.harvestoperator.utils.Constants.Companion.LOCATION_SETTING_CODE
import com.mrrights.harvestoperator.utils.PermissionChecker
import kotlinx.android.synthetic.main.fragment_home.*


private const val TAG = "FragmentHome"
private const val FCMTAG = "FragmentHome FCM"

public class FragmentHome : Fragment(), OnMapReadyCallback
{


	private var lastKnownLocation : Location? = null
	private var locationPermissionGranted = DENIED
	private var gpsTurnedOn = DENIED
	
	//google Map
	private lateinit var googleMap : GoogleMap
	
	//location Manager for GPS
	private var locationManager : LocationManager? = null
	
	private lateinit var fusedLocationProviderClient : FusedLocationProviderClient

	//Broadcast Reciever
	  val broadCastReceiver = object : BroadcastReceiver() {
		override fun onReceive(contxt: Context?, intent: Intent?) {
			CardViewBookingReqWindow.visibility= View.VISIBLE
			val user: String? = intent?.getStringExtra("User")
			tvUser.text = user
			val loc: String? = intent?.getStringExtra("Address")
			tvLoc.text = loc
			val type: String? = intent?.getStringExtra("Type")
			tvType.text = type
			val date: String? = intent?.getStringExtra("Date")
			tvDate.text = date
			val time: String? = intent?.getStringExtra("Time")
			tvTime.text = time
			val area: String? = intent?.getStringExtra("Area")
			tvArea.text = area

		}
	} //Broadcast Reciever

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View?
	{

		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_home, container, false)
		
	}

	override fun onStart() {
		super.onStart()
		//Broadcast Receiver
		LocalBroadcastManager.getInstance(requireContext())
				.registerReceiver(broadCastReceiver, IntentFilter("receiver"))
		//Broadcast Receiver
	}
	override fun onDestroy() {
		super.onDestroy()
		LocalBroadcastManager.getInstance(requireContext())
				.unregisterReceiver(broadCastReceiver)
	}
	@RequiresApi(Build.VERSION_CODES.Q)
	override fun onViewCreated(view: View, savedInstanceState: Bundle?)
	{
		super.onViewCreated(view, savedInstanceState)

		val bundle: Bundle? = activity?.intent?.extras
		if (bundle!=null) {
			CardViewBookingReqWindow.visibility = View.VISIBLE

			val User = bundle?.get("customerName")
			val Address = bundle?.get("Address")
			val Type = bundle?.get("serviceType")
			val Date = bundle?.get("serviceDate")
			val Time = bundle?.get("DeliveryTime")
			val Area = bundle?.get("RequiredArea")
			tvUser.text = User.toString()
			tvLoc.text = Address.toString()
			tvType.text = "Service : "+Type.toString()
			tvDate.text =  "Date : "+Date.toString()
			tvTime.text =  "Time : "+Time.toString()
			tvArea.text =  "Area : "+Area.toString()
		}


		//Broadcast Receiver
		LocalBroadcastManager.getInstance(requireContext())
				.registerReceiver(broadCastReceiver, IntentFilter("receiver"))
		//Broadcast Reciever

		Firebase.messaging.subscribeToTopic("Booking").addOnCompleteListener { task ->

			if (task.isSuccessful) {
				Log.d(FCMTAG, "Subscribe to topic 'Booking'")

			} else {

				Log.d(FCMTAG, "Not able to subscribe the topic")
			}

		}

		// Initialize Places.
		Places.initialize(requireContext(), resources.getString(R.string.google_maps_key))
		
		// Create a new Places client instance.
		
		// Create a new Places client instance.
		val placesClient : PlacesClient = Places.createClient(requireContext())
		
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		val mapFragment = childFragmentManager.findFragmentById(R.id.map2) as SupportMapFragment
		mapFragment.getMapAsync(this)
		
		checkLocationPermission()

		//Button Accept
		buttonAccept.setOnClickListener(View.OnClickListener {
			Toast.makeText(requireContext(), "Booking Accepted", Toast.LENGTH_SHORT).show()
			CardViewBookingReqWindow.visibility = View.INVISIBLE
			cardViewServiceStatus.visibility = View.INVISIBLE
		})
		//Button Reject
		buttonReject.setOnClickListener(View.OnClickListener {
			Toast.makeText(requireContext(), "Booking Rejected", Toast.LENGTH_SHORT).show()
			CardViewBookingReqWindow.visibility = View.INVISIBLE
			cardViewServiceStatus.visibility = View.VISIBLE
		})
		//Check for LocationPermissions & GPS is turned On
		fabLocation.setOnClickListener {
			
			Log.i(TAG, "fabLocation Clicked")
			if (locationPermissionGranted == DENIED || gpsTurnedOn == DENIED)
			{
				checkLocationPermission()
			}
			else
			{
				Log.d(TAG, "onClick: Clicked after Gps Is On")
				locateDevice()
			}
			
		}
		
		
	}
	
	//checking for granted permissions
	@RequiresApi(Build.VERSION_CODES.Q) //Android Q version has permission to access location in background
	private fun checkLocationPermission()
	{
		
		Log.i(TAG, "checking for Location permissions")
		val permissionChecker = PermissionChecker(requireContext())
		
		if (permissionChecker.check(Constants.LOCATION_PERMISSIONS) == DENIED)
		{
			Log.w(TAG, "Location permissions are Denied")
			locationPermissionGranted = DENIED
			ActivityCompat.requestPermissions(requireActivity(), Constants.LOCATION_PERMISSIONS, Constants.LOCATION_CODE)
		}
		else
		{
			locationPermissionGranted = GRANTED
			Log.i(TAG, "Location permissions are Granted")
			//checking GPS is enabled
			checkGpsStatus()
		}
	}
	
	//permission request
	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		
		when (requestCode)
		{
			Constants.LOCATION_CODE -> {
				if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
						locationPermissionGranted = GRANTED
						Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
					}
				} else {
					Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
					locationPermissionGranted = GRANTED
				}
				return
			}
		}
	}
	
	//onMapReady when map gets async
	@RequiresApi(Build.VERSION_CODES.Q)
	override fun onMapReady(map: GoogleMap?)
	{
		map?.let {
			googleMap = it
		}
		
		googleMap.setOnMapClickListener {
			googleMap.clear()
			googleMap.addMarker(MarkerOptions().position(it))
		}
		
		updateLocationUI()
		locateDevice()
	}
	
	//check the GPS is turned On or Not
	@RequiresApi(Build.VERSION_CODES.Q)
	private fun checkGpsStatus()
	{
		Log.i(TAG, "checking for GPS is Turned On")
		locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
		
		val gpsStatus = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
		if (gpsStatus)
		{
			gpsTurnedOn = GRANTED
			Log.i(TAG, "GPS is Turned On")
		}
		else
		{
			gpsTurnedOn = GRANTED
			Log.i(TAG, "GPS is Turned Off")
			enableGPS()
		}
	}
	
	//enabling GPS
	@RequiresApi(Build.VERSION_CODES.Q)
	private fun enableGPS()
	{
		
		Log.i(TAG, "To enable GPS")
		val locationRequest = LocationRequest.create()
		locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
		locationRequest.interval = 30*1000.toLong()
		locationRequest.fastestInterval = 5*1000.toLong()
		
		
		//setting builder for LocationSetting
		val settingsBuilder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
		settingsBuilder.setAlwaysShow(true)
		
		//task for LocationSettingsResponse
		val result : Task<LocationSettingsResponse> =
				LocationServices.getSettingsClient(requireContext()).checkLocationSettings(settingsBuilder.build())
		
		result.addOnCompleteListener {
			try
			{
				//TODO:response object?
				val response = it.getResult(ApiException::class.java)
			}
			catch (e: ApiException)
			{
				when (e.statusCode)
				{
					//popup for enabling GPS
					LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
						val resolvableApiException: ResolvableApiException = e as ResolvableApiException
						resolvableApiException.startResolutionForResult(requireActivity(), LOCATION_SETTING_CODE)
						Log.i(TAG, "GPS enabled!")
						gpsTurnedOn = GRANTED
					} catch (e: IntentSender.SendIntentException) {
						gpsTurnedOn = DENIED
						Toast.makeText(requireContext(), e.message.toString(), Toast.LENGTH_SHORT).show()
						Log.e(TAG, e.toString())
					}

					LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
					}
				}
			}
		}
		
		
	}
	
	
	private fun locateDevice()
	{
		/*
		 * Get the best and most recent location of the device, which may be null in rare
		 * cases when a location is not available.
		 */
		try
		{
			if (locationPermissionGranted == GRANTED)
			{
				val locationResult = fusedLocationProviderClient.lastLocation
				
				locationResult.addOnCompleteListener {
					if (it.isSuccessful)
					{
						lastKnownLocation = it.result
						if (lastKnownLocation != null)
						{
							googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude), DEFAULT_ZOOM))
						}
					}
					else
					{
						Log.d(TAG, "Current location is null. Using defaults.")
						Log.e(TAG, "Exception: %s", it.exception)
						googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(DEFAULT_LOCATION[0], DEFAULT_LOCATION[0]), DEFAULT_ZOOM))
						googleMap.uiSettings.isMyLocationButtonEnabled = false
					}
				}
			}
		}
		catch (e: SecurityException)
		{
			Log.e("Exception: %s", e.message, e)
		}
	}
	
	
	@RequiresApi(Build.VERSION_CODES.Q)
	private fun updateLocationUI()
	{
		
		fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
		try
		{
			if (locationPermissionGranted == GRANTED)
			{
				googleMap.isMyLocationEnabled = true
				
				googleMap.uiSettings?.isMyLocationButtonEnabled = false
				googleMap.uiSettings?.isCompassEnabled = true
				googleMap.uiSettings?.isScrollGesturesEnabledDuringRotateOrZoom = true
				googleMap.uiSettings?.isRotateGesturesEnabled = true
				googleMap.uiSettings?.isScrollGesturesEnabled = true
				googleMap.uiSettings?.isTiltGesturesEnabled = true
				googleMap.uiSettings?.isZoomGesturesEnabled = true
				googleMap.uiSettings?.isMapToolbarEnabled = true
			}
			else
			{
				googleMap.isMyLocationEnabled = false
				googleMap.uiSettings?.isMyLocationButtonEnabled = false
				lastKnownLocation = null
				checkLocationPermission()
			}
		}
		catch (e: SecurityException)
		{
			Log.e("Exception: %s", e.message, e)
		}
	}
	
	
	private fun moveCamera(latLng: LatLng, zoom: Float, title: String)
	{
		Log.d(TAG, "location: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude)
		googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
		/* val options = MarkerOptions().position(latLng).title(tittle)
		 mMap.addMarker(options)*/
	}


	
	/* @SuppressLint("MissingPermission")
	 private fun showCurrentPlace() {

		 if (locationPermissionGranted== GRANTED) {
			 // Use fields to define the data types to return.
			 val placeFields = listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)

			 // Use the builder to create a FindCurrentPlaceRequest.
			 val request = FindCurrentPlaceRequest.newInstance(placeFields)

			 // Get the likely places - that is, the businesses and other points of interest that
			 // are the best match for the device's current location.
			 val placeResult = placesClient.findCurrentPlace(request)
			 placeResult.addOnCompleteListener { task ->
				 if (task.isSuccessful && task.result != null) {
					 val likelyPlaces = task.result

					 // Set the count, handling cases where less than 5 entries are returned.
					 val count = if (likelyPlaces != null && likelyPlaces.placeLikelihoods.size < M_MAX_ENTRIES) {
						 likelyPlaces.placeLikelihoods.size
					 } else {
						 M_MAX_ENTRIES
					 }
					 var i = 0
					 likelyPlaceNames = arrayOfNulls(count)
					 likelyPlaceAddresses = arrayOfNulls(count)
					 likelyPlaceAttributions = arrayOfNulls<List<*>?>(count)
					 likelyPlaceLatLngs = arrayOfNulls(count)
					 for (placeLikelihood in likelyPlaces?.placeLikelihoods ?: emptyList()) {
						 // Build a list of likely places to show the user.
						 likelyPlaceNames[i] = placeLikelihood.place.name
						 likelyPlaceAddresses[i] = placeLikelihood.place.address
						 likelyPlaceAttributions[i] = placeLikelihood.place.attributions
						 likelyPlaceLatLngs[i] = placeLikelihood.place.latLng
						 i++
						 if (i > count - 1) {
							 break
						 }
					 }

					 // Show a dialog offering the user the list of likely places, and add a
					 // marker at the selected place.
					 openPlacesDialog()
				 } else {
					 Log.e(TAG, "Exception: %s", task.exception)
				 }
			 }
		 } else {
			 // The user has not granted permission.
			 Log.i(TAG, "The user did not grant location permission.")

			 // Add a default marker, because the user hasn't selected a place.
			 map?.addMarker(MarkerOptions()
				 .title(getString(R.string.default_info_title))
				 .position(defaultLocation)
				 .snippet(getString(R.string.default_info_snippet)))

			 // Prompt the user for permission.
			 getLocationPermission()
		 }
	 }*/
	
	
}


