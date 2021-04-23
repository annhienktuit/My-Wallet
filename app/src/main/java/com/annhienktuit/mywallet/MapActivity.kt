package com.annhienktuit.mywallet

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.annhienktuit.mywallet.utils.Extensions.toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_map.*
import java.io.IOException
import java.util.*


class MapActivity : AppCompatActivity() {
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var latitude:Double = 0.0
    var longtitude:Double  = 0.0
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
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION), 101)
            return
        } else {

        }
        task.addOnSuccessListener {
            if(it!=null){
                textViewLocation.text = "Latitude: ${it.latitude}" + "\nLongtitude: ${it.longitude}"
                Log.i("location","Latitude: ${it.latitude}" + "\nLongtitude: ${it.longitude}")
                latitude = it.latitude
                longtitude = it.longitude
                convertLocation()
            }
            else {
                toast("deo lay dc vi tri")
            }
        }
        task.addOnFailureListener {
            toast("dit me")
        }
    }
    fun convertLocation(){
        val geocoder = Geocoder(this, Locale.ENGLISH)
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longtitude, 1)
            if (addresses != null) {
                val returnedAddress: Address = addresses[0]
                val strReturnedAddress = StringBuilder("Address:${addresses[0].featureName}, ${addresses[0].locality}, ${addresses[0].adminArea}")
                for (i in 0 until returnedAddress.getMaxAddressLineIndex()) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                textViewLocation.setText(strReturnedAddress.toString())
            } else {
                textViewLocation.setText("No Address returned!")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            textViewLocation.setText("Can not get Address!")
        }
    }
}




