package com.example.weatherapp
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class ManageLocationActivity : AppCompatActivity() {

    private lateinit var txtCity: TextView
    private lateinit var txtDate: TextView
    private lateinit var txtTime: TextView
    private lateinit var txtTemp: TextView
    private lateinit var txtSearch: EditText
    private lateinit var iconBack: ImageView

    private val apiKey = "a863eb50a11a18b6b9b2d9badc169c24"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_location)

        txtCity = findViewById(R.id.txtCity)
        txtDate = findViewById(R.id.txtDate)
        txtTime = findViewById(R.id.txtTime)
        txtTemp = findViewById(R.id.txtTemp)
        txtSearch = findViewById(R.id.txtSearch)
        iconBack = findViewById(R.id.iconBack)

        txtSearch.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {

                loadWeatherInfo(txtSearch.text.toString())
                return@setOnEditorActionListener true
            }
            false
        }
        iconBack.setOnClickListener {
            val intent = Intent(this, MyLocationActivity::class.java)
            startActivity(intent)
        }

    }

    private fun loadWeatherInfo(cityName: String) {
        val url =
            "https://api.openweathermap.org/data/2.5/weather?q=$cityName&units=metric&appid=$apiKey"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                parseWeatherDetails(response)
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
            })


        Volley.newRequestQueue(this).add(request)
    }

    private fun parseWeatherDetails(response: JSONObject) {
        try {
            val cityName = response.getString("name")
            txtCity.text = cityName

            val main = response.getJSONObject("main")
            val temp = main.getString("temp")
            txtTemp.text = temp +"°C"

            val sdfDate = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
            val sdfTime = SimpleDateFormat("h:mm a", Locale.getDefault())
            val currentDate = sdfDate.format(Date())
            val currentTime = sdfTime.format(Date())

            txtDate.text = currentDate
            txtTime.text = currentTime
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}