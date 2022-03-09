package com.example.travelguider.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.example.travelguider.R
import com.example.travelguider.firebase.FirestoreClass
import com.example.travelguider.models.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.main_content.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : BaseActivity(),View.OnClickListener {


    companion object{
        const val  MY_PROFILE_REQUEST_CODE:Int=11
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupActionBar()
//
//        nav_view.setNavigationItemSelectedListener(this)
//
//        FirestoreClass().loadUserData(this)
        location_btn.setOnClickListener {
            startActivity(Intent(this,MapsActivity::class.java))
        }
        delhi.setOnClickListener(this)
        mumbai.setOnClickListener(this)
        hyderabad.setOnClickListener(this)
        Chennai.setOnClickListener(this)
        kolkata.setOnClickListener(this)
        lucknow.setOnClickListener(this)
    }


override fun onClick(v: View?) {
when(v?.id){
    R.id.delhi->{
        val intent=Intent(this@MainActivity,MapsActivity::class.java)
        intent.putExtra("LATITUDE",28.6330)
        intent.putExtra("LONGITUDE",77.2194)
        startActivity(intent)
        Log.d("Delhi","Delhi is clicked")
    }
    R.id.mumbai->{
        val intent=Intent(this@MainActivity,MapsActivity::class.java)
        intent.putExtra("LATITUDE",19.0760)
        intent.putExtra("LONGITUDE",72.8777)
        startActivity(intent)

    }
    R.id.hyderabad->{
        val intent=Intent(this@MainActivity,MapsActivity::class.java)
        intent.putExtra("LATITUDE",17.3850)
        intent.putExtra("LONGITUDE",78.4867)
        startActivity(intent)
    }
    R.id.Chennai->{
        val intent=Intent(this@MainActivity,MapsActivity::class.java)
        intent.putExtra("LATITUDE",13.0827)
        intent.putExtra("LONGITUDE",80.2707)
        startActivity(intent)
    }

    R.id.kolkata->{
        val intent=Intent(this@MainActivity,MapsActivity::class.java)
        intent.putExtra("LATITUDE",22.5726)
        intent.putExtra("LONGITUDE",88.3639)
        startActivity(intent)
    }

    R.id.lucknow->{
        val intent=Intent(this@MainActivity,MapsActivity::class.java)
        intent.putExtra("LATITUDE",26.8467)
        intent.putExtra("LONGITUDE",80.9462)
        startActivity(intent)
    }
}
}

    private fun setupActionBar(){
        setSupportActionBar(toolbar_main_activity)
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        toolbar_main_activity.setNavigationOnClickListener {
            // Toggle drawer
            toggleDrawer()
        }
    }
//
    private fun toggleDrawer(){
        if(drawer_layout.isDrawerOpen(GravityCompat.START )){
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        else{
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }
//
//    override fun onBackPressed() {
//        if(drawer_layout.isDrawerOpen(GravityCompat.START )){
//            drawer_layout.closeDrawer(GravityCompat.START)
//        }
//        else{
//            doubleBackToExit()
//        }
//    }
//
//    fun updateNavigationUserDetails(user:User){
//
//        Glide
//            .with(this)
//            .load(user.image)
//            .centerCrop()
//            .placeholder(R.drawable.ic_user_place_holder)
//            .into(nav_user_image)
//
//        tv_username.text=user.name
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if(resultCode==Activity.RESULT_OK && requestCode== MY_PROFILE_REQUEST_CODE){
//            FirestoreClass().loadUserData(this)
//        }
//        else{
//            Log.e("Cancelled","Cancel")
//        }
//    }
//
//    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        when(item.itemId){
//            R.id.nav_my_profile ->{
//                startActivityForResult(Intent(this,MyProfileActivity::class.java),
//                    MY_PROFILE_REQUEST_CODE)
//            }
//            R.id.nav_sign_out ->{
//                FirebaseAuth.getInstance().signOut()
//
//                val intent=Intent(this,GetsStartedActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(intent)
//                finish()
//            }
//
//        }
//        drawer_layout.closeDrawer(GravityCompat.START)
//        return true
//    }
}