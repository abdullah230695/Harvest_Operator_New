package com.mrrights.harvestoperator.utils

import android.Manifest.permission.*
import android.os.Build
import androidx.annotation.RequiresApi

class Constants
{
	
	companion object
	{
		
		//BINARY
		const val DENIED = 0
		const val GRANTED = 1
		
		
		//USER PERMISSION CODES
		const val LOCATION_CODE = 1
		const val LOCATION_SETTING_CODE = 12
		const val EXTERNAL_STORAGE = 2
		
		//GOOGLE MAPS
		const val DEFAULT_ZOOM = 19F
		val DEFAULT_LOCATION = arrayOf(22.7636613, 81.6997576)
		
		
		@RequiresApi(Build.VERSION_CODES.Q) //Android Q version has permission to access location in background
		//LOCATION PERMISSIONS
		val LOCATION_PERMISSIONS = arrayOf(ACCESS_BACKGROUND_LOCATION, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
		
		//STORAGE PERMISSIONS
		val STORAGE_PERMISSION = arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)
		
		//ARRAY OPERATORS FOR SPINNER
		val arrayOperators = arrayOf("Operator", "Owner", "Driver")
		
		//ARRAY MACHINE CONDITION FOR SPINNER
		val arrayMachineConditions = arrayOf("Machine Condition", "Bad", "Good", "Excellent")
		
		//ARRAY MACHINE TYPES FOR SPINNER
		val arrayMachineTypes = arrayOf("Machine Type", "TOT", "Belt", "Combined")
		
		
	}
	
}