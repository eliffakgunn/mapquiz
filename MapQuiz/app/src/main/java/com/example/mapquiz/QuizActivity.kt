package com.example.mapquiz

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks

class QuizActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener(navListener)
        bottomNav.itemIconTintList = null

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, QuizHomeFragment()).commit()

    }


    private val navListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        var selectedFragment : Fragment? = null

        when (menuItem.itemId) {
            R.id.home -> selectedFragment = QuizHomeFragment()
            R.id.user -> selectedFragment = QuizUserFragment()
            R.id.ranking -> selectedFragment = QuizRankingFragment()
        }

        if (selectedFragment != null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit()
        }

        return@OnNavigationItemSelectedListener true
    }
}