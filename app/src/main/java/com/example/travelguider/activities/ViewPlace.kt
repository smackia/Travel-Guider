package com.example.travelguider.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.example.travelguider.Common.Common
import com.example.travelguider.R
import com.example.travelguider.Remote.IGoogleAPIService
import com.example.travelguider.models.MyPlaces
import com.example.travelguider.models.PlaceDetail
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_view_place.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewPlace : AppCompatActivity() {

    internal lateinit var mService: IGoogleAPIService
    var mPlace: PlaceDetail? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_place)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        //Init Service

        mService = Common.googleApiService

        //Set empty for all text view

        place_name.text = ""
        place_address.text = ""
        place_open_hour.text = ""


        btn_show_map.setOnClickListener {

            // Open Map Intent to view
            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mPlace!!.result!!.url))
            startActivity(mapIntent)

        }

        btn_view_direction.setOnClickListener {
            val viewDirection=Intent(this,ViewDirection::class.java)
            startActivity(viewDirection)
        }


        //Load photo of place

        if (Common.currentResult!!.photos != null && Common.currentResult!!.photos!!.size > 0)
            Picasso.with(this)
                .load(getPhotoOfPlace(Common.currentResult!!.photos!![0].photo_reference!!, 1000))
                .into(photo)

        //Load Rating
        if (Common.currentResult!!.rating != null)
            rating_bar.rating = Common.currentResult!!.rating.toFloat()
        else
            rating_bar.visibility = View.GONE


        //Load open hours

        if (Common.currentResult!!.opening_hours != null)
            place_open_hour.text = "Open now"
        else
            place_open_hour.text = "Closed now"


        //User Service to fetch Address and name
            mService.getDetailPlace(getPlaceDetailUrl(Common.currentResult!!.place_id!!))
                .enqueue(object :Callback<PlaceDetail>{
                    override fun onResponse(
                        call: Call<PlaceDetail>,
                        response: Response<PlaceDetail>
                    ) {
                        mPlace=response!!.body()
//                        place_address.text=mPlace!!.result!!.formatted_address
//                        place_name.text=mPlace!!.result!!.name

                    }

                    override fun onFailure(call: Call<PlaceDetail>, t: Throwable) {
                        Toast.makeText(baseContext,"Photo not found",Toast.LENGTH_SHORT).show()
                    }

                })

    }

    private fun getPlaceDetailUrl(placeId: String): String {

        val url=StringBuilder("https://maps.googleapis.com/maps/api/place/details/json")
        url.append("?placeid=$placeId")
        url.append("&key=AIzaSyBlB5Iv-Yzxec20Empz6R4K0ZSv2ZZHkkw")
        Log.d("place detail",url.toString())
        return url.toString()
    }

    private fun getPhotoOfPlace(photoReference: String, maxWidth: Int): String {

        val url=StringBuilder("https://maps.googleapis.com/maps/api/place/photo")
        url.append("?maxwidth=$maxWidth")
        url.append("&photoreference=$photoReference")
        url.append("&key=AIzaSyBlB5Iv-Yzxec20Empz6R4K0ZSv2ZZHkkw")
        return url.toString()


    }
}