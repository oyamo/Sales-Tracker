package com.oyasis.salestracker.ui

import UserPreferencesRepository
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.oyasis.salestracker.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        GlobalScope.launch(Dispatchers.Main) {
            var intent = Intent(this@MainActivity, Onboarding::class.java)
            val repo = UserPreferencesRepository.getInstance(this@MainActivity)
            if (repo.hasLoggedIn()) {
                intent =  Intent(this@MainActivity, ActivityDashBoard::class.java)
            }
            delay(2000)
            startActivity(intent)
            finish()
        }
    }
}