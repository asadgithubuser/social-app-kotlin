package com.example.instagramkotlin.Models

class Comment {
    private var commenterId: String = ""
    private var comment: String = ""

    constructor()

    constructor(commenterId: String, comment: String){
        this.commenterId = commenterId
        this.comment = comment
    }

    fun getCommenterId():String{
        return commenterId
    }
    fun setCommenterId(commenterId: String){
        this.commenterId = commenterId
    }
    fun getComment():String{
        return comment
    }
    fun setComment(comment:String){
        this.comment = comment
    }
}