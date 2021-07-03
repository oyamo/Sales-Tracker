package com.oyasis.salestracker.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Color

import android.view.WindowManager

import com.oyasis.salestracker.R


class ActivityDashBoard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor("#00BFA6")
        setContentView(R.layout.activity_dashboard)
    }
}