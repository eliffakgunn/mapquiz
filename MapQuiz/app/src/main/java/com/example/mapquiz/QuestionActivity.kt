package com.example.mapquiz

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.mousebird.maply.VectorObject
import com.squareup.picasso.Picasso
import java.lang.NumberFormatException
import kotlin.collections.ArrayList

class QuestionActivity : AppCompatActivity(), QuestionFragment.OnUserDidSelectListener{
    private var txtUsername: TextView? = null
    private var txtTime: TextView? = null
    private var txtCurrQuestion: TextView? = null
    private var txtPoints: TextView? = null
    private var imgFlag: ImageView? = null
    private var txtOtherQs: TextView? = null
    private var txtFlagQ: TextView? = null
    private var questionFragment: QuestionFragment? = null
    private var mAuth: FirebaseAuth? = null
    private var firestore: FirebaseFirestore? = null
    private var askedQuestions: ArrayList<Int>? = null
    private var type: String? = null
    private var level: String? = null
    private var isGuest: String? = null
    private var answer: String? = null
    private var timer: CountDownTimer? = null
    private var rightAns: Int = 0
    private var wrongAns: Int = 0
    private var emptyQ: Int = 0
    private var points: Int = 0
    private var groupID: String? = null
    private var playerType: String? = null
    private var player: String? = null
    private var groupRef: DocumentReference? = null
    private var selectFlag: Boolean = true
    private var qInd : Int = 0
//    private var ind : Int = 1
    private var ind2 : Int = 0
    private var uid:  String? = null
    private var resDocRef: String? = null
    private var collectionRef: CollectionReference? = null
    private var currQuestion: Int = 0
    private var listener: ListenerRegistration? = null
    private var userDidSelect = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        type = intent.getStringExtra("Type")
        level = intent.getStringExtra("Level")
        player = intent.getStringExtra("Player") //multi, single
        isGuest = intent.getStringExtra("isGuest") //true, false

        initVars()

        if(player == "Multi") {
            this.groupID = intent.getStringExtra("GroupID")
            this.playerType = intent.getStringExtra("PlayerType") //player, admin
            this.resDocRef = intent.getStringExtra("resDocRef")
            this.groupRef = firestore!!.collection("Gruplar").document(groupID!!)
            this.collectionRef = firestore!!.collection("Quiz Sonuçları").document(resDocRef!!).collection("Oyuncular")
        }

        initScreen()

        if(type == "Bayrak"){
            imgFlag?.visibility = View.VISIBLE
            txtFlagQ?.visibility = View.VISIBLE
            txtOtherQs?.visibility = View.INVISIBLE
        }

        /*if(type == "Genel Kültür"){
            txtOtherQs?.text = "Müslümanların ilk kıblesi olan Mescid-i Aksa nerededir?"
        }*/

        Thread.sleep(1_000)

