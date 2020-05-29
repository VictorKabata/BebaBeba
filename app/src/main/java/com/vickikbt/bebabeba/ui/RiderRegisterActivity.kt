package com.vickikbt.bebabeba.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.vickikbt.bebabeba.R
import com.vickikbt.bebabeba.databinding.ActivityRiderRegisterBinding
import com.vickikbt.bebabeba.model.Riders
import kotlinx.android.synthetic.main.activity_rider_register.*
import spencerstudios.com.bungeelib.Bungee

class RiderRegisterActivity : AppCompatActivity() {

    lateinit var binding: ActivityRiderRegisterBinding
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rider_register)


        binding.linearlayoutRiderRegister.setOnClickListener {
            startActivity(Intent(this, RiderLoginActivity::class.java))
            Bungee.slideLeft(this)
            finish()
        }

        binding.btnRiderRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val username = binding.riderNameRegister.text.toString()
        val email = binding.riderEmailRegister.text.toString()
        val phone = binding.riderPhoneRegister.text.toString()
        val password = binding.riderPasswordRegister.text.toString()

        when {
            username.isEmpty() -> Toast.makeText(this, "Enter Username!", Toast.LENGTH_SHORT).show()
            email.isEmpty() -> Toast.makeText(this, "Enter Email!", Toast.LENGTH_SHORT).show()
            phone.isEmpty() -> Toast.makeText(this, "Enter Phone Number!", Toast.LENGTH_SHORT).show()
            password.isEmpty() -> Toast.makeText(this, "Enter Password!", Toast.LENGTH_SHORT).show()
            password.length < 8 -> Toast.makeText(this, "Password is too short!", Toast.LENGTH_SHORT).show()
        }

        if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            return
        }

        progressbar_register.visibility = View.VISIBLE

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                progressbar_register.visibility = GONE
                saveOtherCredentialsToFirebase()
                if (!it.isSuccessful) return@addOnCompleteListener
                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener {
                progressbar_register.visibility = GONE
                Toast.makeText(this, "Failed to create user because: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveOtherCredentialsToFirebase() {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val databaseRef = FirebaseDatabase.getInstance().getReference("/Users/Riders/$uid")

        val username = binding.riderNameRegister.text.toString()
        val email = binding.riderEmailRegister.text.toString()
        val phone = binding.riderPhoneRegister.text.toString()

        val riders = Riders(username, email, phone, uid)

        databaseRef.setValue(riders)
            .addOnSuccessListener {
                startActivity(Intent(this, RiderLoginActivity::class.java))
                finish()
            }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, RiderLoginActivity::class.java))
        finish()
        Bungee.slideRight(this)

    }


}
