package com.example.nework.application

import android.app.Application
import com.example.nework.auth.AppAuth
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    @Inject
    lateinit var appAuth: AppAuth
}