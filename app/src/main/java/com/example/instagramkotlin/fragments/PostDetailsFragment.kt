package com.example.instagramkotlin.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramkotlin.Adapter.PostAdapter
import com.example.instagramkotlin.Models.Post
import com.example.instagramkotlin.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [PostDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PostDetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private var postId: String = ""
    private var postItemList: MutableList<Post>? = null
    private var postAdapter: PostAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_post_details, container, false)

        var getPreferance = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if(getPreferance != null){
            postId = getPreferance.getString("postId", "none").toString()
        }

        //======= adapter operatin
        var recyclerView: RecyclerView
        recyclerView = view.findViewById(R.id.post_details_recyclerView)
        recyclerView.setHasFixedSize(true)

        var postLayout = LinearLayoutManager(context)
        recyclerView.layoutManager = postLayout

        postItemList = ArrayList()
        postAdapter = context?.let { PostAdapter(it, postItemList as MutableList<Post>) }
        recyclerView.adapter = postAdapter


        retribeSinglePostData()


        return view
    }

    private fun retribeSinglePostData() {
        var postRef = FirebaseDatabase.getInstance().reference.child("Posts").child(postId)
        postRef.addValueEventListener( object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    var postData = p0.getValue(Post::class.java)
                    postItemList!!.add(postData!!)
                    postAdapter!!.notifyDataSetChanged()
                }
            }
        })
    }

}