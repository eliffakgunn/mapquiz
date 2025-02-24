package com.example.mapquiz

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class MultiPlayerActivity : AppCompatActivity(){
    private var btnBack: ImageButton? = null
    private var btnCreate: Button? = null
    private var btnJoin: Button? = null
    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var uid: String? = null
    private var resDocRef: DocumentReference? = null
    private var groupDocRef: DocumentReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_player)

        init()

        btnBack?.setOnClickListener{
            intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
            this.finish()
        }

        btnCreate?.setOnClickListener{
            createGroup()
        }

        btnJoin?.setOnClickListener{
            joinGroup()
        }
    }

    private fun init(){
        btnBack = findViewById(R.id.btnBack)
        btnCreate = findViewById(R.id.btnCreate)
        btnJoin = findViewById(R.id.btnJoin)
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        uid = mAuth?.currentUser?.uid!!
    }

    private fun createGroup(){
        db?.collection("Kullanıcılar")?.document(uid!!)
                ?.get()?.addOnSuccessListener { snapshot ->
                    if(snapshot != null){
                        val username = snapshot.getString("kullaniciAdi").toString()
                        val totalPuan = snapshot.getLong("puan").toString()

                        createFields(username, totalPuan)

                        intent = Intent(this, CategoryActivity::class.java)
                        intent!!.putExtra("Player", "Multi")
                        intent.putExtra("PlayerType", "Admin")
                        intent.putExtra("GroupID", groupDocRef?.id)
                        intent.putExtra("admin", uid!!)
                        intent.putExtra("resDocRef", resDocRef?.id)
                        startActivity(intent)
                        this.finish()
                    }
                    else{
                        Log.d("MultiPlayerActivity", "No such document")
                    }
                }
    }

    private fun createFields(username: String, totalPuan: String){
        //val user2 = Player(uid, username, 0, totalPuan.toInt())
        val user2 = PlayerMP(uid, username, 0, totalPuan.toInt(), "Devam Ediyor")

        resDocRef = db?.collection("Quiz Sonuçları")?.document()
        resDocRef?.collection("Oyuncular")?.add(user2)

        val resDocRef2 = db?.collection("Quiz Sonuçları")?.document()
        val junk = hashMapOf("junk" to "junk")
        resDocRef2?.set(junk)

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
        
        val user = hashMapOf(
                "startGame" to false,
                "extraPointsArr" to extraPoints,
                "resDocRef" to  resDocRef?.id,
                "resDocRef2" to  resDocRef2?.id
        )

        groupDocRef = db!!.collection("Gruplar").document()
        groupDocRef?.set(user)
    }

    private fun joinGroup(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_join_group)

        val dialogCancel = dialog.findViewById<TextView>(R.id.dialogCancel)
        val dialogConfirm = dialog.findViewById<TextView>(R.id.dialogConfirm)
        val dialogText = dialog.findViewById<TextView>(R.id.dialogText)

        dialog.show()

        dialogCancel?.setOnClickListener{
            dialog.dismiss()
        }

        dialogConfirm?.setOnClickListener {
            val groupID = dialogText.text.toString().trim { it <= ' ' }
            //dialog.dismiss()

            if (groupID.isNotEmpty()) {
                val groupDocRef = db?.collection("Gruplar")?.document(groupID)

                groupDocRef?.get()
                        ?.addOnSuccessListener {
                            if (it.exists()) {
                                val startGame = it.getBoolean("startGame")

                                if(!startGame!!){
                                    val resDocRef2 = it.getString("resDocRef")

                                    db?.collection("Kullanıcılar")?.document(uid!!)
                                            ?.get()?.addOnSuccessListener { snapshot ->
                                                if(snapshot != null){
                                                    val username = snapshot.getString("kullaniciAdi").toString()
                                                    val totalPoint = snapshot.getLong("puan").toString()

                                                    addPlayer(groupDocRef, resDocRef2!!, username, totalPoint)

                                                    dialog.dismiss()

                                                    intent = Intent(this, WaitingRoomActivity::class.java)
                                                    intent.putExtra("PlayerType", "Player")
                                                    intent.putExtra("GroupID", groupID)
                                                    intent!!.putExtra("Player", "Multi")
                                                    intent.putExtra("resDocRef", resDocRef2)
                                                    startActivity(intent)
                                                    this.finish()
                                                }
                                                else{
                                                    Log.d("MultiPlayerActivity", "No such document")
                                                }
                                            }
                                }
                                else{
                                    Toast.makeText(this, "Grup kodu geçersiz.", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                println("Document yok")
                                Toast.makeText(this, "Grup kodu geçersiz.", Toast.LENGTH_LONG).show()
                            }
                        }
                        ?.addOnFailureListener {
                            Log.e("MultiPlayerActivity", "Error occurred in groupDocRef listener")
                        }
            }else{
                Toast.makeText(this, "Lütfen grup kodunu giriniz.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun addPlayer(groupDocRef: DocumentReference, resDocRef: String, username: String, totalPoint: String){
        //val user2 = Player(uid, username, 0, totalPoint.toInt())
        val user2 = PlayerMP(uid, username, 0, totalPoint.toInt(), "Devam Ediyor")

        db?.collection("Quiz Sonuçları")
                ?.document(resDocRef)
                ?.collection("Oyuncular")?.add(user2)

        db?.runTransaction{transaction ->
            transaction.get(groupDocRef)

            for(i in 1..10){
                val field = "extraPointsArr.$i"
                transaction.update(groupDocRef, field,  FieldValue.increment(1))
            }
            
            null
        }?.addOnSuccessListener {
            Log.d("addPlayer", "Transaction success!")

        }
        ?.addOnFailureListener { e ->
            Log.w("addPlayer", "Transaction failure.", e)
        }
    }
}