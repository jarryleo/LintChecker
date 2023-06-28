package cn.leo.lint

import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        ResUtils.context = this
    }

}