package com.example.instagramkotlin.Models

class Post {
    private var postId = ""
    private var postDescription = ""
    private var postImage = ""
    private var postPublisher = ""

    constructor()

    constructor(postId : String, postDescription: String, postImage: String, postPublisher: String){
        this.postId = postId
        this.postDescription = postDescription
        this.postImage = postImage
        this.postPublisher = postPublisher
    }

     fun getPostId(): String{
        return postId
    }
     fun getPostDescription(): String{
        return postDescription
    }
     fun getPostImage(): String{
        return postImage
    }
     fun getPostPublisher(): String{
        return postPublisher
    }
     fun setPostId(postId: String){
        this.postId = postId
    }
     fun setPostDesctiption(postDescription: String){
        this.postDescription = postDescription
    }
     fun setPostImage(postImage: String){
        this.postImage = postImage
    }
     fun setPostPublisher(postPublisher: String){
        this.postPublisher = postPublisher
    }






}