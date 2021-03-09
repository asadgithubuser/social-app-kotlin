package com.example.instagramkotlin.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramkotlin.Models.Comment
import com.example.instagramkotlin.Models.User
import com.example.instagramkotlin.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.comment_item.view.*

class CommentAdapter(private val mContext:Context, private val commentList: MutableList<Comment>?)
    : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.ViewHolder {
        var view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return commentList!!.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var comments = commentList!![position]
        holder.commenter_message.text = comments.getComment()

        getCommenterInfo(comments!!.getCommenterId(), holder.commenter_name, holder.commenterImage)
    }

    private fun getCommenterInfo(commenterId: String, name: TextView, image: CircleImageView) {
        var commenterRef = FirebaseDatabase.getInstance().reference
                .child("Users").child(commenterId)
        commenterRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    var user = p0.getValue(User::class.java)
                    name.text = user!!.getFullName()
                   Picasso.get().load(user!!.getImage()).into(image)
                }
            }
        })
    }

    inner class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView){
        var commenterImage: CircleImageView = itemView.commenter_img_ci
        var commenter_name: TextView = itemView.commenter_username_ci
        var commenter_message: TextView = itemView.commenter_message_ci
    }


}