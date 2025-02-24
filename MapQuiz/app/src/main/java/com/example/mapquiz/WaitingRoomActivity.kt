package com.example.mapquiz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.*


class WaitingRoomActivity : AppCompatActivity() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var groupRef: CollectionReference? = null
    private var adapter: PlayerAdapterWR? = null
    private var groupID: String? = null
    private var level: String? = null
    private var type: String? = null
    private var playerType: String? = null
    private var btnInvite: Button? = null
    private var btnStart: Button? = null
    private var btnBack: ImageButton? = null
    private var txtID: TextView? = null
    private var resDocRef: String? = null
    private var docRef: DocumentReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting_room)

        println("WaitingRoomActivity")

        init()
        setUpRecyclerView()

        if(playerType == "Admin"){
            admin()
            updateDB()
        }
        else{
            btnInvite?.visibility = View.INVISIBLE
            btnStart?.visibility = View.INVISIBLE
        }

        btnBack?.setOnClickListener{
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }

    private fun init(){
        this.playerType = intent.getStringExtra("PlayerType") //player, admin
        this.groupID = intent.getStringExtra("GroupID")
        this.level = intent.getStringExtra("Level")
        this.type = intent.getStringExtra("Type")
        this.resDocRef = intent.getStringExtra("resDocRef")
        this.groupRef = db.collection("Oyun Sonuçları").document(resDocRef!!).collection("Oyuncular")

        println("------------------------------------------------------------")
        println("playerType = $playerType")
        println("groupID = $groupID")
        println("level = $level")
        println("type = $type")
        println("resDocRef = $resDocRef")
        println("groupRef = $groupRef")

        this.btnInvite = findViewById(R.id.btnInvite)
        this.btnStart = findViewById(R.id.btnStart)
        this.txtID = findViewById(R.id.txt_id)
        this.btnBack = findViewById(R.id.ic_leftButton)

        txtID?.text = groupID
    }

    private fun updateDB(){
        val groupDocRef = db.collection("Gruplar").document(groupID!!)
        groupDocRef.get().addOnSuccessListener { snapshot ->
            if (snapshot != null) {
                groupDocRef.update("type", type)
                groupDocRef.update("level", level)

                val questionArr = ArrayList<Int>()

                db.collection(type!!)
                        .document(level!!)
                        .collection("Sorular")
                        .get()
                        .addOnSuccessListener { documents ->
                            for(i in 0..10){
                                val num = documents?.size()?.minus(1)
                                var rand = (0..num!!).random()

                                var bool = questionArr.contains(rand)
                                while (bool) {
                                    rand = (0..num).random()
                                    bool = questionArr.contains(rand)
                                }

                                groupDocRef.update("questionArr", questionArr)

                                questionArr.add(rand)
                            }
                        }
                        .addOnFailureListener {
                            println("SORGU HATASI")
                        }
            }
        }
    }

    private fun admin() {
        btnInvite?.setOnClickListener{
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, "Yarismaya var misin? Grup Kodu: $groupID")
            intent.type = "text/plain"
            startActivity(intent)
        }

        btnStart?.setOnClickListener{
            db.collection("Gruplar").document(groupID!!).update("startGame", true)
            startQuiz()
        }
    }

    private fun setUpRecyclerView() {
        val collectionRef = db.collection("Quiz Sonuçları").document(resDocRef!!).collection("Oyuncular")
        val query = collectionRef.orderBy("point", Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<Player>()
                .setQuery(query, Player::class.java)
                .build()

        adapter = PlayerAdapterWR(options)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun player(){
        println("playeeeeeeeeeeer")
        docRef = db.collection("Gruplar").document(groupID!!)
        //db.collection("Gruplar").document(groupID!!).addSnapshotListener(this) { value, error ->
        docRef?.addSnapshotListener(this) { value, error ->
            if (error != null) {
                Log.d("WaitingRoomActivity", error.toString())
            }
            if (value!!.exists()) {
                db.collection("Gruplar").document(groupID!!)
                    .get().addOnSuccessListener { snapshot ->
                        if (snapshot != null) {
                            val startGame = snapshot.getBoolean("startGame")
                            type = snapshot.getString("type")
                            level = snapshot.getString("level")

                            if(startGame == true){
                                startQuiz()
                            }
                        }
                    }
            }
        }
    }

    private fun startQuiz(){
        val intent = Intent(this, QuestionActivity::class.java)
        intent.putExtra("PlayerType", playerType)
        intent.putExtra("GroupID", groupID)
        intent.putExtra("Player", "Multi")
        intent.putExtra("Type", type)
        intent.putExtra("Level", level)
        intent.putExtra("resDocRef", resDocRef)
        startActivity(intent)
        this.finish()
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()

        if(playerType == "Player"){
            player()
        }
    }

    override fun onStop() {
        super.onStop()
        println("waiting onstop")
        adapter?.stopListening()
    }
    override fun onBackPressed() {
        val intent = Intent(this, QuizActivity::class.java)
        startActivity(intent)
        this.finish() //*
    }
}