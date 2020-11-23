package com.mrrights.harvestoperator.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.mrrights.harvestoperator.R
import com.mrrights.harvestoperator.utils.SharedPrefs
import kotlinx.android.synthetic.main.activity_sign_in.*

class ActivitySignIn : AppCompatActivity() {

    private val TAG = "ActivitySignIn"

    private var phoneNo = ""
    private var password = ""

    //progressDialog
    private lateinit var progressDialog: ProgressDialog

    //firestore
    private lateinit var firestore: FirebaseFirestore

    //SharedPreference
    private lateinit var userSP: SharedPrefs

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        userSP = SharedPrefs(this, "USER")

        //progressDialog init
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)

        buttonCreateAccount.setOnClickListener {

            val intent = Intent(this, ActivitySignUp::class.java)
            startActivity(intent)

        }

        textViewSignIn.setOnClickListener {

            if (cardViewSignUp.isVisible) {
                cardViewSignUp.visibility = View.GONE
                cardViewSignIn.visibility = View.VISIBLE
            }
        }

        textViewSignUp.setOnClickListener {

            startActivity(Intent(this, ActivitySignUp::class.java))

        }

        buttonSignIn.setOnClickListener {

            varInit()
            if (validation()) {
                signIn()
            }

        }

    }


    //validation
    private fun validation(): Boolean {

        var flag = true

        if (phoneNo == "") {

            Toast.makeText(this, "Enter your Phone Number", Toast.LENGTH_SHORT).show()
            flag = false

        } else if (phoneNo.length != 10) {

            Toast.makeText(this, "Enter your 10 digit Phone Number", Toast.LENGTH_SHORT).show()
            flag = false

        }

        if (password == "") {

            Toast.makeText(this, "Enter your Password", Toast.LENGTH_SHORT).show()
            flag = false

        }
        return flag
    }


    //variable Initialization
    private fun varInit() {

        phoneNo = editTextPhoneNo.text.toString()
        password = editTextPassword.text.toString()

    }


    //signing In
    private fun signIn() {
        progressDialog.setMessage("Signing In")
        progressDialog.show()

        Log.d(TAG, "Signing In")

        firestore = FirebaseFirestore.getInstance()
        val opUserReference: CollectionReference = firestore.collection("OperatorUsers")

        opUserReference.whereEqualTo("phoneNo", phoneNo.toLong()).whereEqualTo("password", password).get().addOnSuccessListener { documents ->

            if (!documents.isEmpty) {
                Log.i(TAG, "Signed In docs")
                for (document in documents) {
                    Toast.makeText(this, "Signed In", Toast.LENGTH_SHORT).show()
                    Log.i(TAG, "Signed In : ${document.id} = ${document.data}")

                    userSP.saveString("id", document.id)
                    userSP.saveString(
                        "coverPic", document.getString("coverPic").toString()
                    )
                    userSP.saveString(
                        "profilePic", document.getString("profilePic").toString()
                    )
                    userSP.saveString("name", document.getString("name").toString())
                    userSP.saveInt(
                        "operatorType", document.getLong("operatorType")!!.toInt()
                    )
                    userSP.saveLong("phoneNo", document.getLong("phoneNo")!!)
                    userSP.saveString(
                        "password", document.getString("password").toString()
                    )
                    Log.i(TAG, "Details Saved in Shared Preference : ${document.id} ")

                    progressDialog.dismiss()

                    if (!progressDialog.isShowing) {
                        startActivity(Intent(this, ActivityMain::class.java))
                        finish()
                    }
                }
            } else {
                progressDialog.dismiss()

                if (!progressDialog.isShowing) {
                    Toast.makeText(this, "Username or Password is Wrong!", Toast.LENGTH_SHORT).show()
                    Log.w(TAG, "Username or Password is Wrong : No data found")
                }
            }
        }.addOnFailureListener { exception ->
            progressDialog.dismiss()

            if (!progressDialog.isShowing) {
                Toast.makeText(this, "Error Signing In ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Error Signing In : ", exception)
            }
        }
    }


}
