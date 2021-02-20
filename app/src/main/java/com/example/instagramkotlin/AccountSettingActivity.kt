package com.example.instagramkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_account_setting.*

class AccountSettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_setting)

        accnt_set_logoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this, SignInActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                finish()
            startActivity(intent)
        }
    }
}