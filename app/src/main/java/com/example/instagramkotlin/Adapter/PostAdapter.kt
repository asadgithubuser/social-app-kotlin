package com.example.instagramkotlin.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramkotlin.CommentsActivity
import com.example.instagramkotlin.Models.Post
import com.example.instagramkotlin.Models.User
import com.example.instagramkotlin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.post_item.view.*

class PostAdapter(private val mContext: Context, private val mPost: List<Post>):RecyclerView.Adapter<PostAdapter.ViewHolder>() {
    var firebaseUser: FirebaseUser? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAdapter.ViewHolder {
        var view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    override fun onBindViewHolder(holder: PostAdapter.ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        var post = mPost[position]


        Picasso.get().load(post!!.getPostImage()).into(holder.postImage)
        holder.postDescription.text = post!!.getPostDescription()
        retribePostItemInfo(holder.userName, holder.profileImage, post.getPostPublisher() )

        //---- like / unlike operations

        isLikeOrNot(holder.total_likes, holder.likeButton, post.getPostId())
        holder.likeButton.setOnClickListener {
            if(holder.likeButton.tag == "Like"){
                FirebaseDatabase.getInstance().reference
                    .child("Likes") .child(post!!.getPostId())
                    .child(firebaseUser!!.uid)
                    .setValue(true)
            }else{
                FirebaseDatabase.getInstance().reference.child("Likes")
                    .child(post!!.getPostId()).child(firebaseUser!!.uid)
                    .removeValue()
            }
        }

        /// ====== comments operation
        holder.commentBtn.setOnClickListener {
            var intent = Intent(mContext, CommentsActivity::class.java)
            intent.putExtra("postId", post.getPostId())
            intent.putExtra("publisherId", post.getPostPublisher())
            mContext.startActivity(intent)
        }

        //===== save image for post
        holder.save_post_image.setOnClickListener {
            if(holder.save_post_image.tag == "save"){
                FirebaseDatabase.getInstance().reference.child("Save-Image").child(firebaseUser!!.uid)
                    .child(post.getPostId()).setValue(true)
            }else{
                FirebaseDatabase.getInstance().reference.child("Save-Image").child(firebaseUser!!.uid)
                    .child(post.getPostId()).removeValue()
            }
        }
        checkSaveOrNot(holder.save_post_image, post.getPostId())
        //===== get total comments
        getTotalComments(post.getPostId(), holder.totla_comments)


    }

    private fun checkSaveOrNot(savePostImage: ImageView, postId: String) {
        var saveImgRef = FirebaseDatabase.getInstance().reference.child("Save-Image")
            .child(firebaseUser!!.uid)

        saveImgRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.child(postId).exists()){
                    savePostImage.setImageResource(R.drawable.save_large_icon)
                    savePostImage.tag = "saved"
                }else{
                    savePostImage.setImageResource(R.drawable.save_unfilled_large_icon)
                    savePostImage.tag = "save"
                }
            }
        })
    }

    private fun getTotalComments(postId: String, totalComment: TextView){
        var checkComment: String = "comment"
        var commentsRef = FirebaseDatabase.getInstance().reference.child("Comments").child(postId)
        commentsRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    var comment = p0.childrenCount.toString()
                    if(comment.toInt() == 1){
                        checkComment = " comment"
                    }else if(comment.toInt() > 1){
                        checkComment = " comments"
                    }
                    totalComment.text = comment+ checkComment
                }
            }
        })
    }
    private fun isLikeOrNot(likes: TextView, likeBtn: ImageView, postId: String){
        var firebaseUser = FirebaseAuth.getInstance().currentUser
        var likesRef = FirebaseDatabase.getInstance().reference
                .child("Likes").child(postId)
        likesRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.child(firebaseUser!!.uid).exists()){
                    likeBtn.setImageResource(R.drawable.heart_clicked)
                    likeBtn.tag = "Liked"
                    likes.text = p0.childrenCount.toString()+" likes"
                }else{
                    likeBtn.setImageResource(R.drawable.heart_not_clicked)
                    likeBtn.tag = "Like"
                    likes.text = p0.childrenCount.toString()+" likes"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun retribePostItemInfo(userName: TextView, profileImage: ImageView, postPublisherId: String) {
        var poserRef = FirebaseDatabase.getInstance().reference.child("Users").child(postPublisherId)
        poserRef.addValueEventListener( object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    var user = p0.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).into(profileImage)
                    userName.text = user!!.getFullName()
                }
            }

        })
    }

    inner class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView){
        var userName : TextView = itemView.findViewById(R.id.username)
        var profileImage : ImageView = itemView.findViewById(R.id.postpage_profile_image)
        var postImage : ImageView = itemView.findViewById(R.id.post_item_image)
        var likeButton : ImageView = itemView.findViewById(R.id.like_btn)
        var total_likes : TextView = itemView.findViewById(R.id.total_likes)
        var totla_comments : TextView = itemView.findViewById(R.id.total_comments)
        var commentBtn : ImageView = itemView.findViewById(R.id.comment_btn)
        var postDescription : TextView = itemView.findViewById(R.id.post_description)
        var save_post_image : ImageView = itemView.findViewById(R.id.save_imgpost)
    }

}