package com.example.mapquiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView

class LevelActivity : AppCompatActivity() {
    private var quizName: TextView? = null
    private var lvlHard: TextView? = null
    private var lvlMedium: TextView? = null
    private var lvlEasy: TextView? = null
    private var leftBttn: ImageButton? = null
    private var name: String? = null
    private var player: String? = null
    private var playerType: String? = null
    private var groupID: String? = null
    private var admin: String? = null
    private var resDocRef: String? = null
    private var isGuest: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level)

        this.name = intent.getStringExtra("Type")
        this.player = intent.getStringExtra("Player") //multi, single
        this.isGuest = intent.getStringExtra("isGuest")
        println("player = $player")

        if(player == "Multi"){
            this.playerType = intent.getStringExtra("PlayerType") //player, admin
            this.groupID = intent.getStringExtra("GroupID")
            this.admin = intent.getStringExtra("admin")
            this.resDocRef = intent.getStringExtra("resDocRef")
        }
        
        init()
        
        level()
    }
    
    private fun init(){
        this.quizName = findViewById(R.id.text_quiz)
        this.lvlHard = findViewById(R.id.btn_hard)
        this.lvlMedium = findViewById(R.id.btn_medium)
        this.lvlEasy = findViewById(R.id.btn_easy)
        this.leftBttn = findViewById(R.id.ic_leftButton)
    }
    
    private fun level(){
        var intent: Intent?

        println("playerr = $player")

        if(player == "Single"){
            intent = Intent(this, QuestionActivity::class.java)
            intent.putExtra("isGuest", isGuest)
        }else{
            intent = Intent(this, WaitingRoomActivity::class.java)
            intent.putExtra("PlayerType", playerType)
            intent.putExtra("GroupID", groupID)
            intent.putExtra("resDocRef", resDocRef)
        }
        intent.putExtra("Player", player)
        intent.putExtra("Type", name)

        quizName?.text = name

        lvlHard?.setOnClickListener{
            intent?.putExtra("Level", "Zor")
            startActivity(intent)
            this.finish()
        }

        lvlMedium?.setOnClickListener{
            intent?.putExtra("Level", "Orta")
            startActivity(intent)
            this.finish()
        }

        lvlEasy?.setOnClickListener{
            intent?.putExtra("Level", "Kolay")
            startActivity(intent)
            this.finish()
        }

        /*leftBttn?.setOnClickListener{
            intent = Intent(this, CategoryActivity::class.java)
            startActivity(intent)
            this.finish()
        }*///**

        leftBttn?.setOnClickListener{
            intent = Intent(this, CategoryActivity::class.java)
            intent?.putExtra("Player", player)
            intent?.putExtra("PlayerType", playerType)
            intent?.putExtra("GroupID", groupID)
            intent?.putExtra("resDocRef", resDocRef)
            intent?.putExtra("isGuest", isGuest)
            startActivity(intent)
            this.finish()
        }
    }

    override fun onBackPressed() {
        intent = Intent(this, CategoryActivity::class.java)
        intent?.putExtra("Player", player)
        intent?.putExtra("PlayerType", playerType)
        intent?.putExtra("GroupID", groupID)
        intent?.putExtra("resDocRef", resDocRef)
        intent?.putExtra("isGuest", isGuest)
        startActivity(intent)
        this.finish()
    }

}