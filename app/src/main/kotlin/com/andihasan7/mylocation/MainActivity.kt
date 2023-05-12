package com.andihasan7.mylocation

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlin.math.abs
import kotlin.math.round
import android.location.Geocoder
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var latitude: TextView
    private lateinit var longitude: TextView
    private lateinit var elevation: TextView
    private lateinit var acuration: TextView
    var doubleLatitude = 0.0
    var doubleLongitude = 0.0
    var doubleElevation = 0.0
    var floatAcuration = 0.0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        latitude = findViewById(R.id.tv_latitude)
        longitude = findViewById(R.id.tv_longitude)
        elevation = findViewById(R.id.tv_elevation)
        acuration = findViewById(R.id.tv_acuration)
        val btnRefresh = findViewById<Button>(R.id.btn_find)

        btnRefresh.setOnClickListener {
            getLocation()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getLocation() {
        // check location permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 100)

                return
        }

        // get latitude & longitude
        val location = fusedLocationProviderClient.lastLocation
        location.addOnSuccessListener {
            if (it != null) {
                doubleLatitude = it.latitude
                doubleLongitude = it.longitude
                doubleElevation = it.altitude
                floatAcuration = it.accuracy
                latitude.text = "Latitude: ${convertToDegrees(doubleLatitude)}"
                longitude.text = "Longitude: ${convertToDegrees(doubleLongitude)}"
                elevation.text = "Elevation: ${doubleElevation.round(2)} meter"
                acuration.text = "Acuration: $floatAcuration meter"
                // kode getKecamatan
                getAddress(doubleLatitude, doubleLongitude)
            }
        }
    }
    
    private fun getAddress(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val kecamatan = address.subLocality
                // set text ke TextView dengan id tv_kecamatan
                findViewById<TextView>(R.id.tv_kecamatan).text = "$kecamatan"
            } else {
                // handle kasus ketika addresses kosong atau null
                findViewById<TextView>(R.id.tv_kecamatan).text = "Error kecamatan tidak ditemukan"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun convertToDegrees(decimals: Double): String {
        var degree = abs(decimals).toInt().toString()
        var minute = ((abs(decimals) - degree.toDouble()) * 60).toInt().toString()
        var second = ((((abs(decimals) - degree.toDouble()) * 60) - minute.toDouble()) * 60).round(2).toString()

        // tambahkan nol sebelum angka yang kurang dari 10
        degree = degree.padStart(2, '0')
        minute = minute.padStart(2, '0')
        second = second.padStart(2, '0')

        if (decimals < 0) {
            degree = "-$degree"
        }
        return "$degree\u00B0 $minute\u0027 $second\u0022"
    }

    // custom round ke 2 digit
    private fun Double.round(decimals: Int) : Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return round(this * multiplier ) / multiplier
    }
}