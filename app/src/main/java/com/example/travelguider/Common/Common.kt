package com.example.travelguider.Common

import com.example.travelguider.Remote.IGoogleAPIService
import com.example.travelguider.Remote.RetrofitClient
import com.example.travelguider.Remote.RetrofitScalarsClient
import com.example.travelguider.models.Results

object Common {
    private val GOOGLE_API_URL="https://maps.googleapis.com/"

    var currentResult:Results?=null

    val googleApiService:IGoogleAPIService
    get()=RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService::class.java)

    val googleApiServiceScalars:IGoogleAPIService
        get()=RetrofitScalarsClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService::class.java)
}