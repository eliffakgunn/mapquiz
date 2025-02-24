package com.example.mapquiz

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class QuizHomeFragment:Fragment(){
    private var btnLeft: ImageButton? = null
    private var btnPlay: Button? = null
    private var btnMulti: Button? = null
    private var txtUsername: TextView? = null
    private var txtPoints: TextView? = null
    private var imgCoin: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container,false)

        initView(view)
        home()

        return view
    }

    private fun initView(view: View){
        this.btnLeft = view.findViewById(R.id.button_left)
        this.btnPlay = view.findViewById(R.id.btn_play)
        this.btnMulti = view.findViewById(R.id.btn_multiplayer)
        this.txtUsername = view.findViewById(R.id.txt_username)
        this.txtPoints = view.findViewById(R.id.txt_points)
        this.imgCoin = view.findViewById(R.id.img_coin)
    }

    @SuppressLint("SetTextI18n")
    private fun home(){
        if(FirebaseAuth.getInstance().currentUser != null){
            FirebaseFirestore.getInstance()
                .collection("Kullanıcılar")
                .document(FirebaseAuth.getInstance().currentUser?.uid!!)
                .get().addOnSuccessListener { snapshot ->
                    if(snapshot != null){
                        txtUsername?.text = snapshot.getString("kullaniciAdi")
                        val points = snapshot.getLong("puan")
                        txtPoints?.text = points.toString() + " puan"
                    } else{
                        Log.d("QuizHomeFragment", "home: No such document")
                    }
                }
        }else{
            txtUsername?.text = "Misafir"
            txtPoints?.visibility = View.INVISIBLE
            imgCoin?.visibility = View.INVISIBLE
        }

        btnLeft?.setOnClickListener {
            val i = Intent(activity, MainActivity::class.java)
            startActivity(i)
            (activity as Activity?)!!.overridePendingTransition(0, 0)
        }

        btnPlay?.setOnClickListener {
            val i = Intent(activity, CategoryActivity::class.java)
            i.putExtra("Player", "Single")
            if(FirebaseAuth.getInstance().currentUser == null)
                i.putExtra("isGuest", "true")
            startActivity(i)
            (activity as Activity?)!!.overridePendingTransition(0, 0)
        }

        btnMulti?.setOnClickListener {
            if(FirebaseAuth.getInstance().currentUser != null){

                val i = Intent(activity, MultiPlayerActivity::class.java)
                startActivity(i)
                (activity as Activity?)!!.overridePendingTransition(0, 0)
            }
            else{
                Toast.makeText(activity, "Grup ile oynamak için giriş yapmalısınız.", Toast.LENGTH_LONG).show()

                val i = Intent(activity, LoginActivity::class.java)
                i.putExtra("from", "QuizHomeFragment")
                startActivity(i)
                (activity as Activity?)!!.overridePendingTransition(0, 0)
            }
        }
    }
}