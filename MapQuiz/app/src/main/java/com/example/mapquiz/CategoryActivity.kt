package com.example.mapquiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView

class CategoryActivity : AppCompatActivity() {
    var country: TextView? = null
    var capital: TextView? = null
    var flag: TextView? = null
    var mixed: TextView? = null
    var leftBttn: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        init()

        category()
    }

    private fun init(){
        country = findViewById(R.id.btn_country)
        capital = findViewById(R.id.btn_capital)
        flag = findViewById(R.id.btn_flag)
        mixed = findViewById(R.id.btn_mixed)
        leftBttn = findViewById(R.id.ic_leftButton)
    }

    private fun category(){
        val player = intent.getStringExtra("Player") //multi, single
        val playerType = intent.getStringExtra("PlayerType") //player, admin
        val groupID = intent.getStringExtra("GroupID")
        val resDocRef = intent.getStringExtra("resDocRef")
        val isGuest = intent.getStringExtra("isGuest")

        println("player cat = $player")

        var intent = Intent(this, LevelActivity::class.java)
        intent.putExtra("Player", player)
        intent.putExtra("PlayerType", playerType)
        intent.putExtra("GroupID", groupID)
        intent.putExtra("resDocRef", resDocRef)
        intent.putExtra("isGuest", isGuest)

        country?.setOnClickListener{
            intent.putExtra("Type", "Ülke")
            startActivity(intent)
            this.finish()
        }

        capital?.setOnClickListener{
            intent.putExtra("Type", "Başkent")
            startActivity(intent)
            this.finish()
        }

        flag?.setOnClickListener{
            intent.putExtra("Type", "Bayrak")
            startActivity(intent)
            this.finish()
        }

        mixed?.setOnClickListener{
            intent.putExtra("Type", "Genel Kültür")
            startActivity(intent)
            this.finish()
        }

        leftBttn?.setOnClickListener{
            intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, QuizActivity::class.java)
        startActivity(intent)
    }
}