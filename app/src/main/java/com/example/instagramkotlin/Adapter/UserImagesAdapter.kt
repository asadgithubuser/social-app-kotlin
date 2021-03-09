package com.example.instagramkotlin.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramkotlin.Models.Post
import com.example.instagramkotlin.R
import com.example.instagramkotlin.fragments.PostDetailsFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.grid_user_image_item.view.*

class UserImagesAdapter(private val mContext: Context, private val mPostList: MutableList<Post>?)
    :RecyclerView.Adapter<UserImagesAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(mContext).inflate(R.layout.grid_user_image_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPostList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var postList = mPostList!![position]
        Picasso.get().load(postList!!.getPostImage()).into(holder.imageItem)

        holder.imageItem.setOnClickListener {
            var changeToFragment = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
            changeToFragment.putString("postId", postList!!.getPostId())
            changeToFragment.apply()

            (mContext as FragmentActivity).getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_frameLayout, PostDetailsFragment()).commit()
        }
    }

    inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
        var imageItem : ImageView = itemView.findViewById(R.id.grid_user_image_item)
    }
}