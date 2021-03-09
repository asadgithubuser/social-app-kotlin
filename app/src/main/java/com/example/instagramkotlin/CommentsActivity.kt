package com.example.instagramkotlin

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramkotlin.Adapter.CommentAdapter
import com.example.instagramkotlin.Models.Comment
import com.example.instagramkotlin.Models.Post
import com.example.instagramkotlin.Models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_comments.*

class CommentsActivity : AppCompatActivity() {
    private var postId: String? = null
    private var publisherId: String? = null
    private var firebaseUser: FirebaseUser? = null
    private var commentList: MutableList<Comment>? = null
    private var commentAdapter: CommentAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        var intent = intent
        postId = intent.getStringExtra("postId")
        publisherId = intent.getStringExtra("publisherId")

        retribePostData(postId)
        getPosterData(publisherId)
        getCommenterData()

        add_comment_btn.setOnClickListener {
            if(comment_text.text.toString() == ""){
                Toast.makeText(this, "Please write your comment message first", Toast.LENGTH_LONG).show()
            }else{
                addCommentMessage(postId)
            }
        }

        //====== comments adapter and all operations
        var recyclerView: RecyclerView? = null
        recyclerView = findViewById(R.id.comments_recycler_view)
        var linearLayout = LinearLayoutManager(this)
        linearLayout.reverseLayout = true
        recyclerView.layoutManager = linearLayout

        commentList = ArrayList()
        commentAdapter = CommentAdapter(this, commentList)
        recyclerView.adapter = commentAdapter


        retdibeComments()
    }

    private fun retdibeComments() {
        var getCommentRef = FirebaseDatabase.getInstance().reference
                .child("Comments").child(postId!!)

        getCommentRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    commentList!!.clear()
                    for (comments in p0.children){
                        var comment = comments.getValue(Comment::class.java)
                        commentList!!.add(comment!!)
                    }
                    commentAdapter!!.notifyDataSetChanged()
                }
            }
        })
    }

    private fun addCommentMessage(postId: String?){
        var commentRef = FirebaseDatabase.getInstance().reference
                .child("Comments")
                .child(postId!!)

        var commentMap = HashMap<String, Any>()
        commentMap["commenterId"] = firebaseUser!!.uid
        commentMap["comment"] = comment_text.text.toString()

        commentRef.push().setValue(commentMap)

        comment_text.text.clear()
    }
    private fun getCommenterData() {
        var onlineUserRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        onlineUserRef.addValueEventListener( object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    var user = p0.getValue(User::class.java)
                    Picasso.get().load(user!!.getImage()).into(commenter_profile_image)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun getPosterData(publisherId: String?){
        var publisherDataRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherId!!)
        publisherDataRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    var user = p0.getValue(User::class.java)
                    Picasso.get().load(user!!.getImage()).into(poster_img_comment_page)
                    poser_username.text = user!!.getFullName()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
    private fun retribePostData(postId: String?) {
        var postDataRef = FirebaseDatabase.getInstance().reference.child("Posts").child(postId!!)
        postDataRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    var post = p0.getValue(Post::class.java)
                    Picasso.get().load(post!!.getPostImage()).into(comment_post_image)
                    comment_post_description.text = post!!.getPostDescription()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}