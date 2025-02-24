package com.example.mapquiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class ScoreActivityMulti : AppCompatActivity() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var collectionRef: CollectionReference? = null
    private var adapter: PlayerAdapterScore? = null
    private var groupID: String? = null
    private var level: String? = null
    private var type: String? = null
    private var playerType: String? = null
    private var totalPoint: String? = null
    private var btnReplay: ImageButton? = null
    private var btnHome: ImageButton? = null
    private var resDocRef: String? = null
    private var username: String? = null
    private var groupRef: DocumentReference? = null
    private var resDocReference: DocumentReference? = null
    private var uid:  String? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score_multi)

        init()
        setUpRecyclerView()
        setScore()

        btnHome?.setOnClickListener{
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
            this.finish()
        }

        btnReplay?.setOnClickListener{
            replay()
        }
    }

    private fun init(){
        this.playerType = intent.getStringExtra("PlayerType") //player, admin
        this.groupID = intent.getStringExtra("GroupID")
        this.level = intent.getStringExtra("Level")
        this.type = intent.getStringExtra("Type")
        this.totalPoint = intent.getStringExtra("totalPoint")
        this.resDocRef = intent.getStringExtra("resDocRef")
        this.username = intent.getStringExtra("username")
        this.groupRef = db.collection("Gruplar").document(groupID!!)
        this.mAuth = FirebaseAuth.getInstance()
        uid = mAuth?.currentUser?.uid!!

        this.resDocReference = db.collection("Quiz Sonuçları").document(resDocRef!!)
        this.collectionRef = resDocReference?.collection("Oyuncular")
        this.btnReplay = findViewById(R.id.btn_replay)
        this.btnHome = findViewById(R.id.btn_home)
    }

    private fun setUpRecyclerView() {
        val query = collectionRef?.orderBy("point", Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<PlayerMP>()
                .setQuery(query!!, PlayerMP::class.java)
                .build()

        adapter = PlayerAdapterScore(options)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setScore(){
        val mAuth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        if(mAuth.currentUser?.uid != null) {
            db.collection("Kullanıcılar").document(mAuth.currentUser?.uid!!)
                    .get().addOnSuccessListener { snapshot ->
                        if (snapshot != null) {
                            db.collection("Kullanıcılar").document(mAuth.currentUser?.uid!!)
                                    .update("puan", totalPoint?.toInt())
                                    .addOnSuccessListener { Log.d("ScoreActivityMulti", "Point successfully updated!") }
                                    .addOnFailureListener { e -> Log.w("ScoreActivityMulti", "Error updating point", e) }
                        } else {
                            Log.d("ScoreActivity", "No such document")
                        }
                    }
        }
    }

    private fun replay(){
        var admin = false

        db.runTransaction{transaction ->
            val snapshot = transaction.get(groupRef!!)
            val startGame = snapshot.getBoolean("startGame")
            val resDocRef2 = snapshot.getString("resDocRef2")

            admin = false

            if(startGame!!){
                admin = true
                transaction.update(groupRef!!, "startGame",  false)
                transaction.update(groupRef!!, "resDocRef",  resDocRef2)
            }

            null
        }.addOnSuccessListener {
            Log.d("replay", "Transaction success!")

            if(admin)
                admin()
            else
                player()

        }
        .addOnFailureListener { e ->
            Log.w("replay", "Transaction failure.", e)
        }
    }

    private fun admin(){
        groupRef?.get()?.addOnSuccessListener { snapshot ->
            if (snapshot != null) {
                val resDocRef = snapshot.getString("resDocRef")
                //val user2 = Player(uid, username, 0, totalPoint?.toInt())
                val user2 = PlayerMP(uid, username, 0, totalPoint?.toInt(), "Devam Ediyor")
                db.collection("Quiz Sonuçları")
                        .document(resDocRef!!)
                        .collection("Oyuncular")
                        .add(user2)

                val extraPoints = hashMapOf(
                        "1" to 1,
                        "2" to 1,
                        "3" to 1,
                        "4" to 1,
                        "5" to 1,
                        "6" to 1,
                        "7" to 1,
                        "8" to 1,
                        "9" to 1,
                        "10" to 1
                )

                val resDocRef3 = db.collection("Quiz Sonuçları").document()
                val junk = hashMapOf("junk" to "junk")
                resDocRef3.set(junk)

                val user = hashMapOf(
                        "startGame" to false,
                        "extraPointsArr" to extraPoints,
                        "resDocRef" to  resDocRef,
                        "resDocRef2" to  resDocRef3.id
                )

                groupRef?.set(user)

                intent = Intent(this, WaitingRoomActivity::class.java)
                intent!!.putExtra("Player", "Multi")
                intent.putExtra("PlayerType", "Admin")
                intent.putExtra("GroupID", groupRef?.id)
                intent.putExtra("admin", uid!!)
                intent.putExtra("Type", type)
                intent.putExtra("Level", level)
                intent.putExtra("resDocRef", resDocRef)
                startActivity(intent)
                this.finish()
            }
        }
    }

    private fun player(){
        groupRef?.get()?.addOnSuccessListener { snapshot ->
            if (snapshot != null) {
                val resDocRef = snapshot.getString("resDocRef")
                //val user2 = Player(uid, username, 0, totalPoint?.toInt())
                val user2 = PlayerMP(uid, username, 0, totalPoint?.toInt(), "Devam Ediyor")
                db.collection("Quiz Sonuçları")
                        .document(resDocRef!!)
                        .collection("Oyuncular")
                        .add(user2)

                db.runTransaction{transaction ->
                    for(i in 1..10){
                        val field = "extraPointsArr.$i"
                        transaction.update(groupRef!!, field,  FieldValue.increment(1))
                    }

                    null
                }.addOnSuccessListener {
                    Log.d("player", "Transaction success!")

                }
                .addOnFailureListener { e ->
                    Log.w("player", "Transaction failure.", e)
                }

                intent = Intent(this, WaitingRoomActivity::class.java)
                intent.putExtra("PlayerType", "Player")
                intent.putExtra("GroupID", groupID)
                intent!!.putExtra("Player", "Multi")
                intent.putExtra("resDocRef", resDocRef)
                startActivity(intent)
                this.finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
    }

    override fun onBackPressed() {
        val intent = Intent(this, QuizActivity::class.java)
        startActivity(intent)
        this.finish() //*
    }
}