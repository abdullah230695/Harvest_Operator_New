package com.mrrights.harvestoperator.activities

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.mrrights.harvestoperator.R
import com.mrrights.harvestoperator.fragments.FragmentBooking
import com.mrrights.harvestoperator.fragments.FragmentHome
import com.mrrights.harvestoperator.fragments.FragmentProfile
import com.mrrights.harvestoperator.utils.SharedPrefs
import kotlinx.android.synthetic.main.activity_main.*

class ActivityMain : AppCompatActivity()
{
	
	private val TAG : String = "ActivityMain"
	
	//progressDialog
	private lateinit var progressDialog : ProgressDialog
	
	//firestore
	private lateinit var firestore : FirebaseFirestore
	
	//SharedPreference
	private lateinit var sp : SharedPrefs
	
	override fun onCreate(savedInstanceState : Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		
		//adding HomeFragment
		replaceFragment(FragmentHome())
		
		bottomNavBar.setOnNavigationItemSelectedListener {
			when (it.itemId)
			{
                R.id.menuHome ->
                {
                    Log.d(TAG, "Replacing FragmentHome...")
                    replaceFragment(FragmentHome())
                    true
                }
                R.id.menuBooking ->
                {
                    Log.d(TAG, "Replacing FragmentBooking...")
                    replaceFragment(FragmentBooking())
                    true
                }
                R.id.menuProfile ->
                {
                    Log.d(TAG, "Replacing FragmentProfile...")
                    replaceFragment(FragmentProfile())
                    true
                }
				else -> false
			}
		}
	}
	
	private fun replaceFragment(fragment : Fragment)
	{
		supportFragmentManager.beginTransaction().replace(R.id.framelayout, fragment).addToBackStack(null).commit()
		Log.d(TAG, "Fragment Replaced...")
	}
	
}