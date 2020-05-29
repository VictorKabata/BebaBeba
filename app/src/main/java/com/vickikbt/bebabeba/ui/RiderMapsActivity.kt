package com.vickikbt.bebabeba.ui

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.vickikbt.bebabeba.R
import com.vickikbt.bebabeba.adapter.DriversNearbyAdapter
import com.vickikbt.bebabeba.model.DriversNearby
import kotlinx.android.synthetic.main.activity_rider_maps.*

class RiderMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    //Location
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

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
        const val REQUEST_CHECK_SETTINGS = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rider_maps)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                lastLocation = p0.lastLocation
            }
        }

        riderUID = FirebaseAuth.getInstance().currentUser!!.uid
        taxiRequests = FirebaseDatabase.getInstance().reference.child("GeoFire/TaxiRequests")
        driversAvailable = FirebaseDatabase.getInstance().reference.child("GeoFire/DriversAvailable")

        createLocationRequest()

        btn_request_ride.setOnClickListener {
            setPickUpLocation()
        }

        btn_cancel_ride.setOnClickListener {
            cancelRequest()
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        checkLocationPermission()
        getMyLocation()

    }

    private fun getMyLocation() {
        mMap.isMyLocationEnabled = true

        fusedLocationProviderClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng))
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17f))
            }
        }

    }

    private fun setPickUpLocation() {
        val currentPosition = LatLng(lastLocation.latitude, lastLocation.longitude)
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(currentPosition))

        savePickUpLocationToFirebase()
    }

    private fun savePickUpLocationToFirebase() {
        val riderUID = FirebaseAuth.getInstance().currentUser!!.uid
        val taxiRequests = FirebaseDatabase.getInstance().reference.child("GeoFire/TaxiRequests")
        val geoFire = GeoFire(taxiRequests)

        geoFire.setLocation(riderUID, GeoLocation(lastLocation.latitude, lastLocation.longitude)) { _, error ->
            if (error == null) {
                btn_request_ride.visibility = View.GONE
                btn_cancel_ride.visibility = View.VISIBLE
                recyclerview_drivers .visibility = View.VISIBLE

                loadRecyclerView()
                Toast.makeText(applicationContext, "Taxi request made", Toast.LENGTH_SHORT).show()
            }
        }

        getNearbyDrivers()
    }

    private fun getNearbyDrivers() {
        val geoFire = GeoFire(driversAvailable)
        val geoQuery = geoFire.queryAtLocation(GeoLocation(lastLocation.latitude, lastLocation.longitude), radius)
        geoQuery.removeAllListeners()

        geoQuery.addGeoQueryEventListener(object : GeoQueryEventListener {
            override fun onKeyEntered(key: String?, location: GeoLocation?) {
                /*if (!driverFound) {
                    driverFound = true
                    driverFoundId = key
                }*/

                driverFound = true
                driverFoundId = key

                val driverDatabaseRef: DatabaseReference =
                    FirebaseDatabase.getInstance().reference.child("Users").child("Drivers").child(driverFoundId!!)
                val hashMap = HashMap<String, String>()
                hashMap["CustomerRideID"] = riderUID!!
                driverDatabaseRef.updateChildren(hashMap as Map<String, Any>)

                getDriverLocation()

                Log.e("VickiKbt", "onKeyEntered invoked!")
            }

            override fun onKeyExited(key: String?) {
                Log.e("VickiKbt", "onKeyExited invoked!")
            }

            override fun onKeyMoved(key: String?, location: GeoLocation?) {
                Log.e("VickiKbt", "onKeyMoved invoked!")
            }

            override fun onGeoQueryReady() {
                //If driver is not found increase radius and perform recursion
                if (!driverFound) {
                    getNearbyDrivers()
                }
                Log.e("VickiKbt", "Outer onGeoQueryReady invoked!")
            }

            override fun onGeoQueryError(error: DatabaseError?) {
                Log.e("VickiKbt", "onGeoQueryError invoked!")
            }

        })
    }

    private fun getDriverLocation() {
        val driverWorkingRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("GeoFire/DriversAvailable").child(driverFoundId!!).child("l")
        driverWorkingRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.e("VickiKbt", "closestDriver OnCanceled called invoked!")

                    val listMap = dataSnapshot.value as List<*>?
                    var locationLat = 0.0
                    var locationLng = 0.0
                    Toast.makeText(applicationContext, "Driver found!", Toast.LENGTH_SHORT).show()

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

    private fun cancelRequest() {
        val geoFire = GeoFire(taxiRequests)

        geoFire.removeLocation(riderUID) { _, error ->
            if (error == null) {
                val currentPosition = LatLng(lastLocation.latitude, lastLocation.longitude)
                btn_request_ride.visibility = View.VISIBLE
                btn_cancel_ride.visibility = View.GONE
                recyclerview_drivers .visibility = View.GONE
                Toast.makeText(applicationContext, "Taxi request cancelled!", Toast.LENGTH_SHORT).show()
                mMap.clear()
                mMap.animateCamera(CameraUpdateFactory.newLatLng(currentPosition))
            }
        }
    }

    private fun loadRecyclerView() {
        val data: MutableList<DriversNearby> = ArrayList()
        for (i in 1..10)
            data.add(DriversNearby("Driver $i"))

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerview_drivers.layoutManager = layoutManager
        val adapter = DriversNearbyAdapter(data)
        recyclerview_drivers.setHasFixedSize(true)
        recyclerview_drivers.adapter = adapter
    }

    private fun checkLocationPermission() {
        //Checks if the app is granted location access and if not it sak for location access.
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 1000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }

        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                startLocationUpdates()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    public override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }
    }

    override fun onStop() {
        super.onStop()
        val geoFire = GeoFire(taxiRequests)

        geoFire.removeLocation(riderUID) { _, error ->
            if (error == null) {
                Toast.makeText(applicationContext, "Taxi request cancelled!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val geoFire = GeoFire(taxiRequests)

        geoFire.removeLocation(
            riderUID, GeoFire.CompletionListener { key, error ->
                if (error == null) {
                    Toast.makeText(applicationContext, "Taxi request cancelled!", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
