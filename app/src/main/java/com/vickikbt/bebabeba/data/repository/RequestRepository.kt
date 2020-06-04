package com.vickikbt.bebabeba.data.repository

import android.location.Location
import android.view.View
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.vickikbt.bebabeba.utils.Constants.DRIVER_AVAILABLE_NODE
import com.vickikbt.bebabeba.utils.Constants.TAXI_REQUEST_NODE
import com.vickikbt.bebabeba.utils.toast
import timber.log.Timber

class RequestRepository(
    private val authRepository: AuthRepository
) {
    private lateinit var mMap: GoogleMap

    private var firebaseAuth = FirebaseAuth.getInstance()
    private var firebaseDatabase = FirebaseDatabase.getInstance()


    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false

    private lateinit var lastLocation: Location

    private var riderUID: String? = null
    private var taxiRequests: DatabaseReference? = null
    var driversAvailable: DatabaseReference? = null

    private var radius = 5.0
    private var driverFound: Boolean = false
    private var driverFoundId: String? = null


    fun savePickUpLocationToFirebase(view: View) {
        riderUID = firebaseAuth.currentUser!!.uid
        taxiRequests = firebaseDatabase.reference.child(TAXI_REQUEST_NODE)
        driversAvailable = firebaseDatabase.reference.child(DRIVER_AVAILABLE_NODE)
        val geoFire = GeoFire(taxiRequests)

        geoFire.setLocation(riderUID, GeoLocation(lastLocation.latitude, lastLocation.longitude))

        getNearbyDrivers(view)
    }

    fun getNearbyDrivers(view: View) {
        val geoFire = GeoFire(driversAvailable)
        val geoQuery = geoFire.queryAtLocation(GeoLocation(lastLocation.latitude, lastLocation.longitude), radius)
        geoQuery.removeAllListeners()

        geoQuery.addGeoQueryEventListener(object : GeoQueryEventListener {
            override fun onKeyEntered(key: String?, location: GeoLocation?) {

                driverFound = true
                driverFoundId = key

                val driverDatabaseRef: DatabaseReference =
                    FirebaseDatabase.getInstance().reference.child("Users").child("Drivers").child(driverFoundId!!)
                val hashMap = HashMap<String, String>()
                hashMap["CustomerRideID"] = riderUID!!
                driverDatabaseRef.updateChildren(hashMap as Map<String, Any>)

                getDriverLocation(view)

                Timber.tag("VickiKbt").e("onKeyEntered invoked!")
            }

            override fun onKeyExited(key: String?) {
                Timber.tag("VickiKbt").e("onKeyExited invoked!")
            }

            override fun onKeyMoved(key: String?, location: GeoLocation?) {
                Timber.tag("VickiKbt").e("onKeyMoved invoked!")
            }

            override fun onGeoQueryReady() {
                //If driver is not found increase radius and perform recursion
                if (!driverFound) {
                    getNearbyDrivers(view)
                }
                Timber.tag("VickiKbt").e("Outer onGeoQueryReady invoked!")
            }

            override fun onGeoQueryError(error: DatabaseError?) {
                Timber.tag("VickiKbt").e("onGeoQueryError invoked!")
            }

        })
    }

    fun getDriverLocation(view: View) {
        val driverWorkingRef =
            firebaseDatabase.reference.child(DRIVER_AVAILABLE_NODE).child(driverFoundId!!).child("l")

        driverWorkingRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    Timber.tag("VickiKbt").e("closestDriver OnCanceled called invoked!")

                    val listMap = dataSnapshot.value as List<*>?
                    var locationLat = 0.0
                    var locationLng = 0.0

                    view.context.toast("Driver found!")


                    if (listMap!![0] != null) {
                        locationLat = listMap[0].toString().toDouble()
                    }
                    if (listMap[1] != null) {
                        locationLng = listMap[1].toString().toDouble()
                    }
                    val driverLocation = LatLng(locationLat, locationLng)
                    //mMap.clear()
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(driverLocation))
                    mMap.addMarker(MarkerOptions().position(driverLocation).title("Your driver!"))
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}