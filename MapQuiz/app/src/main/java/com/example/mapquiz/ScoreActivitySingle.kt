package com.example.mapquiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ScoreActivitySingle : AppCompatActivity() {
    private var type: String? = null
    private var level: String? = null
    private var isGuest: String? = null
    private var totalPoint: String? = null
    private var rightAns: String? = null
    private var wrongAns: String? = null
    private var emptyQ: String? = null
    private var points: String? = null
    private var txtTrue: TextView? = null
    private var txtFalse: TextView? = null
    private var txtMissed: TextView? = null
    private var txtPoints: TextView? = null
    private var txtTotalPoints: TextView? = null
    private var txtTotal: TextView? = null
    private var btnReplay: ImageButton? = null
    private var btnHome: ImageButton? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score_single)

        init()
        setScreen()
    }
    
    private fun init(){
        this.rightAns = intent.getStringExtra("rightAns")
        this.wrongAns = intent.getStringExtra("wrongAns")
        this.emptyQ = intent.getStringExtra("emptyQ")
        this.points = intent.getStringExtra("points")
        this.totalPoint = intent.getStringExtra("totalPoint")
        this.type = intent.getStringExtra("Type")
        this.level = intent.getStringExtra("Level")
        this.isGuest = intent.getStringExtra("isGuest")

        this.txtTrue = findViewById(R.id.txt_true)
        this.txtFalse = findViewById(R.id.txt_false)
        this.txtMissed = findViewById(R.id.txt_missed)
        this.txtPoints = findViewById(R.id.txt_points)
        this.txtTotalPoints = findViewById(R.id.txt_total_points)
        this.txtTotal = findViewById(R.id.txtTotal)
        this.btnReplay = findViewById(R.id.btn_replay)
        this.btnHome = findViewById(R.id.btn_home)
    }

    private fun setScreen(){
        val mAuth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        txtTrue?.text = rightAns
        txtFalse?.text = wrongAns
        txtMissed?.text = emptyQ
        txtPoints?.text = points
        if(isGuest == "true") {
            txtTotalPoints?.visibility = View.INVISIBLE
            txtTotal?.visibility = View.INVISIBLE
        }
        else {
            txtTotalPoints?.text = totalPoint

            if (mAuth.currentUser?.uid != null) {
                db.collection("Kullanıcılar").document(mAuth.currentUser?.uid!!)
                    .get().addOnSuccessListener { snapshot ->
                        if (snapshot != null) {
                            db.collection("Kullanıcılar").document(mAuth.currentUser?.uid!!)
                                .update("puan", totalPoint?.toInt())
                                .addOnSuccessListener {
                                    Log.d(
                                        "ScoreActivity",
                                        "Point successfully updated!"
                                    )
                                }
                                .addOnFailureListener { e ->
                                    Log.w(
                                        "ScoreActivity",
                                        "Error updating point",
                                        e
                                    )
                                }
                        } else {
                            Log.d("ScoreActivity", "No such document")
                        }
                    }
            }
        }

        btnReplay?.setOnClickListener{
            intent = Intent(this, QuestionActivity::class.java)
            intent?.putExtra("Type", type)
            intent?.putExtra("Level", level)
            intent?.putExtra("Player", "Single")
            intent?.putExtra("isGuest", isGuest)
            startActivity(intent)
            this.finish()
        }

        btnHome?.setOnClickListener{
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, QuizActivity::class.java)
        startActivity(intent)
        this.finish() //*
    }
}