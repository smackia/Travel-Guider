package com.example.travelguider.Common

import com.example.travelguider.Remote.IGoogleAPIService
import com.example.travelguider.Remote.RetrofitClient

object Common {
    private val GOOGLE_API_URL="https://maps.googleapis.com/"

    val googleApiService:IGoogleAPIService
    get()=RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService::class.java)
}