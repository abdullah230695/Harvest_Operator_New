package com.mrrights.harvestoperator.activities


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.mrrights.harvestoperator.R
import com.mrrights.harvestoperator.utils.Constants.Companion.arrayOperators
import com.mrrights.harvestoperator.utils.SimpleSpinner
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*

class ActivitySignUp : AppCompatActivity() {

    private val TAG = "ActivitySignUp"

    private lateinit var firestore: FirebaseFirestore

    private var name: String = ""
    private var phoneNo: String = ""
    private var password: String = ""
    private var rePassword: String = ""
    private var operatorType: Int = 0
    private var heavyLicNo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        setSpinner()

        firestore = FirebaseFirestore.getInstance()


        buttonSignUp.setOnClickListener {

            varInit()

            if (validation()) {
                addOperatorUser()
            }
        }
    }

    //setting Operator Spinner
    private fun setSpinner() {

        val simpleSpinner = SimpleSpinner(applicationContext)
        spinnerOperator.adapter = simpleSpinner.listSpinner(arrayOperators)
    }

    //variableInitialization
    private fun varInit() {
        name = editTextName.text.toString()
        phoneNo = editTextPhoneNo.text.toString()
        password = editTextPassword.text.toString()
        rePassword = editTextRePassword.text.toString()
        operatorType = spinnerOperator.selectedItemPosition
        heavyLicNo = editTextHeavyLicNo.text.toString()
    }

    //validation
    private fun validation(): Boolean {
        var flag = true

        if (name == "") {
            editTextName.error = "Enter Name"
            flag = false
        }

        if (phoneNo == "") {
            editTextPhoneNo.error = "Enter Phone Number"
            flag = false
        } else if (phoneNo.length != 10) {
            editTextPhoneNo.error = "Enter 10 digit Phone Number"
            flag = false
        }



        if (password == "") {
            editTextPassword.error = "Enter Password"
            flag = false
        }

        if (rePassword == "") {
            editTextRePassword.error = "Enter password again"
            flag = false
        } else if (password != rePassword) {
            editTextRePassword.error = "Password is not matching"
            flag = false
        }

        if (operatorType == 0) {
            (spinnerOperator.selectedView as TextView).error = "Select Operator"
            flag = false
        }

        if (heavyLicNo == "") {
            editTextHeavyLicNo.error = "Enter Heavy License Number"
            flag = false
        } else if (heavyLicNo.length != 16) {
            editTextHeavyLicNo.error = "Enter 16 digit Heavy License Number"
            flag = false
        }
        return flag
    }


    //phoneNo Exist?
    private fun phoneNoExist(): Boolean {
        var flag = false

        firestore = FirebaseFirestore.getInstance()

        val docRef = firestore.collection("OperatorUsers").whereEqualTo("phoneNo", phoneNo.toLong())

        docRef.addSnapshotListener { documents, e ->

            if (documents != null) {
                for (document in documents) {
                    Log.w(TAG, "Phone Number exists : ${document.get("phoneNo")}")
                    Toast.makeText(this, "Phone Number exists!", Toast.LENGTH_SHORT).show()
                    editTextPhoneNo.error = "Phone Number exists!"
                }

            } else {
                flag = true
                Toast.makeText(this, "Phone Number not exists!", Toast.LENGTH_SHORT).show()
                Log.i(TAG, "phoneNo not exist,")
            }

            if (e != null) {
                Log.i(TAG, "Error in getting phone Number : :", e)
                Toast.makeText(this, "\"Error in getting phone Number : ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        return flag
    }


    //document ID Heavy License Exist?
    private fun licenseExist(): Boolean {
        var flag = false


        val docRef = firestore.collection("OperatorUsers").document(heavyLicNo)

        docRef.addSnapshotListener { document, e ->

            if (document != null && document.exists()) {
                Log.w(TAG, "Document ID(License) exist : ${document.id}")
                editTextHeavyLicNo.error = "Heavy License Already Exist!"
                Toast.makeText(this, "License already exists!", Toast.LENGTH_SHORT).show()
            } else {
                flag = true
                Log.i(TAG, "Document ID(License) not exist")
                Toast.makeText(this, "Document ID(License) not exist", Toast.LENGTH_SHORT).show()
            }

            if (e != null) {
                Log.i(TAG, "Error in getting Document ID(License) :", e)
                Toast.makeText(this, " Error in getting License ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        return flag
    }


    //add new OperatorUser
    private fun addOperatorUser() {

        val opUserRef: CollectionReference = firestore.collection("OperatorUsers")

        val userDetails: MutableMap<String, Any> = HashMap()
        userDetails["name"] = name
        userDetails["phoneNo"] = phoneNo.toLong()
        userDetails["password"] = password
        userDetails["operatorType"] = operatorType
        userDetails["heavyLicNo"] = heavyLicNo
        userDetails["coverPic"] = ""
        userDetails["profilePic"] = ""

        opUserRef.document(heavyLicNo).set(userDetails).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i(TAG, "Registration Success : HeavyLicNo = $heavyLicNo")
                Toast.makeText(this, "Registered Successfully!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, ActivitySignIn::class.java))
                finish()
            } else {
                Log.e(TAG, "Registration Failure : Message = ${task.exception?.message}")
                Toast.makeText(this, "Registration Failed!", Toast.LENGTH_SHORT).show()
            }
        }


    }


}