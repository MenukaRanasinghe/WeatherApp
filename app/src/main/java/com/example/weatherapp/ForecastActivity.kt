package com.example.weatherapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.ForecastActivity.Companion.EXTRA_LATITUDE
import com.example.weatherapp.ForecastActivity.Companion.EXTRA_LONGITUDE
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

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
    private lateinit var recycle_forecast : RecyclerView
    private var forecastList = JSONArray()
    private lateinit var forecastAdapter : ForecastAdapter
    lateinit var iconBack : ImageView

    companion object {
        const val EXTRA_LATITUDE = "extra_latitude"
        const val EXTRA_LONGITUDE = "extra_longitude"
    }

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

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
        recycle_forecast = findViewById(R.id.recycle_forecast)
        iconBack = findViewById(R.id.iconBack)

        if (intent.hasExtra(EXTRA_LATITUDE) && intent.hasExtra(EXTRA_LONGITUDE)) {
            latitude = intent.getDoubleExtra(EXTRA_LATITUDE, 0.0)
            longitude = intent.getDoubleExtra(EXTRA_LONGITUDE, 0.0)

            loadForecastData(latitude, longitude)

        }

    }

    private fun loadForecastData(latitude: Double, longitude: Double) {
        val forecastUrl = "https://api.openweathermap.org/data/2.5/forecast?lat=$latitude&lon=$longitude&units=metric&appid=530cf267b432cdcebf78ef5f761fb94d"

        val request = JsonObjectRequest(
            com.android.volley.Request.Method.GET, forecastUrl, null,
            Response.Listener { response ->
                Log.e("Response", response.toString())

                try {
                    forecastList = response.getJSONArray("list")
                    forecastAdapter?.notifyDataSetChanged()


                    val firstForecast = forecastList.getJSONObject(0)

                    val main = firstForecast.getJSONObject("main")

                    val temp = main.getString("temp")
                    val humidity = main.getString("humidity")

                    val weatherArray = firstForecast.getJSONArray("weather")

                    val weather = weatherArray.getJSONObject(0)

                    val description = weather.getString("description")

                    val wind = firstForecast.getJSONObject("wind")

                    val windSpeed = wind.getDouble("speed")

                    val rainObject = firstForecast.optJSONObject("rain")

                    if (rainObject != null && rainObject.has("3h")) {
                        val rainfallLast3Hours = rainObject.getDouble("3h")
                        val formattedRainfall = String.format("%.2f mm", rainfallLast3Hours)

                        txt_tommorow_rain.text = formattedRainfall
                    } else {
                        txt_tommorow_rain.text = "Unavailable"
                    }
//                    forecastAdapter.notifyDataSetChanged()
//                    forecastArray = forecastList



                    Log.e("data", "Temperature: $temp, Humidity: $humidity, Description: $description")
                    txt_tommorow_temp.text = "$temp °C"
                    txt_tommorow_des.text = description
                    txt_tommorow_humidity.text = "$humidity %"
                    txt_tommorow_wind.text = (windSpeed.toDouble()* 3.6).roundToInt().toString()+"km/h"

                }catch (e: Exception){
                    Log.e("Error", e.toString())
                }
            },
            Response.ErrorListener { error ->
                // Handle errors
                Toast.makeText(this, "Error fetching forecast data: $error", Toast.LENGTH_SHORT).show()
            }
        )
        Volley.newRequestQueue(this).add(request)

        setupRecyclerView()

        iconBack.setOnClickListener {
            val intent = Intent(this, MyLocationActivity::class.java)
            startActivity(intent)
        }

    }


    private fun setupRecyclerView() {
        // Create and set up the adapter
        forecastAdapter = ForecastAdapter()
        recycle_forecast.layoutManager = LinearLayoutManager(this)
        recycle_forecast.adapter = forecastAdapter

    }
    private inner class ForecastAdapter : RecyclerView.Adapter<ForecastDataHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastDataHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_forecast_row, parent, false)
            return ForecastDataHolder(view)
        }

        override fun onBindViewHolder(holder: ForecastDataHolder, position: Int) {
            val forecast = forecastList.getJSONObject(position)
            holder.bind(forecast)
        }

        override fun getItemCount(): Int {
            return forecastList.length()
        }
    }

    inner class ForecastDataHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        private val txt_date: TextView = itemView.findViewById(R.id.txt_date)
        private val txt_time: TextView = itemView.findViewById(R.id.txt_time)
        private val img_weather: ImageView = itemView.findViewById(R.id.img_weather)
        private val txt_status: TextView = itemView.findViewById(R.id.txt_status)
        private val txt_min_tem: TextView = itemView.findViewById(R.id.txt_min_tem)
        private val txt_max_temp: TextView = itemView.findViewById(R.id.txt_max_temp)


        fun bind(forecast: JSONObject) {

            val date = dateFormat.parse(forecast.getString("dt_txt"))
            val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
            val formattedTime = timeFormat.format(date)

            Log.d("ForecastDataHolder", "Formatted Time: $formattedTime")


            txt_date.text = formattedDate
            txt_time.text = formattedTime
            txt_status.text = forecast.getJSONArray("weather").getJSONObject(0).getString("description")
            txt_min_tem.text = forecast.getJSONObject("main").getString("temp_min")+"°C"
            txt_max_temp.text = forecast.getJSONObject("main").getString("temp_max")+"°C"

//            Picasso.get().load("https://openweathermap.org/img/w/"+forecast.getString("weather")).into(img_weather)
            Picasso.get().load("https://openweathermap.org/img/w/" + forecast.getJSONArray("weather").getJSONObject(0).getString("icon") + ".png").into(img_weather)

        }
    }
}