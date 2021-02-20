package com.example.instagramkotlin

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        res_to_login_btn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        register_btn.setOnClickListener {
            createAccount()
        }
    }

    private fun createAccount() {
        val fullName = fullName.text.toString()
        val profession = profession.text.toString()
        val email = email.text.toString()
        val password = password.text.toString()

        when{
            TextUtils.isEmpty(fullName)-> Toast.makeText(this, "Name should not be empty", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(profession) -> Toast.makeText(this, "Profession field should not be empty", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(email) -> Toast.makeText(this, "Email should not be empty", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this, "Password should not be empty", Toast.LENGTH_SHORT).show()

            else ->{
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Create Account")
                progressDialog.setMessage("Please Wait, this will take some time...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth : FirebaseAuth = FirebaseAuth.getInstance()
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {task ->
                    if(task.isSuccessful){
                        saveRegisterInfo(fullName, profession, email, password, progressDialog)
                    }
                    else{
                        val errorMsg = task.exception!!.toString()
                        Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                        mAuth.signOut()
                        progressDialog.dismiss()
                    }
                }

            }

        }

    }

    private fun saveRegisterInfo(fullName: String, profession: String, email: String, password: String, progressDialog: ProgressDialog) {
        val currentUID = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUID
        userMap["fullName"] = fullName
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/kotlininstagram-6c53f.appspot.com/o/images%2F1_nh5bpXALyB9N8WljOYj_dg.jpeg?alt=media&token=fad2bcb3-f381-4304-b247-f4adcb1e18b6"
        userMap["profession"] = profession
        userMap["email"] = email
        userMap["password"] = password
        userMap["aboutUrSelf"] = "Your Bio. Not set Yet"

        userRef.child(currentUID).setValue(userMap).addOnCompleteListener { task ->
            if(task.isSuccessful){
                progressDialog.dismiss()
                Toast.makeText(this, "You have register created successfully", Toast.LENGTH_LONG).show()
                val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
            }
            else{
                val errorMessage = task.exception.toString()
                Toast.makeText(this, "Register Error: $errorMessage", Toast.LENGTH_LONG).show()
                FirebaseAuth.getInstance().signOut()
                progressDialog.dismiss()
            }
        }
    }

}