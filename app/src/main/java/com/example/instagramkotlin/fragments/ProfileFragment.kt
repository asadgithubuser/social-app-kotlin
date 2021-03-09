package com.example.instagramkotlin.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramkotlin.AccountSettingActivity
import com.example.instagramkotlin.Adapter.SavePostImageAdapter
import com.example.instagramkotlin.Adapter.UserImagesAdapter
import com.example.instagramkotlin.Models.Post
import com.example.instagramkotlin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.instagramkotlin.Models.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.fixedRateTimer

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

private lateinit var imgButton : ImageView
/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private lateinit var profileId:String
    private lateinit var firebaseUser:FirebaseUser
    private var postImagesList: MutableList<Post>? = null
    private var userImagesAdapter : UserImagesAdapter ? = null
    private var savePostImageAdapter: UserImagesAdapter? = null
    private var saveImageList : MutableList<Post>? = null
    private var saveImagesKeyList: List<String>? = null


    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        //==========
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        var pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if(pref != null){
            this.profileId = pref.getString("profileId", "none").toString()
        }
        if(profileId == firebaseUser?.uid){
            view.profile_edit_follow_btn.text = "Edit Profile"
        }else if(profileId != firebaseUser?.uid){
            checkFollowAndFollowingText()
        }

        //======== follow and following
        followAndFollowing(firebaseUser, view)


        //====
        getFollowers()
        getFollowing()
        getProfileInfo()
        getTotalPosts()

        //==== display user all images
        var recyclerView :RecyclerView
        recyclerView = view.findViewById(R.id.profile_all_img_recyclerView)
        recyclerView.setHasFixedSize(true)

        var imageLayout: LinearLayoutManager = GridLayoutManager(context, 3)
        recyclerView.layoutManager = imageLayout

        postImagesList = ArrayList()
        userImagesAdapter = context?.let { UserImagesAdapter(it, postImagesList ) }
        recyclerView.adapter = userImagesAdapter

        retribeUserPotImages()



        //===== display all-save images
        var recyclerViewsave : RecyclerView
        recyclerViewsave = view.findViewById(R.id.profile_saved_img_recyclerView)
        recyclerViewsave.setHasFixedSize(true)

        var saveLayoutManager : LinearLayoutManager = GridLayoutManager(context, 3)
        recyclerViewsave.layoutManager = saveLayoutManager

        saveImageList = ArrayList()
        savePostImageAdapter = context?.let { UserImagesAdapter(it, saveImageList) }
        recyclerViewsave.adapter = savePostImageAdapter

        retribeSaveImageItem()


        //===== display default view
        recyclerView.visibility = View.VISIBLE
        recyclerViewsave.visibility = View.GONE

        view.show_saved_images_btn.setOnClickListener {
            recyclerViewsave.visibility = View.VISIBLE
            recyclerView.visibility  = View.GONE
        }
        view.all_profiler_image.setOnClickListener {
            recyclerViewsave.visibility = View.GONE
            recyclerView.visibility  = View.VISIBLE
        }

        return view
    }


    private fun retribeSaveImageItem() {
        saveImagesKeyList = ArrayList()
        Log.d("key", saveImagesKeyList.toString())
        var saveImageRef = FirebaseDatabase.getInstance().reference.child("Save-Image")
            .child(firebaseUser!!.uid)
        saveImageRef.addValueEventListener( object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                   for (saveImage in p0.children){
                       (saveImagesKeyList as ArrayList<String>).add(saveImage.key!!)
                   }
                    savedImageList()
                }
            }
        })

    }

    private fun savedImageList(){
        var postRef = FirebaseDatabase.getInstance().reference.child("Posts")
        postRef.addValueEventListener( object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    saveImageList!!.clear()

                    for (p in p0.children){
                        var post = p.getValue(Post::class.java)
                        for (key in saveImagesKeyList!!){
                            if(key == post!!.getPostId()){
                                saveImageList!!.add(post!!)
                            }
                        }
                    }
                    savePostImageAdapter!!.notifyDataSetChanged()
                }
            }
        })
    }
    private fun getTotalPosts() {
        var postRef = FirebaseDatabase.getInstance().reference.child("Posts")
        postRef.addValueEventListener( object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    var postCounter = 0
                    for(postItem in p0.children){
                        var post = postItem.getValue(Post::class.java)
                        if(post!!.getPostPublisher() == profileId){
                            postCounter++
                        }
                        total_posts_no.text = postCounter.toString()
                    }

                }
            }
        })
    }

    private fun retribeUserPotImages() {
        var postImageRef = FirebaseDatabase.getInstance().reference
                .child("Posts")
        postImageRef.addValueEventListener( object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    postImagesList!!.clear()

                    for (images in p0.children){
                        var post = images.getValue(Post::class.java)
                        if(post!!.getPostPublisher().equals(profileId)){
                            postImagesList!!.add(post!!)
                        }
                        Collections.reverse(postImagesList)
                        userImagesAdapter!!.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    private fun followAndFollowing(firebaseUser: FirebaseUser, view: View?) {
        view?.profile_edit_follow_btn?.setOnClickListener{
            var editBtnTxt = view?.profile_edit_follow_btn?.text
            when{
                editBtnTxt == "Edit Profile" -> {
                    startActivity(Intent(context, AccountSettingActivity::class.java))
                }
                editBtnTxt == "Follow" -> {
                    firebaseUser?.uid.let { onlineUser ->
                        FirebaseDatabase.getInstance().reference
                                .child("Follow").child(onlineUser.toString())
                                .child("Following").child(profileId)
                                .setValue(true)
                    }
                    firebaseUser?.uid.let { onlineUser ->
                        FirebaseDatabase.getInstance().reference
                                .child("Follow").child(profileId)
                                .child("Followers").child(onlineUser.toString())
                                .setValue(true)
                    }
                }

                editBtnTxt == "Following" -> {
                    firebaseUser?.uid.let { onlineUser ->
                        FirebaseDatabase.getInstance().reference
                                .child("Follow").child(onlineUser.toString())
                                .child("Following").child(profileId)
                                .removeValue()
                    }
                    firebaseUser?.uid.let { onlineUser ->
                        FirebaseDatabase.getInstance().reference
                                .child("Following").child(profileId)
                                .child("Followers").child(onlineUser.toString())
                                .removeValue()
                    }
                }
            }
        }

    }

    private fun checkFollowAndFollowingText() {
        var followingRef = firebaseUser?.uid.let { onlineUser ->
                FirebaseDatabase.getInstance().reference.child("Follow")
                    .child(onlineUser.toString()).child("Following")
        }
        if(followingRef != null){
            followingRef.addValueEventListener( object : ValueEventListener{

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.child(profileId).exists()){
                        view?.profile_edit_follow_btn?.text = "Following"
                    }else{
                        view?.profile_edit_follow_btn?.text = "Follow"
                    }
                }
                override fun onCancelled(p0: DatabaseError) {

                }
            })
        }
    }
    private fun getFollowers(){
        var getFollowers = FirebaseDatabase.getInstance().reference.child("Follow")
                    .child(profileId).child("Followers")

        getFollowers.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    view?.total_followers_number?.text = p0.childrenCount.toString()
                }
            }
        })
    }
    private fun getFollowing(){
        var getFollowingRef = firebaseUser?.uid.let { onlineUser ->
            FirebaseDatabase.getInstance().reference.child("Follow")
                    .child(onlineUser.toString()).child("Following")
        }
        getFollowingRef.addValueEventListener( object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    view?.total_following_number?.text = p0.childrenCount.toString()
                }
            }
        })
    }

    private fun getProfileInfo(){
        var userRef = FirebaseDatabase.getInstance().reference.child("Users").child(profileId)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    var user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(view?.profile_img)
                    view?.profile_name?.text = user!!.getFullName()
                    view?.profile_profession?.text = user!!.getProfession()
                    view?.profile_bio?.text = user!!.getAboutUrSelf()
                }
            }
        })
    }
    override fun onPause() {
        super.onPause()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onStop() {
        super.onStop()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }
}