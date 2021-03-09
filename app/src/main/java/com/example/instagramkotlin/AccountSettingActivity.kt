package com.example.instagramkotlin

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.ProgressBar
import android.widget.Toast
import com.example.instagramkotlin.Models.User
import com.example.instagramkotlin.fragments.ProfileFragment
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_account_setting.*
import java.util.Calendar.getInstance

class AccountSettingActivity : AppCompatActivity() {
    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""
    private var cropUri = ""
    private var imageUri : Uri? = null
    private var storageProfileImageRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_setting)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfileImageRef = FirebaseStorage.getInstance().reference.child("Profile Images")

        account_setting_logoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this, SignInActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
        }

        //====== account update info
        userInfo()
        edit_profile_img_btnImg.setOnClickListener {
            checker = "clicked"
            CropImage.activity()
                    .setAspectRatio(1,1)
                    .start(this);
        }
        updateUserInfo.setOnClickListener {
            if (checker == "clicked"){
                unpdateUserInfoWithImage()
            }else{
                updateUserData()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode ==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
            var result = CropImage.getActivityResult(data)
            imageUri = result.uri
            profile_setting_edit_profile_img.setImageURI(imageUri)
        }
    }
    private fun unpdateUserInfoWithImage(){

        when{
            TextUtils.isEmpty(edit_name.toString()) -> Toast.makeText(this, "Name should not be empty", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(edit_profession.toString()) -> Toast.makeText(this, "Profession should not be empty", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(edit_abouturself.toString()) -> Toast.makeText(this, "Please tell something about yourself", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(imageUri.toString()) -> Toast.makeText(this, "Profile image should not be empty", Toast.LENGTH_LONG).show()
            else -> {
                var progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Update Account Info")
                progressDialog.setMessage("Please wait, we are updating your profile information.")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                var imageRef = storageProfileImageRef!!.child(firebaseUser!!.uid+".jpg")
                var uploadTask : StorageTask<*>
                uploadTask = imageRef.putFile(imageUri!!)

                uploadTask.continueWithTask ( Continuation<UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                    if (!task.isSuccessful){
                        task.exception?.let {
                            throw it
                        }
                    }
                   return@Continuation imageRef.downloadUrl
                }).addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        val imgDownloadUrl = task.result
                        cropUri = imgDownloadUrl.toString()

                        var ref = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

                        val userMap = HashMap<String, Any>()
                        userMap["fullName"] = edit_name.text.toString().toLowerCase()
                        userMap["profession"] = edit_profession.text.toString()
                        userMap["aboutUrSelf"] = edit_abouturself.text.toString()
                        userMap["image"] = cropUri

                        ref.updateChildren(userMap)
                        Toast.makeText(this, "Account Information updated successfully", Toast.LENGTH_LONG).show()

                        startActivity(Intent(this, AccountSettingActivity::class.java))
                        finish()

                        progressDialog.dismiss()
                    }else{
                        progressDialog.dismiss()
                    }
                }
            }
        }
    }
    private fun updateUserData(){

        when{
            edit_name.text.toString() == "" -> {
                Toast.makeText(this, "Name should not be null", Toast.LENGTH_LONG).show()
            }
            edit_profession.text.toString() == "" -> {
                Toast.makeText(this, "Profession should not be null", Toast.LENGTH_LONG).show()
            }
            edit_abouturself.text.toString() == "" -> {
                Toast.makeText(this, "Please tell us yourself :) ", Toast.LENGTH_LONG).show()
            }
            else -> {
                var userRef = FirebaseDatabase.getInstance().getReference().child("Users")
                var userMap = HashMap<String, Any>()
                userMap["FullName"] = edit_name.text.toString().toLowerCase()
                userMap["profession"] = edit_profession.text.toString()
                userMap["aboutUrSelf"] = edit_abouturself.text.toString()

                userRef.child(firebaseUser.uid).updateChildren(userMap)
                Toast.makeText(this, "Your Account Info has been updated", Toast.LENGTH_LONG).show()

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }



    }
    private fun userInfo(){
        var userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.uid)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    var user = p0.getValue<User>(User::class.java)

                    edit_name.setText(user!!.getFullName())
                    edit_profession.setText(user!!.getProfession())
                    edit_abouturself.setText(user!!.getAboutUrSelf())

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profile_setting_edit_profile_img)
                }
            }

        })
    }
}