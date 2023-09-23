package com.example.krishimitr.utils

import com.google.firebase.auth.PhoneAuthProvider

class Temp {
    companion object{
        var resendToken: PhoneAuthProvider.ForceResendingToken ?= null
    }
}