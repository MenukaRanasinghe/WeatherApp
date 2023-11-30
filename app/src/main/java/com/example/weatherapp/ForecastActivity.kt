package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import org.w3c.dom.Text

class ForecastActivity : AppCompatActivity() {

    lateinit var img_tommorow_weather : ImageView
    lateinit var txt_tommorow : TextView
    lateinit var txt_tommorow_temp : TextView
    lateinit var txt_tommorow_des : TextView
    lateinit var img_tommorow_rain : ImageView
    lateinit var txt_tommorow_rain : TextView
    lateinit var txt_tommorow_rain_des : TextView
    lateinit var img_tommorow_wind : ImageView
    lateinit var txt_tommorow_wind : TextView
    lateinit var txt_tommorow_wind_des : TextView
    lateinit var img_tommorow_humidity : ImageView
    lateinit var txt_tommorow_humidity : TextView
    lateinit var txt_tommorow_humidity_des : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        img_tommorow_weather = findViewById(R.id.img_tommorow_weather)
        txt_tommorow = findViewById(R.id.txt_tommorow)
        txt_tommorow_temp = findViewById(R.id.txt_tommorow_temp)
        txt_tommorow_des = findViewById(R.id.txt_tommorow_des)
        img_tommorow_rain = findViewById(R.id.img_tommorow_rain)
        txt_tommorow_rain = findViewById(R.id.txt_tommorow_rain)
        txt_tommorow_rain_des = findViewById(R.id.txt_tommorow_rain_des)
        img_tommorow_wind = findViewById(R.id.img_tommorow_wind)
        txt_tommorow_wind = findViewById(R.id.txt_tommorow_wind)
        txt_tommorow_wind_des = findViewById(R.id.txt_tommorow_wind_des)
        img_tommorow_humidity = findViewById(R.id.img_tommorow_humidity)
        txt_tommorow_humidity = findViewById(R.id.txt_tommorow_humidity)
        txt_tommorow_humidity_des = findViewById(R.id.txt_tommorow_humidity_des)

    }
}