package com.example.travelguider.activities

import Helper.DirectionJSONParser
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Path
import android.location.Location
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.travelguider.Common.Common
import com.example.travelguider.R
import com.example.travelguider.Remote.IGoogleAPIService

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.travelguider.databinding.ActivityViewDirectionBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.*
import dmax.dialog.SpotsDialog
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewDirection : AppCompatActivity(), OnMapReadyCallback {

     lateinit var mMap:GoogleMap
    private lateinit var binding: ActivityViewDirectionBinding

    lateinit var mService:IGoogleAPIService


    lateinit var mCurrentMarker:Marker

     var polyLine: Polyline?=null

    companion object{
        private const val MY_PERMISSION_CODE:Int=1000
    }

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: com.google.android.gms.location.LocationRequest
    lateinit var locationCallback: LocationCallback
    lateinit var mLastLocation: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityViewDirectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //Init Service
        mService= Common.googleApiServiceScalars

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            if(checkLocationPermission()){
                builLocationRequest()
                buildLocationCallBack();
                fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this)
                fusedLocationProviderClient.requestLocationUpdates(com.google.android.gms.location.LocationRequest(),locationCallback, Looper.myLooper()!!)
            }

        }

        else{
            builLocationRequest()
            buildLocationCallBack();
            fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this)
            fusedLocationProviderClient.requestLocationUpdates(com.google.android.gms.location.LocationRequest(),locationCallback, Looper.myLooper()!!)
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
       mMap!!.uiSettings.isZoomControlsEnabled=true

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location->
            mLastLocation=location

            //Add your location to Map
            val markerOptions=MarkerOptions()
                .position(LatLng(mLastLocation.latitude,mLastLocation.longitude))
                .title("Your Position")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))


            mCurrentMarker= mMap!!.addMarker(markerOptions)!!

            //Move Camera
            mMap!!.moveCamera(CameraUpdateFactory.newLatLng(LatLng(mLastLocation.latitude,mLastLocation.longitude)))
            mMap!!.animateCamera(CameraUpdateFactory.zoomTo(12.0f))


            //Create marker for destination location

            val destinationLatLng=LatLng(Common.currentResult!!.geometry?.location?.lat!!.toDouble(),
                Common.currentResult!!.geometry?.location?.lng!!.toDouble())

            mMap!!.addMarker(MarkerOptions().position(destinationLatLng)
                .title(Common.currentResult!!.name)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)))

            //Get Direction
            drawPath(mLastLocation,Common.currentResult!!.geometry!!.location!!)


        }
    }


    private fun checkLocationPermission():Boolean {
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.ACCESS_FINE_LOCATION))
                ActivityCompat.requestPermissions(this, arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ), ViewDirection.MY_PERMISSION_CODE
                )
            else
                ActivityCompat.requestPermissions(this, arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ), ViewDirection.MY_PERMISSION_CODE
                )
            return false
        }
        else
            return true
    }

    private fun builLocationRequest()  {
        locationRequest=com.google.android.gms.location.LocationRequest()
        locationRequest.priority=com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval=5000
        locationRequest.fastestInterval=3000
        locationRequest.smallestDisplacement=10f

    }

    private fun buildLocationCallBack() {
        locationCallback=object :LocationCallback(){
            override fun onLocationResult(p0: LocationResult) {

               mLastLocation=p0!!.lastLocation

                //Add your location to Map+
                val markerOptions=MarkerOptions()
                    .position(LatLng(mLastLocation.latitude,mLastLocation.longitude))
                    .title("Your Position")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))


                mCurrentMarker= mMap!!.addMarker(markerOptions)!!

                //Move Camera
                mMap!!.moveCamera(CameraUpdateFactory.newLatLng(LatLng(mLastLocation.latitude,mLastLocation.longitude)))
                mMap!!.animateCamera(CameraUpdateFactory.zoomTo(12.0f))


//                Create marker for destination location

                val destinationLatLng=LatLng(Common.currentResult!!.geometry?.location?.lat!!.toDouble(),
                    Common.currentResult!!.geometry?.location?.lng!!.toDouble())

                mMap!!.addMarker(MarkerOptions().position(destinationLatLng)
                    .title(Common.currentResult!!.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)))
                
                //Get Direction 
                drawPath(mLastLocation,Common.currentResult!!.geometry!!.location!!)
                
                

            }
        }
    }

    private fun drawPath(mLastLocation: Location, location:com.example.travelguider.models.Location) {

        if(polyLine!=null)
            polyLine!!.remove() //Remove old direction

        val origin:String=StringBuilder(mLastLocation.latitude.toString())
            .append(",")
            .append(mLastLocation.longitude.toString())
            .toString()

        val destination:String=StringBuilder(location.lat.toString())
            .append(",")
            .append(location.lng.toString())
            .toString()

        mService.getDirections(origin,destination)
            .enqueue(object:Callback<String>{
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    ParserTask().execute(response.body().toString())
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d("Direction","Error")
                }

            })

    }


    override fun onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.onStop()
    }

    //Override OnRequestPermissionResult

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            ViewDirection.MY_PERMISSION_CODE ->{
                if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
                        if (checkLocationPermission()){
                            builLocationRequest()
                            buildLocationCallBack();
                            fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this)
                            fusedLocationProviderClient.requestLocationUpdates(com.google.android.gms.location.LocationRequest(),locationCallback, Looper.myLooper()!!)
                            mMap!!.isMyLocationEnabled=true
                        }

                }else
                {
                    Toast.makeText(this,"Permission Denied ", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

   inner class ParserTask:AsyncTask<String,Int,List<List<HashMap<String,String>>>>() {
        internal  val waitingDialog: SpotsDialog =SpotsDialog(this@ViewDirection)

        override fun onPreExecute() {
            super.onPreExecute()
            waitingDialog.show()
            waitingDialog.setMessage("Please wait.....")
        }

        override fun onPostExecute(result: List<List<HashMap<String, String>>>) {
            super.onPostExecute(result)

            var points:ArrayList<LatLng>?=null
            var polylineOptions:PolylineOptions?=null

            for(i in result!!.indices){
                points= ArrayList()
                polylineOptions=PolylineOptions()

                val path=result[i]
                for(j in path.indices){
                    val point=path[j]
                    val lat=point["lat"]!!.toDouble()
                    val lng=point["lng"]!!.toDouble()
                    val position=LatLng(lat,lng)

                    points.add(position)
                }

                polylineOptions.addAll(points)
                polylineOptions.width(12f)
                polylineOptions.color(Color.RED)
                polylineOptions.geodesic(true)
            }
            polyLine= polylineOptions?.let { mMap!!.addPolyline(it) }
            waitingDialog.dismiss()
        }

        override fun doInBackground(vararg params: String?): List<List<HashMap<String, String>>>?{
            val jsonObjects:JSONObject
            var routes:List<List<HashMap<String,String>>>?=null
            try {
                jsonObjects= JSONObject(params[0])
                val parser= DirectionJSONParser()
                routes=parser.parse(jsonObjects)
            }
            catch (e:JSONException){
                e.printStackTrace()
            }
            return routes!!
        }

    }

}

