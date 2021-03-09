package com.example.instagramkotlin.Models

class User {
    private var fullName: String = ""
    private var profession: String = ""
    private var email: String = ""
    private var password: String = ""
    private var aboutUrSelf: String = ""
    private var image: String = ""
    private var uid: String = ""

    constructor()

    constructor(fullName: String, profession: String, email: String, password: String, aboutUrSelf: String,
    image: String, uid: String){
        this.fullName = fullName
        this.profession = profession
        this.email = email
        this.password = password
        this.aboutUrSelf = aboutUrSelf
        this.image = image
        this.uid = uid
    }

    fun getFullName():String{
        return fullName
    }
    fun setFullName(fullName: String){
        this.fullName = fullName
    }
    fun getProfession():String{
        return profession
    }
    fun getEmail():String{
        return email
    }
    fun getPassword():String{
        return password
    }
    fun getAboutUrSelf():String{
        return aboutUrSelf
    }
    fun getImage():String{
        return image
    }
    fun getUid():String{
        return uid
    }


    fun setProfession(profession: String){
        this.profession = profession
    }
    fun setEmail(email: String){
        this.email = email
    }
    fun setPassword(password: String){
        this.password = password
    }
    fun setAboutUrSelf(aboutUrSelf: String){
        this.aboutUrSelf = aboutUrSelf
    }
    fun setImage(image: String){
        this.image = image
    }
    fun setUid(uid: String){
        this.uid = uid
    }





}