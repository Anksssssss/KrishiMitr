package com.example.krishimitr.models

import android.text.Editable

data class Farmer(
    var firstName: String?=null,
    val lastName:String?=null,
    val phoneNumber:String?=null,
    val email:String?=null,
    val password:String?=null,
    var uid: String? = null,
    var isExpert: String? = null,
    var specialization: String? = null
)