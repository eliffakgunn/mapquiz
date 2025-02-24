package com.example.flutter_mapquiz

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import io.flutter.Log
import io.flutter.embedding.android.FlutterActivity

class GlobeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_globe)
        } catch (e: Exception) {
            Log.e("GlobeActivity", "onCreateView", e);
            throw e;
        }


        /*var fragment: Fragment = LocalGlobeFragment()
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()*/
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}