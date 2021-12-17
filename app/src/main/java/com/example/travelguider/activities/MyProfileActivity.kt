package com.example.travelguider.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.travelguider.R
import com.example.travelguider.firebase.FirestoreClass
import com.example.travelguider.models.User
import com.example.travelguider.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.io.IOException
import java.net.URI

class MyProfileActivity : BaseActivity() {


    companion object{
        private  const val READ_STORAGE_PERMISSION_CODE=1
        private  const val PICK_IMAGE_REQUEST_COD=2
    }

    private var mSelectedImageFileUri: Uri?=null
    private  var mProfileImageURL:String=""
    private lateinit var  mUserDetails:User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        setupActionBar()

        FirestoreClass().loadUserData(this)

        iv_profile_user_image.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
                showImageChooser()
            }
            else{

                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_CODE)
            }
        }

        update_button.setOnClickListener {
            if(mSelectedImageFileUri!=null){
                uploadUserImage()
            }
            else{
                showProgressDialog()
                updateUserProfileData()
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                showImageChooser()
            }else{
                Toast.makeText(this,"Oops, permission denied",Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun showImageChooser(){
        var galleryIntent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_COD)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK && requestCode== PICK_IMAGE_REQUEST_COD && data!!.data!=null){
            mSelectedImageFileUri=data.data

            try {
                Glide
                    .with(this@MyProfileActivity)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(iv_profile_user_image)
            }
            catch (e:IOException){
                e.printStackTrace()
            }
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_my_profile_activity)
        val actionBar=supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24)
            actionBar.title="My Profile"
        }
        toolbar_my_profile_activity.setNavigationOnClickListener { onBackPressed() }
    }


    fun setUserDataInUI(user: User){

        mUserDetails=user




        Glide
            .with(this@MyProfileActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(iv_profile_user_image)

            et_name.setText(user.name)
            et_email.setText(user.email)
            if(user.mobile!=0L){
                et_mobile.setText(user.mobile.toString())
            }
    }

    fun updateUserProfileData(){
        var userHashMap=HashMap<String,Any>()

        var anyChangesMade=false

        if(mProfileImageURL.isNotEmpty() && mProfileImageURL!=mUserDetails.image){
            userHashMap[Constants.IMAGE]=mProfileImageURL
            anyChangesMade=true
        }

        if(et_name.text.toString()!=mUserDetails.name){
            userHashMap[Constants.NAME]=et_name.text.toString()
            anyChangesMade=true
        }


        if(et_mobile.text.toString()!=mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE]=et_mobile.text.toString().toLong()
            anyChangesMade=true
        }
        if(anyChangesMade)
        FirestoreClass().updateUserProfileData(this,userHashMap)
    }

    private fun uploadUserImage(){
        showProgressDialog()
        if(mSelectedImageFileUri!=null){
            val sRef:StorageReference=FirebaseStorage.getInstance().reference.child("USER_IMAGE"+System.currentTimeMillis()
            +"."+getFileExtension(mSelectedImageFileUri))

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapshot ->
                Log.i("Firebase Image URL",taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri->
                    Log.i("Downloadable Image URL",uri.toString())
                    mProfileImageURL=uri.toString()
                    updateUserProfileData()
                }
            }.addOnFailureListener{
                exception-> Toast.makeText(this,exception.message,Toast.LENGTH_LONG).show()

                hideProgressDialog()
            }


        }
    }

    private fun getFileExtension(uri:Uri?):String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

fun profileUpdateSuccess(){
    hideProgressDialog()

    setResult(Activity.RESULT_OK)
    finish()
}


}