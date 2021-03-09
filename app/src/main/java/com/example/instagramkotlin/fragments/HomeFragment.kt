package com.example.instagramkotlin.fragments

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<Post>? = null
    private var folowingList :MutableList<Post>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_home, container, false)

        var recyclerView : RecyclerView? = null
        recyclerView = view.findViewById(R.id.home_recycleview_postitem)
        var postLayoutManager = LinearLayoutManager(context)
        postLayoutManager.reverseLayout = true
        postLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = postLayoutManager

        postList = ArrayList()
        postAdapter = context?.let{PostAdapter(it, postList as ArrayList<Post>)}
        recyclerView.adapter = postAdapter

        postFromFollowins()

        return view
    }

    private fun postFromFollowins(){
        folowingList = ArrayList()
        var followingRef = FirebaseDatabase.getInstance().reference.child("Follow")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("Following")

        followingRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    (folowingList as ArrayList<String>).clear()
                    for( postData in p0.children){
                        postData.key?.let { (folowingList as ArrayList<String>).add(it) }
                    }
                     retribePostData()
                }
            }

        })
    }

    private fun retribePostData(){
        var postRef = FirebaseDatabase.getInstance().reference.child("Posts")
        postRef.addValueEventListener( object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    postList?.clear()
                    for(post in p0.children){
                        var postData = post.getValue(Post::class.java)
                        for (id in (folowingList as ArrayList<String>)){
                            if (postData!!.getPostPublisher() == id){
                                postList!!.add(postData)
                            }
                            postAdapter!!.notifyDataSetChanged()
                        }
                    }
                }
            }
        })
    }
}