package com.cis436.flagiq

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class MainViewModel(application: Application) : AndroidViewModel(application)
{
    data class Country(
        val name: String,
        val flagUrl: String
    )

    private val _countries = MutableLiveData<List<Country>>()

    init{
        fetchCountryData()
    }
    private fun fetchCountryData() {
        // Replace the API key with your own
        val countryUrl = "https://restcountries.com/v3.1/all"

        // Create a request queue using the application context
        val queue = Volley.newRequestQueue(getApplication<Application>().applicationContext)

        // Request a string response from the provided URL
        val stringRequest = StringRequest(
            Request.Method.GET, countryUrl,
            { response ->
                // Parse the JSON response
                val countriesList = parseCountries(response)
                // Update the LiveData with the parsed cat breeds
                _countries.value = countriesList
            },
            {
                Log.e("MainViewModel", "Error occurred while fetching cat breeds")
            })

        // Add the request to the RequestQueue
        queue.add(stringRequest)
    }
    private fun parseCountries(response: String): List<Country> {
        val countriesList = mutableListOf<Country>()
        try {
            val jsonArray = JSONArray(response)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val name = jsonObject.getJSONObject("name").getString("common")
                val imageObject = jsonObject.optJSONObject("flags")
                val flagUrl = imageObject?.getString("png") ?: ""
                Log.d("MainViewModel", "Name: $name URL: $flagUrl")
                countriesList.add(Country(name, flagUrl))
            }
        } catch (e: Exception) {
            Log.e("MainViewModel", "Error parsing JSON response: ${e.message}")
        }
        Log.d("MainViewModel", "Size: ${countriesList.size}")
        return countriesList
    }




}