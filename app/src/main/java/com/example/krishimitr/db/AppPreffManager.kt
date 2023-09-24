package com.example.krishimitr.db

import android.content.Context
import android.content.SharedPreferences
import org.jetbrains.annotations.NonNls

class AppPreffManager(context: Context) {

    private val pref: SharedPreferences
    private val editor: SharedPreferences.Editor

    init {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = pref.edit()
    }

    var currUserUid: String
        get() = pref.getString(UID,"null").toString()
        set(userUid){
            editor.putString(UID,userUid)
            editor.commit()
        }

    var currUserKey: String
        get() = pref.getString(KEY,"null").toString()
        set(userKey){
            editor.putString(KEY,userKey)
            editor.commit()
        }


    companion object {
        @NonNls
        private val PREF_NAME = "userapp"
        private val UID = "uid"
        private val KEY = "key"
    }


}