package com.example.myapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp.DTO.UserDTO
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SignInActivity : AppCompatActivity() {

    private lateinit var tv_signUp: TextView
    private lateinit var tv_signIn: TextView
    private lateinit var signUpLayout: LinearLayout
    private lateinit var signInLayout: LinearLayout
    private lateinit var btn_login: Button
    private lateinit var btn_signUp: Button
    private lateinit var ed_signup_email: EditText
    private lateinit var ed_signup_pass: EditText
    private lateinit var ed_signup_repass: EditText
    private lateinit var ed_signin_email: EditText
    private lateinit var ed_signin_pass: EditText
    private lateinit var img_fb: ImageView
    private lateinit var img_gg: ImageView
    private lateinit var img_tw: ImageView
    private lateinit var tv_quenmk: TextView
    private lateinit var callbackManager: CallbackManager
    private val TAG = "chuongdk"
    private lateinit var auth: FirebaseAuth
    lateinit var dto: UserDTO

    //    LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        FacebookSdk.sdkInitialize(getApplicationContext());
        var accessToken = AccessToken.getCurrentAccessToken();

        auth = FirebaseAuth.getInstance()


        initView()

        tv_signUp.setOnClickListener {
            tv_signUp.background = resources.getDrawable(R.drawable.switch_trcks, null)
            tv_signUp.setTextColor(resources.getColor(R.color.textColor, null))
            tv_signIn.background = null
            signUpLayout.visibility = View.VISIBLE
            signInLayout.visibility = View.GONE
            tv_signIn.setTextColor(resources.getColor(R.color.redColor, null))
            ed_signin_email.setText("")
            ed_signin_pass.setText("")

        }
        tv_signIn.setOnClickListener {
            tv_signUp.background = null
            tv_signUp.setTextColor(resources.getColor(R.color.textColor, null))
            tv_signIn.background = resources.getDrawable(R.drawable.switch_trcks, null)
            signUpLayout.visibility = View.GONE
            signInLayout.visibility = View.VISIBLE
            tv_signIn.setTextColor(resources.getColor(R.color.textColor, null))
            ed_signup_email.setText("")
            ed_signup_pass.setText("")
            ed_signup_repass.setText("")

        }
        btn_login.setOnClickListener {
            var intent = Intent(this@SignInActivity, MainActivity::class.java)
            startActivity(intent)
        }
        btn_signUp.setOnClickListener {
            tv_signUp.background = null
            tv_signUp.setTextColor(resources.getColor(R.color.textColor, null))
            tv_signIn.background = resources.getDrawable(R.drawable.switch_trcks, null)
            signUpLayout.visibility = View.GONE
            signInLayout.visibility = View.VISIBLE
            tv_signIn.setTextColor(resources.getColor(R.color.textColor, null))

            ed_signup_email.setText("")
            ed_signup_pass.setText("")
            ed_signup_repass.setText("")

            Toast.makeText(this@SignInActivity, "Đăng Ký Thành Công!!", Toast.LENGTH_SHORT).show()
        }

        if (accessToken != null && !accessToken.isExpired) {
            startActivity(Intent(this@SignInActivity, MainActivity::class.java))
            finish()
        }
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onCancel() {
                    TODO("Not yet implemented")
                }

                override fun onError(error: FacebookException) {
                    TODO("Not yet implemented")
                }

                override fun onSuccess(result: LoginResult) {
                    handleFacebookAccessToken(result.accessToken)

                }

            })
        img_fb.setOnClickListener {
            
            LoginManager.getInstance()
                .logInWithReadPermissions(this, listOf("public_profile,email"))

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)


    }


    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser!!

                    val databaseReference = FirebaseDatabase.getInstance().reference.child("Users")
                    val currentFirebaseUser = FirebaseAuth.getInstance().currentUser
                    val uid = currentFirebaseUser!!.uid



                    dto = UserDTO()
                    dto.id = uid
                    dto.fullname = user.displayName!!
                    dto.email = user.email!!
                    dto.avt = user.photoUrl.toString()

                    databaseReference.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.hasChild(uid)) {
                                //Old User
                                userAlreadyExistsScore();

                            } else {
                                // User Not Yet Exists
                                newUserScore();
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                    Log.e(
                        TAG,
                        "handleFacebookAccessToken: " + user.displayName + " email= " + user.email
                    )

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure" + task.exception, task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()

                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this@SignInActivity, MainActivity::class.java)
            intent.putExtra("name", user.displayName)
            intent.putExtra("email", user.email)
            startActivity(intent)
        }
    }

    private fun newUserScore() {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        val UserId = databaseReference.child(auth.getCurrentUser()!!.uid)

        UserId
            .setValue(dto)
            .addOnCanceledListener {
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {

                Log.e(TAG, "handleFacebookAccessToken: " + it.message)
            }
        Toast.makeText(this@SignInActivity, "Sign in Successful", Toast.LENGTH_SHORT).show()
        val main2 = Intent(this@SignInActivity, MainActivity::class.java)
        startActivity(main2)
    }

    private fun userAlreadyExistsScore() {
        val user = auth.currentUser
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        val UserId = databaseReference.child(auth.currentUser!!.uid)
        if (user != null) {
            val valueEventListener: ValueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {


                    val userDTO = dataSnapshot.getValue(UserDTO::class.java)
                    Log.d(TAG, "onDataChange: "+userDTO!!.address)
                    UserId.setValue(userDTO)
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            }
            UserId.addListenerForSingleValueEvent(valueEventListener)
            Toast.makeText(this@SignInActivity, "Sign in Successful", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@SignInActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initView() {
        tv_signUp = findViewById(R.id.tv_signup)
        tv_signIn = findViewById(R.id.tv_signin)
        signUpLayout = findViewById(R.id.singUpLayout)
        signInLayout = findViewById(R.id.signInLayout)
        btn_login = findViewById(R.id.btn_login)
        btn_signUp = findViewById(R.id.btn_signUp)
        ed_signup_email = findViewById(R.id.ed_signup_email)
        ed_signup_pass = findViewById(R.id.ed_signup_pass)
        ed_signup_repass = findViewById(R.id.ed_signup_repass)
        ed_signin_email = findViewById(R.id.ed_login_email)
        ed_signin_pass = findViewById(R.id.ed_login_pass)
        img_fb = findViewById(R.id.img_facebook)
        img_gg = findViewById(R.id.img_google)
        img_tw = findViewById(R.id.img_twitter)
        tv_quenmk = findViewById(R.id.tv_quenmk)

    }
}
