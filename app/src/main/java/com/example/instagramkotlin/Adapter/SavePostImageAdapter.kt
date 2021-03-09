package com.example.instagramkotlin.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramkotlin.Models.Post
import com.example.instagramkotlin.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.grid_user_image_item.view.*

class SavePostImageAdapter(private val mContext: Context, private val mSaveList: List<Post>)
    :RecyclerView.Adapter<SavePostImageAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavePostImageAdapter.ViewHolder {
        var view = LayoutInflater.from(mContext).inflate(R.layout.grid_user_image_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mSaveList.size
    }

    override fun onBindViewHolder(holder: SavePostImageAdapter.ViewHolder, position: Int) {
        var saveImage = mSaveList[position]
        Picasso.get().load(saveImage!!.getPostImage()).into(holder.imageItem)
    }

    inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
        var imageItem : ImageView = itemView.findViewById(R.id.grid_user_image_item)
    }
}