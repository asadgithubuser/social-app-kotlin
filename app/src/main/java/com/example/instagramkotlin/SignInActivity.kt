package com.example.instagramkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*


class SignInActivity : AppCompatActivity() {
    override fun onStart() {
        super.onStart()

        if(FirebaseAuth.getInstance().currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        login_to_res_btn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }


}