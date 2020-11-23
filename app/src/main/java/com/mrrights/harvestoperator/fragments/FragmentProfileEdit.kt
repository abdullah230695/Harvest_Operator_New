package com.mrrights.harvestoperator.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.mrrights.harvestoperator.R
import com.mrrights.harvestoperator.utils.Constants.Companion.arrayMachineConditions
import com.mrrights.harvestoperator.utils.Constants.Companion.arrayMachineTypes
import com.mrrights.harvestoperator.utils.SharedPrefs
import com.mrrights.harvestoperator.utils.SimpleSpinner
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import kotlinx.android.synthetic.main.fragment_profile_edit.view.*
import java.util.*


class FragmentProfileEdit : Fragment() {

    var TAG = "FragmentProfileEdit"

    //profile variables
    var name = ""
    var phoneNo = ""
    var heavyLicNo = ""

    //machine variables
    var machineType = 0
    var machineCondition = 0
    var machineNo = ""
    var insNo = ""

    private lateinit var simpleSpinner: SimpleSpinner

    private lateinit var firestore: FirebaseFirestore

    private lateinit var userSP: SharedPrefs

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_profile_edit, container, false)

        firestore = FirebaseFirestore.getInstance()

        simpleSpinner = SimpleSpinner(requireContext())

        userSP = SharedPrefs(requireContext(), "USER")

        //machineConditions Initialization
        view.spinnerMachineCondtion.adapter = simpleSpinner.listSpinner(arrayMachineConditions)
        //machineTypes Initialization
        view.spinnerMachineType.adapter = simpleSpinner.listSpinner(arrayMachineTypes)

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSharedPrefs()

        buttonUpdate.setOnClickListener {

            varProfileInit()
            if (profileValidation()) {
                updateProfile()
            }

        }

        textViewAddMachine.setOnClickListener {

            varAddMachineInit()
            if (machineValidation()) {
                addNewMachine()
            }

        }

        imageViewBack.setOnClickListener {
            activity!!.onBackPressed()
        }

    }


    //Initialization for profile fields
    private fun varProfileInit() {
        name = editTextName.text.toString()
        phoneNo = editTextPhoneNo.text.toString()
        heavyLicNo = editTextHeavyLicNo.text.toString()
    }


    //Initialization for machine fields
    private fun varAddMachineInit() {
        machineType = spinnerMachineType.selectedItemPosition
        machineCondition = spinnerMachineCondtion.selectedItemPosition
        machineNo = editTextMachineNo.text.toString()
        insNo = editTextInsuranceNo.text.toString()
    }


    //getting operator details
    private fun getOperatorUserDetails() {
        val opUserReference: CollectionReference = firestore.collection("OperatorUsers")

        opUserReference.document(userSP.getString("id").toString()).get().addOnSuccessListener { document ->

            if (document.exists()) {
                userSP.saveString("id", document.id)
                userSP.saveString("coverPic", document.getString("coverPic").toString())
                userSP.saveString("profilePic", document.getString("profilePic").toString())
                userSP.saveString("name", document.getString("name").toString())
                userSP.saveInt("operatorType", document.getLong("operatorType")!!.toInt())
                userSP.saveLong("phoneNo", document.getLong("phoneNo")!!)
                Log.i(TAG, "Details Saved in Shared Preference : $document.id ")
            }


        }.addOnFailureListener { exception ->
            Toast.makeText(context, "Error getting Details : ${exception.message}", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "Error getting Details : ", exception)
        }
    }


    //profile validation for updating
    private fun profileValidation(): Boolean {

        when {
            name == "" -> {
                Toast.makeText(context, "Enter your Name", Toast.LENGTH_SHORT).show()
                return false
            }
            phoneNo.length != 10 -> {
                Toast.makeText(context, "Enter your 10 digit Phone Number", Toast.LENGTH_SHORT).show()
                return false
            }
            heavyLicNo.length != 16 -> {
                Toast.makeText(context, "Enter your 10 digit License Number", Toast.LENGTH_SHORT).show()
                return false
            }
            else -> {
                return true
            }

        }

    }


    //updating profile details
    private fun updateProfile() {

        val opUserReference: CollectionReference = firestore.collection("OperatorUsers")
        val docRef = opUserReference.document(userSP.getString("id").toString())
        docRef.update("name", name)
        docRef.update("phoneNo", phoneNo.toLong())
        docRef.update("heavyLicNo", heavyLicNo).addOnSuccessListener {

            Log.i(TAG, "Updated Profile ID:${userSP.getString("id")}")
            Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show()
            getOperatorUserDetails()

        }.addOnFailureListener {

            Log.e(TAG, "Error in Updating Profile", it)
            Toast.makeText(context, "Error in Updating Profile : ${it.message}", Toast.LENGTH_SHORT).show()

        }

    }


    //machine validation for adding new
    private fun machineValidation(): Boolean {
        when {
            machineType == 0 -> {
                Toast.makeText(context, "Select Machine Type", Toast.LENGTH_SHORT).show()
                return false
            }
            machineNo.length != 10 -> {
                Toast.makeText(context, "Enter your 10 Machine Number", Toast.LENGTH_SHORT).show()
                return false
            }
            insNo.length != 16 -> {
                Toast.makeText(context, "Enter your 16 Insurance Number", Toast.LENGTH_SHORT).show()
                return false
            }
            machineCondition == 0 -> {
                Toast.makeText(context, "Select Machine Condition", Toast.LENGTH_SHORT).show()
                return false
            }
            else -> {
                return true
            }
        }
    }


    //add new machine
    private fun addNewMachine() {
        val machineRef = firestore.collection("Machines").document(machineNo)

        val machineDetails: MutableMap<String, Any> = HashMap()
        machineDetails["condition"] = spinnerMachineCondtion.selectedItemPosition
        machineDetails["insuranceNo"] = insNo
        machineDetails["number"] = machineNo
        machineDetails["type"] = spinnerMachineType.selectedItemPosition
        machineDetails["operatorID"] = userSP.getString("id").toString()
        machineDetails["ownerID"] = ""

        machineRef.set(machineDetails).addOnSuccessListener {

            Log.i(TAG, "Machine Added : $machineNo")
            Toast.makeText(context, "New Machine Added", Toast.LENGTH_SHORT).show()

            spinnerMachineType.setSelection(0)
            spinnerMachineCondtion.setSelection(0)
            editTextMachineNo.text = null
            editTextInsuranceNo.text = null

        }.addOnFailureListener {

            Log.e(TAG, "Error in Adding Machine", it)
            Toast.makeText(context, "Error in Adding Machine : ${it.message}", Toast.LENGTH_SHORT).show()

        }
    }


    //setting shared preferences operator details
    private fun setSharedPrefs() {

        editTextName.setText(userSP.getString("name"))
        editTextPhoneNo.setText(userSP.getLong("phoneNo").toString())
        editTextHeavyLicNo.setText(userSP.getString("id"))

    }


}



