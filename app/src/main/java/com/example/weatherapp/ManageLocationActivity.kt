package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class ManageLocationActivity : AppCompatActivity() {
    lateinit var txtlocation: TextView
    lateinit var txtdate: TextView
    lateinit var txttime: TextView
    lateinit var txtcity: TextView
    lateinit var txttemperature: TextView
    lateinit var img: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_location)
        val apiKey = "https://openweathermap.org/api"
        val city = "YourCity"

        loadWeatherInfo(apiKey,city )
    }
    fun loadWeatherInfo(city: String ,apiKey: String){
        val url=" https://openweathermap.org/api"
        val request= JsonObjectRequest(
            Request.Method.GET,url,null,
            Response.Listener { data ->
                Log.e("Response",data.toString())
                try {
                    txtlocation.text="Location \t" +data
                        .getJSONArray("weather")
                        .getJSONObject(0)
                        .getString("location")

                    txtdate.text="Date \t"+data
                        .getJSONObject("main")
                        .getString("date")

                    txttime.text="Time \t"+data
                        .getJSONObject("main")
                        .getString("time")

                    txtcity.text="City \t"+data
                        .getJSONObject("main")
                        .getString("city")

                    txttemperature.text="Temp \t"+data
                        .getJSONObject("main")
                        .getString("temp")

                }catch (e:Exception)
                {
                }
                Log.e("Response",data.toString())
            },
            Response.ErrorListener { error ->
                Log.e("Response",error.toString())
            })
        Volley.newRequestQueue(this).add(request)
    }

}