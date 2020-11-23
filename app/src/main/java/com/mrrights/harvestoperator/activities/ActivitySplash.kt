package com.mrrights.harvestoperator.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.mrrights.harvestoperator.R
import com.mrrights.harvestoperator.utils.SharedPrefs

class ActivitySplash : AppCompatActivity() {

    private var TAG = "ActivitySplash"


    //firestore
    private lateinit var firestore: FirebaseFirestore

    //SharedPreference
    private lateinit var userSP: SharedPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        firestore = FirebaseFirestore.getInstance()

        userSP = SharedPrefs(this, "USER")

        Handler().postDelayed({
            Log.d(TAG, "Checking User Details")
            if (userSP.getString("id") != null) {

                val opUserReference: CollectionReference = firestore.collection("OperatorUsers")

                opUserReference.whereEqualTo("phoneNo", userSP.getLong("phoneNo").toString().toLong()).whereEqualTo("password", userSP.getString("password")).get()
                    .addOnSuccessListener { documents ->

                        if (documents.isEmpty) {

                            Toast.makeText(this, "Sign Up", Toast.LENGTH_SHORT).show()
                            Log.w(TAG, "Username or Password : No data found")
                            startActivity(Intent(this, ActivitySignUp::class.java))
                            finish()
                        } else {

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
                                userSP.saveString(
                                    "name", document.getString("name").toString()
                                )
                                userSP.saveInt(
                                    "operatorType", document.getLong("operatorType")!!.toInt()
                                )
                                userSP.saveLong(
                                    "phoneNo", document.getLong("phoneNo")!!
                                )
                                userSP.saveString(
                                    "password", document.getString("password").toString()
                                )


                                startActivity(Intent(this, ActivityMain::class.java))
                                finish()


                            }
                        }

                    }.addOnFailureListener { exception ->
                        Toast.makeText(this, "Error Signing In ${exception.message}", Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "Error Signing In : ", exception)
                        startActivity(Intent(this, ActivitySignUp::class.java))
                        finish()
                    }
            } else {
                Log.d(TAG, "No User Details: Try to Login")
                startActivity(Intent(this, ActivitySignIn::class.java))
                finish()
            }
        }, 1000)

    }
}