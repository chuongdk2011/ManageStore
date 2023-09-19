package com.example.myapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var tv = findViewById(R.id.tv) as TextView

        tv.setOnClickListener {
            LoginManager.getInstance().logOut()
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "logout", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@MainActivity,SignInActivity::class.java) )
        }
    }
}