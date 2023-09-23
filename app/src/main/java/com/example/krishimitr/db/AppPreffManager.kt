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

    companion object {
        @NonNls
        private val PREF_NAME = "userapp"

        @NonNls
        private const val PREF_IS_USER_LOGGED_ID = "is_use_logged_in"
    }

    fun setData(boolean: Boolean) {
        editor.putBoolean(PREF_IS_USER_LOGGED_ID, boolean)
    }

    val isUserLoggedIn: Boolean
        get() = pref.getBoolean(PREF_IS_USER_LOGGED_ID, false)

}