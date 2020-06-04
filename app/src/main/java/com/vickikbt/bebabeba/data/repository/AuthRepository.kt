package com.vickikbt.bebabeba.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.vickikbt.bebabeba.data.model.Riders
import com.vickikbt.bebabeba.utils.Constants.SAVE_USER_NODE

class AuthRepository {

    private var firebaseAuth = FirebaseAuth.getInstance()
    private var firebaseDatabase = FirebaseDatabase.getInstance()

    private val uid = firebaseAuth.uid ?: ""
    private val databaseRef = firebaseDatabase.getReference(SAVE_USER_NODE + uid)

    /*
    *  This is a function that enables a user to login
    * */
    fun login(email: String, password: String) =
        firebaseAuth.signInWithEmailAndPassword(email, password)

    /*
    *  This is a function that enables a creation of  a new user
    * */
    fun register(email: String, password: String) =
        firebaseAuth.createUserWithEmailAndPassword(email, password)

    /*
    *  This is a function that enables a user Sign out
    * */
    fun signOut() = firebaseAuth.signOut()

    /*
    *  This is a function that checks if a user is currently logged in
    *  and if he or she is already logged in he will be redirected  to RequestRide Activity
    * */
    fun getCurrentUser() = firebaseAuth.currentUser

    /*
    *  This is a function that saves a user inside realtime firebase database
    *
    * */
    fun saveUser(riders: Riders) =
        firebaseDatabase.reference.child(databaseRef.toString()).child(riders.UID).setValue(riders)

}