package com.annhienktuit.mywallet.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.annhienktuit.mywallet.R
import com.annhienktuit.mywallet.utils.Extensions.toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_map.*
import java.io.IOException
import java.util.*


class MapActivity : AppCompatActivity() {
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var latitude: Double = 0.0
    var longtitude: Double = 0.0
    var queryType: String = ""
    var isFindingBank:Boolean = false //false is atm, true is bank
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation() //innitialize location
        btnfindATM.setOnClickListener {
            isFindingBank = false
            fetchLocationandOpenMap() //get location
        }
        btnfindBank.setOnClickListener { 
            isFindingBank = true
            fetchLocationandOpenMap()
        }
    }

    private fun fetchLocationandOpenMap() {
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
            if (it != null) {
                textViewLocation.text = "Latitude: ${it.latitude}" + "\nLongtitude: ${it.longitude}"
                latitude = it.latitude
                longtitude = it.longitude
                convertLocation() //covert latitude to address
                openMap()
            } else {
                toast("Cannot get location")
            }
        }
        task.addOnFailureListener {
            toast("Failed")
        }
    }

    fun convertLocation() {
        val geocoder = Geocoder(this, Locale.ENGLISH)
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longtitude, 1)
            if (addresses != null) {
                val returnedAddress: Address = addresses[0]
                Log.i("location",addresses[0].toString())
                val strReturnedAddress = StringBuilder("Your address:${returnedAddress.featureName}, ${returnedAddress.thoroughfare}, ${returnedAddress.locality}, ${returnedAddress.countryName}")
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

    fun openMap() {
        //still okay but not optimize
//        val uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longtitude) //open google maps
//        val uri = "https://www.google.com/maps/search/atm/@$latitude,$longtitude"
//        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        if(!isFindingBank) queryType = "atm"
        else queryType = "bank"
        toast("Finding nearby $queryType")
        val mapIntent: Intent = Uri.parse(
                "geo:$latitude,$longtitude?q=$queryType ${textViewBankName.text.toString()}"
        ).let { location ->
            Intent(Intent.ACTION_VIEW, location)
        }
        startActivity(mapIntent)
    }

    fun fetchLocation() {
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
            if (it != null) {
                latitude = it.latitude
                longtitude = it.longitude
                convertLocation() //covert latitude to address
            }
        }
    }
}




