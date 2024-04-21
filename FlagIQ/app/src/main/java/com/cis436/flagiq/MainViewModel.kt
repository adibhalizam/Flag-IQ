package com.cis436.flagiq

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class MainViewModel(application: Application) : AndroidViewModel(application)
{
    data class Country(
        val name: String,
        val flagUrl: String
    )
}