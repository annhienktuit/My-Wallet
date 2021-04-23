package com.annhienktuit.mywallet

import android.Manifest
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import com.annhienktuit.mywallet.utils.Extensions.toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_map.*


class MapActivity : AppCompatActivity() {
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        btnLocation.setOnClickListener {
            fetchLocation()
        }
    }
    private fun fetchLocation(){
        val task = fusedLocationProviderClient.lastLocation
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),101)
            return
        } else {

        }
        task.addOnSuccessListener {
            if(it!=null){
                textViewLocation.text = "Latitude: ${it.latitude}" + "\nLongtitude: ${it.longitude}"
                Log.i("location","Latitude: ${it.latitude}" + "\nLongtitude: ${it.longitude}")
            }
            else {
                toast("deo lay dc vi tri")
            }
        }
        task.addOnFailureListener {
            toast("dit me")
        }
    }
}




