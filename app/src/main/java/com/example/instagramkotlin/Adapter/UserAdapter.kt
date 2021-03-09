package com.example.instagramkotlin.Adapter

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramkotlin.Models.User
import com.example.instagramkotlin.R
import com.example.instagramkotlin.fragments.ProfileFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user_tem.view.*

class UserAdapter(private var mContext: Context, private var mUser: List<User>, private var isFragment: Boolean = false)
    : RecyclerView.Adapter<UserAdapter.ViewHolder>(){

    val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.activity_user_tem, parent, false)
        return UserAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return mUser.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val user = mUser[position]

        viewHolder.full_name.text = user.getFullName()
        viewHolder.profession.text = user.getProfession()
        Picasso.get().load(user.getImage()).placeholder(R.drawable.profile).into(viewHolder.p_image)

        followAndFolloing(user.getUid(), viewHolder)

        //======== follow-following function
        followFollowingStatus(user.getUid(), viewHolder.follow_btn)

        // ======== show user details
        viewHolder.itemView.setOnClickListener(View.OnClickListener {
            var pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                pref.putString("profileId", user.getUid())
                pref.apply()

            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frameLayout, ProfileFragment()).commit()

        })
    }

    private fun followAndFolloing(uid:String, viewHolder: UserAdapter.ViewHolder) {
        viewHolder.follow_btn.setOnClickListener {
            if(viewHolder.follow_btn.text.toString() == "Follow"){
                firebaseUser?.uid.let {onlineuser ->
                    FirebaseDatabase.getInstance().reference
                            .child("Follow").child(onlineuser.toString())
                            .child("Following").child(uid)
                            .setValue(true).addOnCompleteListener{ task ->
                                if (task.isSuccessful){
                                    FirebaseDatabase.getInstance().reference
                                            .child("Follow").child(uid)
                                            .child("Followers").child(onlineuser.toString())
                                            .setValue(true).addOnCompleteListener { task ->

                                            }
                                }
                            }
                }
            }
            else
            {
                firebaseUser?.uid.let { onlineUser ->
                    FirebaseDatabase.getInstance().reference
                            .child("Follow").child(onlineUser.toString())
                            .child("Following").child(uid)
                            .removeValue().addOnCompleteListener { task ->
                                if(task.isSuccessful){
                                    FirebaseDatabase.getInstance().reference
                                            .child("Follow").child(uid)
                                            .child("Followers").child(onlineUser.toString())
                                            .removeValue().addOnCompleteListener {

                                            }
                                }
                            }
                }
            }
        }
    }

    class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView){
        var full_name: TextView = itemView.findViewById(R.id.f_name)
        var profession: TextView = itemView.findViewById(R.id.profession)
        var p_image: ImageView = itemView.findViewById(R.id.prof_image)
        var follow_btn: Button = itemView.findViewById(R.id.follow_btn_sa)
    }

    private fun followFollowingStatus(uid: String, followBtn: Button) {
        var followBtnRef = firebaseUser?.uid.let { onlineUser ->
            FirebaseDatabase.getInstance().reference.child("Follow").child(onlineUser.toString())
                    .child("Following")
        }
        followBtnRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.child(uid).exists()){
                    followBtn.text = "Following"
                }else{
                    followBtn.text = "Follow"
                }
            }

        })
    }
}