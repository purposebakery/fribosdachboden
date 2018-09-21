package com.purposebakery.fribosdachboden.generic

import android.annotation.SuppressLint
import android.content.ContextWrapper
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.pixplicity.easyprefs.library.Prefs

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(packageName)
                .setUseDefaultSharedPreference(true)
                .build()
    }
}