package com.example.travelguider.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.example.travelguider.R
import kotlinx.android.synthetic.main.activity_gets_started.*

class GetsStartedActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gets_started)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        signup_button.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        signin_button.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }
}