package com.vickikbt.bebabeba.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.vickikbt.bebabeba.R
import com.vickikbt.bebabeba.databinding.ActivityRiderLoginBinding
import kotlinx.android.synthetic.main.activity_rider_login.*
import spencerstudios.com.bungeelib.Bungee

class RiderLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiderLoginBinding
    private var firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rider_login)

        linearlayout_rider_login.setOnClickListener{
            startActivity(Intent(this, RiderRegisterActivity::class.java))
            Bungee.slideLeft(this)
        }

        btn_rider_login.setOnClickListener {
            loginRider()
        }
    }

    private fun loginRider(){
        val email = binding.riderEmailLogin.text.toString()
        val password = binding.riderPasswordLogin.text.toString()

        when {
            email.isEmpty() -> Toast.makeText(this, "Enter Email Address!", Toast.LENGTH_SHORT).show()
            password.isEmpty() -> Toast.makeText(this, "Enter Password!", Toast.LENGTH_SHORT).show()
        }

        if (email.isEmpty() || password.isEmpty()) {
            return
        }

        progressbar_login.visibility= View.VISIBLE

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    progressbar_login.visibility= View.GONE
                    startActivity(Intent(this, RiderMapsActivity::class.java))
                    finish()
                }
            }
            .addOnFailureListener {
                progressbar_login.visibility= View.GONE
                Toast.makeText(this, "Failed to login user because: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    //Function to check if the user has already signed in
    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, RiderMapsActivity::class.java))
            finish()
        }
    }

}
