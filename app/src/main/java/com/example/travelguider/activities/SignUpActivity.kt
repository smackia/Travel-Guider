package com.example.travelguider.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.travelguider.R
import com.example.travelguider.firebase.FirestoreClass
import com.example.travelguider.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_gets_started.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        setupActionBar()
    }

    private  fun setupActionBar(){
        setSupportActionBar(toolbar_sign_up_activity)

        val actionBar=supportActionBar

        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_black_24dp)
        }

        toolbar_sign_up_activity.setNavigationOnClickListener { onBackPressed() }

        signUp_btn.setOnClickListener {
            registerUser()
        }
    }


    fun userRegisteredSuccess(){
        Toast.makeText(this,"You have successfully registered",Toast.LENGTH_LONG).show()
        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    private fun registerUser(){
        val name:String=name.text.toString().trim{it<=' '}
        val email:String=email.text.toString().trim{it<=' '}
        val password:String=password.text.toString().trim{it<=' '}


        if(validateForm(name,email,password)){
           showProgressDialog()
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser = task.result!!.user!!
                    val registeredEmail = firebaseUser.email!!
                    val user= User(firebaseUser.uid,name,registeredEmail)
                    FirestoreClass().registerUser(this,user)


                } else {
                    Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateForm(name:String,email:String,password:String):Boolean{
        return when{
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("Please enter a name")
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar("Please enter a password")
                false
            }
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please enter a email")
                false
            }
            else->{
                true
            }
        }
    }
}