        showQuestion()
    }

    private fun startTimer(){
        var time: Long = 16

        timer = object:  CountDownTimer(time*1000, 1000) {
            override fun onTick(p0: Long){
                time -= 1
                txtTime?.text = time.toString()
            }
            override fun onFinish() {
                //emptyQ = emptyQ.plus(1)

                if(!userDidSelect) {
                    emptyQ = emptyQ.plus(1)
                    answer?.let { questionFragment?.drawRightCountry(it) }
                    questionFragment?.drawCountry()
                }

                showQuestion()
                /*if(player == "Single"){
                    showQuestion()
                }
                else{
                    /*if(!userDidSelect) {
                        answer?.let { questionFragment?.drawRightCountry(it) }
                        questionFragment?.drawCountry()
                    }*/
                    showQuestion()
                }*/
            }
        }

        timer?.start()
    }
    
    private fun initVars() {
        this.questionFragment = QuestionFragment()
        this.txtUsername = findViewById(R.id.txt_user_name)
        this.txtTime = findViewById(R.id.txt_time)
        this.txtCurrQuestion = findViewById(R.id.txt_q_num)
        this.txtPoints = findViewById(R.id.txt_points)
        this.imgFlag = findViewById(R.id.img_flag)
        this.txtOtherQs = findViewById(R.id.txt_otherQs)
        this.txtFlagQ = findViewById(R.id.txt_flagQ)
        askedQuestions = ArrayList()

        this.mAuth = FirebaseAuth.getInstance()
        this.firestore = FirebaseFirestore.getInstance()
        this.uid = FirebaseAuth.getInstance().currentUser?.uid
    }

    private fun initScreen(){
        supportFragmentManager.beginTransaction().replace(R.id.fragment, questionFragment!!).commit()
        txtCurrQuestion?.text = "0"

        if(mAuth?.currentUser?.uid != null) {
            firestore?.collection("Kullanıcılar")?.document(mAuth?.currentUser?.uid!!)
                    ?.get()?.addOnSuccessListener { snapshot ->
                        if (snapshot != null) {
                            txtUsername?.text = snapshot.getString("kullaniciAdi")
                            txtPoints?.text = snapshot.getLong("puan").toString()
                        } else {
                            Log.d("QuestionActivity", "No such document")
                        }
                    }
        }else{
            txtUsername?.text = "Misafir"
            txtPoints?.text = "0"
        }
    }

    private fun showQuestion(){
        //currQuestion = txtCurrQuestion?.text.toString().toInt()
        currQuestion += 1
        selectFlag = true
        userDidSelect = false

        println("showQuestion currQuestion = $currQuestion")

        if(currQuestion <= 10){
            println("showQuestion ife girdi")
            txtCurrQuestion?.text = currQuestion.toString()
            startTimer()

            if(player == "Single"){
                showQuestionHelper(type, level)
            }else{
                firestore?.collection(type!!)
                        ?.document(level!!)
                        ?.collection("Sorular")
                        ?.get()
                        ?.addOnSuccessListener { documents ->

                            firestore?.collection("Gruplar")?.document(groupID!!)
                                    ?.get()?.addOnSuccessListener { snapshot ->
                                        if (snapshot != null) {
                                            val questionArr = snapshot.get("questionArr") as ArrayList<Int>
                                            val ind = questionArr[qInd++]
                                            if(type != "Bayrak"){
                                                val question = documents.documents[ind].getString("Soru")
                                                txtOtherQs?.text = question
                                            }else{
                                                val url = documents.documents[ind].getString("url")
                                                FirebaseStorage.getInstance().getReferenceFromUrl(url!!)

                                                Picasso.get().load(url).into(imgFlag)
                                            }
                                            answer = documents.documents[ind].getString("Cevap")
                                        }
                                    }
                        }
                        ?.addOnFailureListener {
                            println("SORGU HATASI")
                        }
            }
        }
        else{
            println("showQuestion else girdi")

            if(player == "Single"){
                val intent = Intent(this, ScoreActivitySingle::class.java)
                intent.putExtra("rightAns", rightAns.toString())
                intent.putExtra("wrongAns", wrongAns.toString())
                intent.putExtra("emptyQ", emptyQ.toString())
                intent.putExtra("points", points.toString())
                intent.putExtra("totalPoint", txtPoints?.text.toString())
                intent.putExtra("Type", type)
                intent.putExtra("Level", level)
                intent.putExtra("isGuest", isGuest)
                startActivity(intent)
            }else{
                listener?.remove()
                timer?.cancel()

                collectionRef?.whereEqualTo("userId", uid)
                        ?.get()
                        ?.addOnSuccessListener { documents ->
                            val ref = documents.documents[0].reference
                            ref.update("status", "Bitti")
                        }

                val intent = Intent(this, ScoreActivityMulti::class.java)
                intent.putExtra("PlayerType", playerType)
                intent.putExtra("GroupID", groupID)
                intent.putExtra("Player", "Multi")
                intent.putExtra("Type", type)
                intent.putExtra("Level", level)
                intent.putExtra("totalPoint", txtPoints?.text.toString())
                intent.putExtra("resDocRef", resDocRef)
                intent.putExtra("username", txtUsername?.text?.toString())
                startActivity(intent)
            }
        }
    }

    private fun showQuestionHelper(type: String?, level: String?){
        firestore?.collection(type!!)
                ?.document(level!!)
                ?.collection("Sorular")
                ?.get()
                ?.addOnSuccessListener { documents ->
                    val num = documents?.size()?.minus(1)
                    var rand = (0..num!!).random()

                    var bool = askedQuestions?.contains(rand)
                    while (bool!!) {
                        rand = (0..num).random()
                        bool = askedQuestions?.contains(rand)
                    }
                    askedQuestions?.add(rand)

                    if(type != "Bayrak"){
                        val question = documents.documents[rand].getString("Soru")
                        txtOtherQs?.text = question
                    }else{
                        val url = documents.documents[rand].getString("url")
                        FirebaseStorage.getInstance().getReferenceFromUrl(url!!)

                        Picasso.get().load(url).into(imgFlag)
                    }
                    answer = documents.documents[rand].getString("Cevap")

                    /*val question = documents.documents[rand].get("Soru")
                    answer = documents.documents[rand].get("Cevap") as String?
                    txtOtherQs?.text = question as String?

                    val url = documents.documents[ind].getString("url")
                    FirebaseStorage.getInstance().getReferenceFromUrl(url!!)

                    Picasso.get().load(url).into(imgFlag)*/
                }
                ?.addOnFailureListener {
                    println("SORGU HATASI")
                }
    }

    override fun onUserDidSelect(country: String, vectorObject: VectorObject){
        var ans = true
        userDidSelect = true //*
        timer?.cancel() //*

        if(selectFlag){
            selectFlag = false

            questionFragment?.drawCountry(Color.BLACK, answer!!, vectorObject)
            Thread.sleep(2_000)

            val countryTR = Translate().translate(country)

            if(player == "Single")
                timer?.cancel()

            if(answer == countryTR){
                questionFragment?.drawCountry(Color.GREEN, answer!!, vectorObject)
                rightAns = rightAns.plus(1)

                if(player == "Single"){
                    points += 10

                    val newPoints = txtPoints?.text.toString().toInt() + 10
                    txtPoints?.text = newPoints.toString()
                }
            }
            else{
                questionFragment?.drawCountry(Color.RED, answer!!, vectorObject)
                wrongAns = wrongAns.plus(1)
                ans = false
            }

            questionFragment?.drawCountry()

            if(player == "Single")
                showQuestion()
            else{
                //userDidSelect = true;
                print("answe = $ans")
                if(ans)
                    incCounter()
                //++ind2
                showQuestion() //*
            }
        }
    }

    private fun incCounter(){
        var incFlag = false
        var p = "0"


        firestore?.runTransaction{transaction ->
            val snapshot = transaction.get(groupRef!!)
            //val field = "extraPointsArr.${ind2}"
            val field = "extraPointsArr.${currQuestion}"
            val extraPoints = snapshot.getLong(field)
            incFlag = false

            println("++ currQuestion = $currQuestion extraPoints = $extraPoints")

            //if(ans) {
            if(currQuestion <= 10) {
                incFlag = true
                transaction.update(groupRef!!, field, FieldValue.increment(-1))
                p = extraPoints.toString()
            }
            //}


            null
        }?.addOnSuccessListener {
            Log.d("incCounter", "Transaction success!")


            if(incFlag){
                points += (10 + p.toInt())
                val newPoints = txtPoints?.text.toString().toInt() + 10 + p.toInt()
                txtPoints?.text = newPoints.toString()

                println("-- currQuestion = $currQuestion p = $p txtPoints?.text = ${txtPoints?.text}")


            collectionRef?.whereEqualTo("userId", uid)
                        ?.get()
                        ?.addOnSuccessListener { documents ->
                            val ref = documents.documents[0].reference
                            ref.update("point", points)
                        }
            }
        }
        ?.addOnFailureListener { e ->
            Log.w("incCounter", "Transaction failure.", e)
        }
        //++ind2
    }

    override fun onBackPressed() {
        timer?.cancel()

        val intent = Intent(this, QuizActivity::class.java)
        startActivity(intent)
    }
}

