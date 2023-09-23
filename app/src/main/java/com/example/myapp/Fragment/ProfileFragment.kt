package com.example.myapp.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.myapp.R
import com.example.myapp.SignInActivity
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        // Inflate the layout for this fragment

        var tv_logout = view.findViewById<TextView>(R.id.tv_logout)
        tv_logout.setOnClickListener {
            LoginManager.getInstance().logOut()
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(context, "logout", Toast.LENGTH_SHORT).show()
            startActivity(Intent(context, SignInActivity::class.java) )
        }
        return  view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}