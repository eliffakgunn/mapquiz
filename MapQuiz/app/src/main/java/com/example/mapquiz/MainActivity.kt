package com.example.mapquiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query


class   MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main()


        /*FirebaseFirestore.getInstance().collection("Gruplar").document("w83ECnJSRZXdrWSeQACL").delete()
                .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w("TAG", "Error deleting document", e) }*/

        /*val user = hashMapOf(
                "puan" to 15,
                "username" to "eliffakgunn"
        )

        val city = hashMapOf(
                "151024016" to user
        )

        val oyuncular = hashMapOf("oyuncular" to city)

        FirebaseFirestore.getInstance().collection("cities").document("LA")
                .set(oyuncular)
                .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }*/

        // Assume the document contains:
// {
//   name: "Frank",
//   favorites: { food: "Pizza", color: "Blue", subject: "recess" }
//   age: 12
// }
//
// To update age and favorite color:
        /*FirebaseFirestore.getInstance().collection("cities").document("LA")
                .update(mapOf("oyuncular.151024016.puan" to 25))*/

        /*val user2 = hashMapOf(
                "puan" to 15,
                "username" to "esraaakgunn"
        )

        val city2 = hashMapOf(
                "1801042251" to user2
        )

        val oyuncular2 = hashMapOf("oyuncular" to city2)*/

        /*FirebaseFirestore.getInstance().collection("cities").document("LA")
                .update(mapOf("oyuncular.1801042251" to user2))*/ //yeni oyuncu ekleme

        /*FirebaseFirestore.getInstance().collection("cities")
                .document("LA")
                .get()
                .addOnSuccessListener {snapshot->
                    val map1 = snapshot.get("oyuncular.1801042251.username") as String
                    println("map1 = $map1")
                }*/ //veri okuma



        /*FirebaseFirestore.getInstance().collection("Sorular")
                .whereEqualTo("Seviye", "Kolay")
                .get()
                .addOnSuccessListener { documents ->
                    println("*****")
                    for (document in documents) {
                        println("+++")
                        println("-- ${document.get("Soru")}")
                    }
                }
                .addOnFailureListener { exception ->
                    println("SORGU HATASI")
                }*/

        /*FirebaseFirestore.getInstance().collection("Gruplar").document("GunTMkU5pA18Cuhu5osz")
                .get().addOnSuccessListener { snapshot ->
                    if (snapshot != null) {
                        val playerArr = snapshot.get("playerArr") as ArrayList<HashMap<String,Any>>


                        println("playerArr[0][uid] =" + playerArr[0]["uid"])
                    }
                }*/
    }

    private fun main(){
        val btnQuiz = findViewById<Button>(R.id.btn_quiz)
        val btnGlobe = findViewById<Button>(R.id.btn_kesfet)

        btnQuiz.setOnClickListener{
            val intent = Intent(this, QuizActivity::class.java)
            //val intent = Intent(this, ScoreActivityMulti::class.java)
            startActivity(intent)
            this.finish()
        }

        btnGlobe.setOnClickListener{
            val intent = Intent(this, GlobeActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }

    /*override fun onStart() {
        super.onStart()

        //FirebaseFirestore.getInstance().collection("Gruplar").document("GunTMkU5pA18Cuhu5osz")?.addSnapshotListener(this) { value, error ->
        FirebaseFirestore.getInstance().collection("Gruplar").document("GunTMkU5pA18Cuhu5osz").collection("Oyuncular").addSnapshotListener {  snapshots, e ->
            if (e != null) {
                Log.d("WaitingRoomActivity", e.toString())
            }

            println("değişti")

        }
    }*/

}

/*sorgu
    val db = FirebaseFirestore.getInstance()
        db.collection("Ülke")
                .document("Kolay")
                .collection("Sorular")
                .get()
                .addOnSuccessListener { documents ->
                    println("*****")
                    for (document in documents) {
                        println("+++")
                        println("-- ${document.get("Soru")}")
                    }
                }
                .addOnFailureListener { exception ->
                    println("SORGU HATASI")
                }

        db.collection("Bilgiler")
        .whereEqualTo("ülke", "Türkiye")
        .get()
        .addOnSuccessListener { documents ->
            println("//*****")
            for (document in documents) {
                println("//+++")
                println("Bilgi: ${document.get("bilgi")}")
            }
        }
        .addOnFailureListener { exception ->
            println("//SORGU HATASI")
        }

        db.collection("Bilgiler")
                .document("Türkiye")
                .collection("Türkiye")
                .get()
                .addOnSuccessListener { documents ->
                    println("*****---------")
                    for (document in documents) {
                        println("+++--------")
                        println("BİLGİİİİ: ${document.get("bilgi")}")
                    }
                }
                .addOnFailureListener { exception ->
                    println("SORGU HATASI")
                }

 */