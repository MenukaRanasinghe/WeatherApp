package com.example.weatherapp

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.ForecastActivity.Companion.EXTRA_LATITUDE
import com.example.weatherapp.ForecastActivity.Companion.EXTRA_LONGITUDE
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import org.w3c.dom.Text
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


private lateinit var txtLocation: TextView
private lateinit var txtWeather: TextView
private lateinit var txtDate: TextView
private lateinit var txtTime: TextView
private lateinit var txtTemp: TextView
private lateinit var txtUV: TextView
private lateinit var txtWind: TextView
private lateinit var txtHumidity: TextView
private lateinit var imgWeather: ImageView
private lateinit var txt_next_seven_days: TextView
private lateinit var iconLocation: ImageView
private lateinit var txtSearch: TextView
private lateinit var fusedLocationClient: FusedLocationProviderClient

private val apiKey = "a863eb50a11a18b6b9b2d9badc169c24"

private var progressDialog: ProgressDialog? = null

class MyLocationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_location)

        txtLocation = findViewById(R.id.txtLocation)
        txtWeather = findViewById(R.id.txtWeather)
        txtDate = findViewById(R.id.txtDate)
        txtTime = findViewById(R.id.txtTime)
        txtTemp = findViewById(R.id.txtTemp)
        txtUV = findViewById(R.id.txtUV)
        txtWind = findViewById(R.id.txtWind)
        txtHumidity = findViewById(R.id.txtHumidity)
        imgWeather = findViewById(R.id.imgWeather)
        iconLocation = findViewById(R.id.iconLocation)
        txt_next_seven_days = findViewById(R.id.txt_next_seven_days)
        txtSearch = findViewById(R.id.txtSearch)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        requestLocationPermission()
        getCurrentLocationAndLoadWeather()


        txtSearch.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                val cityName = txtSearch.text.toString().trim()
                if (cityName.isNotEmpty()) {

                    loadWeatherInfo(cityName)
                }else{
                    getCurrentLocationAndLoadWeather()
                }
                return@setOnEditorActionListener true
            }
            false
        }

    }

    private fun loadWeatherInfo(cityName: String) {
        val url =
            "https://api.openweathermap.org/data/2.5/weather?q=$cityName&units=metric&appid=$apiKey"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                parseWeatherDetails(response)
//                loadWeatherInfo()
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
            })


        Volley.newRequestQueue(this).add(request)
    }

    private fun parseWeatherDetails(response: JSONObject) {
        try {
            val cityName = response.getString("name")
            txtLocation.text = cityName

            val main = response.getJSONObject("main")
            val temp = main.getString("temp")
            val humidity = main.getString("humidity")
            val windSpeed = response.getJSONObject("wind").getString("speed")
            val uvIndex = getUVIndex(0.0)
            val coordObject = response.getJSONObject("coord")
            val latitude = coordObject.getDouble("lat")
            val longitude = coordObject.getDouble("lon")

            txtTemp.text = "$temp °C"
            txtHumidity.text = "$humidity%"
            txtWind.text = "$windSpeed km/h"
            txtUV.text = "$uvIndex"

            Log.d("WeatherApp", "Latitude: $latitude, Longitude: $longitude")

            val timeZone = getTimeZone(latitude, longitude)

            txt_next_seven_days.setOnClickListener(){

                val intent = Intent(this, ForecastActivity::class.java)
                intent.putExtra(EXTRA_LATITUDE, latitude)
                intent.putExtra(EXTRA_LONGITUDE, longitude)
                startActivity(intent)
                startActivity(Intent(this, ForecastActivity::class.java))
            }

            val sdfDate = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
            val sdfTime = SimpleDateFormat("h:mm a", Locale.getDefault())

            sdfDate.timeZone = timeZone
            sdfTime.timeZone = timeZone

            val currentDate = sdfDate.format(Date())
            val currentTime = sdfTime.format(Date())

            Log.d("WeatherApp", "Current Date: $currentDate")
            Log.d("WeatherApp", "Current Time: $currentTime")

            runOnUiThread {
                txtDate.text = currentDate
                txtTime.text = currentTime
            }

            val iconCode = response.getJSONArray("weather").getJSONObject(0)
                .getString("icon")
            val imgIconUrl =
                "https://openweathermap.org/img/w/$iconCode.png"

            Picasso.get().load(imgIconUrl).into(imgWeather, object : Callback {
                override fun onSuccess() {
                    dismissProgressDialog()
                }

                override fun onError(e: Exception?) {
                    Log.e("Picasso", "Error loading image: ${e?.message}")
                    dismissProgressDialog()
                }
            })

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun getTimeZone(latitude: Double, longitude: Double): TimeZone {
        val geoCoder = Geocoder(this, Locale.getDefault())
        val addresses = geoCoder.getFromLocation(latitude, longitude, 1)

        return if (addresses != null) {
            val timeZoneId = java.util.TimeZone.getDefault().id
            TimeZone.getTimeZone(timeZoneId)

        } else {
            TimeZone.getDefault()
        }
    }

    private fun getCurrentLocationAndLoadWeather() {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        updateLocationTextView(location.latitude, location.longitude)

                        loadWeatherInfo(location.latitude, location.longitude)

                        txt_next_seven_days.setOnClickListener(){

                            val intent = Intent(this, ForecastActivity::class.java)
                            intent.putExtra(EXTRA_LATITUDE, location.latitude)
                            intent.putExtra(EXTRA_LONGITUDE, location.longitude)
                            startActivity(intent)
                            startActivity(Intent(this, ForecastActivity::class.java))
                        }
                        iconLocation.setOnClickListener {
                            val intent = Intent(this, ManageLocationActivity::class.java)
                            startActivity(intent)
                        }

                    } else {
                        dismissProgressDialog()
                        showErrorToast("Location not available")
                    }
                }
                .addOnFailureListener { e ->
                    dismissProgressDialog()
                    Log.e("Location", "Error getting location", e)
                    showErrorToast("Error getting location")
                }
        } catch (e: SecurityException) {
            dismissProgressDialog()
            Log.e("Location", "Security exception: ${e.message}")
            showErrorToast("Location permission denied")
        }
    }

    private fun updateLocationTextView(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this)
        val addresses: List<Address>? = try {
            geocoder.getFromLocation(latitude, longitude, 1)
        } catch (e: IOException) {
            null
        }

        val cityName = addresses?.firstOrNull()?.locality

        if (cityName != null) {
            txtLocation.text = "$cityName"
        } else {
            txtLocation.text = "My Weather - Lat: $latitude, Lon: $longitude"
        }
    }

    private fun loadWeatherInfo(latitude: Double, longitude: Double) {
        val url =
            "https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&units=metric&appid=$apiKey"
        showProgressDialog("Loading")

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { data ->
                try {
                    val description =
                        data.getJSONArray("weather").getJSONObject(0).getString("description")
                    txtWeather.text = description

                    val temp = data.getJSONObject("main").getString("temp")
                    val humidity = data.getJSONObject("main").getString("humidity")
                    val windSpeed = data.getJSONObject("wind").getString("speed")


                    txtTemp.text = "$temp °C"


                    txtHumidity.text = "$humidity%"
                    txtWind.text = "$windSpeed km/h"


                    val uvIndex = getUVIndex(0.0)
                    txtUV.text = "$uvIndex"


                    val sdfDate = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
                    val sdfTime = SimpleDateFormat("h:mm a", Locale.getDefault())

                    val currentDate = sdfDate.format(Date())
                    val currentTime = sdfTime.format(Date())

                    txtDate.text = currentDate
                    txtTime.text = currentTime


                    val iconCode = data.getJSONArray("weather").getJSONObject(0)
                        .getString("icon")
                    val imgIconUrl =
                        "https://openweathermap.org/img/w/$iconCode.png"

                    Picasso.get().load(imgIconUrl).into(imgWeather, object : Callback {
                        override fun onSuccess() {
                            dismissProgressDialog()
                        }

                        override fun onError(e: Exception?) {
                            Log.e("Picasso", "Error loading image: ${e?.message}")
                            dismissProgressDialog()
                        }
                    })
                } catch (e: Exception) {
                    Log.e("Error", e.toString())
                    dismissProgressDialog()
                }
            },
            { error ->
                Log.e("Response", error.toString())
                // Handle error here
                dismissProgressDialog()
                showErrorToast("Error fetching weather data")
            })

        Volley.newRequestQueue(this).add(request)
    }


    private fun getUVIndex(uvValue: Double): String {
        return if (uvValue <= 2.9) {
            "Low"
        } else {
            "High"
        }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }

    }

    private fun showProgressDialog(message: String) {
        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage(message)
        progressDialog?.setCancelable(false)
        progressDialog?.show()
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }

    private fun showErrorToast(message: String) {
        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
    }
}
