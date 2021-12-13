package com.example.travelguider.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.AnimationUtils
import com.example.travelguider.R
import com.example.travelguider.firebase.FirestoreClass
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        text_animate.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slidedown))
        text_animate1.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slideup))

        Handler().postDelayed({

            var currentUserID=FirestoreClass().getCurrentUserId()

            if(currentUserID.isNotEmpty()){
                startActivity(Intent(this, MainActivity::class.java))
            }
            else{
                startActivity(Intent(this, GetsStartedActivity::class.java))
            }

            finish()
        },2500)

    }
}