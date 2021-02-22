package com.example.instagramkotlin

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.Toast
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

        login_btn.setOnClickListener {
            userLoginFun()
        }
    }

    private fun userLoginFun() {
        val email = email.text.toString()
        val password = password.text.toString()

        when{
            TextUtils.isEmpty(email) -> Toast.makeText(this, "UserName should not be empty", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this, "Password should not be empty", Toast.LENGTH_LONG).show()

            else -> {
                val progressD = ProgressDialog(this)
                progressD.setTitle("Login Progress")
                progressD.setMessage("Please wait, may we will take some seconds")
                progressD.setCanceledOnTouchOutside(false)
                progressD.show()

                val mAuth = FirebaseAuth.getInstance()
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        progressD.dismiss()
                        val intent = Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        finish()
                        startActivity(intent)
                    }else{
                        val errMessage = task.exception.toString()
                        Toast.makeText(this, "Login Eorror: $errMessage", Toast.LENGTH_LONG).show()
                        mAuth.signOut()
                        progressD.dismiss()
                    }
                }
            }
        }
    }


}
















