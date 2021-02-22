package com.example.instagramkotlin.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramkotlin.Adapter.UserAdapter
import com.example.instagramkotlin.Models.User
import com.example.instagramkotlin.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*


/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {

    private var recyclerView : RecyclerView? = null
    private var userAdapter: UserAdapter? = null
    private var mUser: MutableList<User>? = null


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=  inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.search_userItem_list)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        mUser = ArrayList()
        userAdapter = context?.let{UserAdapter(it,mUser as ArrayList<User>,true)}
        recyclerView?.adapter = userAdapter

        view.edit_search_field.addTextChangedListener(object: TextWatcher
        {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(view.edit_search_field.text.toString() == ""){

                }
                else{
                    recyclerView?.visibility = View.VISIBLE
                    retribeUsers()
                    userSearch(s.toString().toLowerCase())
                }
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })
        return view

    }

    private fun userSearch(input: String) {
        val query = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .orderByChild("fullName")
                .startAt(input)
                .endAt(input + "\uf8ff")

        query.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUser?.clear()
                for (snapshot in dataSnapshot.children){
                    val user = snapshot.getValue(User::class.java)
                    if(user != null){
                        mUser?.add(user)
                    }
                }
                userAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun retribeUsers() {
        val userRef = FirebaseDatabase.getInstance().getReference().child("Users")

        userRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(view?.edit_search_field?.text.toString() == ""){
                    mUser?.clear()

                    for (snapshot in dataSnapshot.children){
                        val user = snapshot.getValue(User::class.java)
                        if(user != null){
                            mUser?.add(user)
                        }
                    }
                    userAdapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


}