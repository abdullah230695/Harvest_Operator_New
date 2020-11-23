package com.mrrights.harvestoperator.fragments

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.mrrights.harvestoperator.R
import com.mrrights.harvestoperator.activities.ActivitySignIn
import com.mrrights.harvestoperator.adapters.AdapterMachines
import com.mrrights.harvestoperator.models.ModelMachine
import com.mrrights.harvestoperator.utils.SharedPrefs
import kotlinx.android.synthetic.main.fragment_profile.*


class FragmentProfile : Fragment()
{
	
	private val TAG : String = "FragmentProfile"
	
	//progressDialog
	private lateinit var progressDialog : ProgressDialog
	
	//firestore
	private lateinit var firestore : FirebaseFirestore
	
	//SharedPreference
	private lateinit var userSP : SharedPrefs
	
	//listMachines
	private lateinit var listMachines : List<ModelMachine>
	
	
	override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View?
	{
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_profile, container, false)
	}
	
	override fun onViewCreated(view : View, savedInstanceState : Bundle?)
	{
		super.onViewCreated(view, savedInstanceState)
		
		userSP = SharedPrefs(requireContext(), "USER")
		listMachines = ArrayList<ModelMachine>()
		firestore = FirebaseFirestore.getInstance()
		
		//progressDialog init
		progressDialog = ProgressDialog(context)
		progressDialog.setMessage("Loading")
		progressDialog.setCancelable(false)
		
		//set Values for the views
		setValues()
		
		imageViewLogout.setOnClickListener {
			this.userSP.clearSharedPreference()
			startActivity(Intent(this.requireContext(), ActivitySignIn::class.java))
			activity?.finish()
		}
		
		imageViewEdit.setOnClickListener {
			replaceFragment()
		}
		
		
	}
	
	private fun setValues()
	{
		//progress showing
		progressDialog.show()
		
		editTextName.text = userSP.getString("name")
		
		if (userSP.getInt("operatorType") == 1)
		{
			textViewOperatorType.text = "Owner"
		}
		else if (userSP.getInt("operatorType") == 2)
		{
			textViewOperatorType.text = "Driver"
		}
		
		//get Machine Details
		getMachines()
	}
	
	private fun getMachines()
	{
		
		val machineCollection = firestore.collection("Machines")
		machineCollection.whereEqualTo("operatorID", this.userSP.getString("id")).get().addOnSuccessListener { documents ->
			if (! documents.isEmpty)
			{
				for (document in documents)
				{
					val machine = document.toObject(ModelMachine::class.java)
					(listMachines as ArrayList<ModelMachine>).add(machine)
				}
				recyclerViewMachines.layoutManager = LinearLayoutManager(context)
				recyclerViewMachines.adapter = AdapterMachines(activity !!.applicationContext, listMachines)
				progressDialog.dismiss()
				if (! progressDialog.isShowing)
				{
					consProfile.visibility = View.VISIBLE
					textViewMachineCount.visibility = View.GONE
					recyclerViewMachines.visibility = View.VISIBLE
				}
				
			}
			else
			{
				progressDialog.dismiss()
				consProfile.visibility = View.VISIBLE
				recyclerViewMachines.visibility = View.GONE
				textViewMachineCount.visibility = View.VISIBLE
			}
		}.addOnFailureListener {
			progressDialog.dismiss()
			consProfile.visibility = View.VISIBLE
			recyclerViewMachines.visibility = View.GONE
			textViewMachineCount.visibility = View.VISIBLE
			Toast.makeText(context, "Error in getting Machines ${it.message}", Toast.LENGTH_SHORT).show()
		}
		
	}
	
	private fun replaceFragment()
	{
		
		fragmentManager !!.beginTransaction().replace(R.id.framelayout, FragmentProfileEdit()).addToBackStack(null).commit()
		Log.d(TAG, "Fragment Replaced...")
	}
	
	
}