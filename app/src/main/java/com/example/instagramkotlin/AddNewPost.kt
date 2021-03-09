package com.example.instagramkotlin

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_add_new_post.*

class AddNewPost : AppCompatActivity() {
    private var firebaseUser: FirebaseUser? = null
    private var StorageRef : StorageReference? = null
    var myUri = ""
    var imageUri : Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_post)

        StorageRef = FirebaseStorage.getInstance().reference.child("Post Images")
        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        post_image.setOnClickListener {
            CropImage.activity()
                .setAspectRatio(2, 1)
                .start(this);
        }
        add_post.setOnClickListener {
            savePostData()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
           var result =  CropImage.getActivityResult(data)
            imageUri = result.uri

            post_image.setImageURI(imageUri)
        }
    }

    private fun savePostData (){
        when{
            TextUtils.isEmpty(post_description.toString()) -> Toast.makeText(this, "Post description should not be empty", Toast.LENGTH_LONG).show()
            imageUri.toString() == "" -> {
                Toast.makeText(this, "Post image should not be empty", Toast.LENGTH_LONG).show()
            }
            else -> {
                var progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Add New Post")
                progressDialog.setMessage("Please wait, adding a new post")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                var imageRef = StorageRef!!.child(System.currentTimeMillis().toString()+".jpg")
                var uploadTask: StorageTask<*>
                uploadTask = imageRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                    if (!task.isSuccessful){
                        task.exception?.let{
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation imageRef.downloadUrl
                } ).addOnCompleteListener(OnCompleteListener { task ->
                    if(task.isSuccessful){
                        var downloadurl = task.result
                        myUri = downloadurl.toString()

                        var postRef = FirebaseDatabase.getInstance().reference.child("Posts")
                        var postId = postRef.push().key

                        var postMap = HashMap<String, Any>()
                        postMap["postId"] = postId!!
                        postMap["postDescription"] = post_description.text.toString()
                        postMap["postPublisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                        postMap["postImage"] = myUri

                        postRef.child(postId).updateChildren(postMap)
                        startActivity(Intent(this, MainActivity::class.java))

                        progressDialog.dismiss()
                      }else{
                        progressDialog.dismiss()
                    }
                })
            }
        }
    }
}