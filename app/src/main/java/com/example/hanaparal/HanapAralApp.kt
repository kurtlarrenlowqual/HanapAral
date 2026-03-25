package com.example.hanaparal

import android.app.Application
import com.example.hanaparal.core.AppContainer

class HanapAralApp : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}