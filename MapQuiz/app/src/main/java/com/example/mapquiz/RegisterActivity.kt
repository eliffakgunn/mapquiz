package com.example.mapquiz

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private var from: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        
        this.from = intent.getStringExtra("from")

        register()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun register(){
        val btnReg = findViewById<Button>(R.id.btn_reg)
        btnReg.setOnClickListener{
            when {
                TextUtils.isEmpty(findViewById<EditText>(R.id.e_txt_username).text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(this, "Lütfen kullanıcı adınızı giriniz.", Toast.LENGTH_SHORT).show()
                }

                TextUtils.isEmpty(findViewById<EditText>(R.id.e_txt_mail).text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(this, "Lütfen mail adresinizi giriniz.", Toast.LENGTH_SHORT).show()
                }

                TextUtils.isEmpty(findViewById<EditText>(R.id.e_txt_pass).text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(this, "Lütfen şifrenizi giriniz.", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    val username : String = findViewById<EditText>(R.id.e_txt_username).text.toString().trim { it <= ' ' }
                    val email: String = findViewById<EditText>(R.id.e_txt_mail).text.toString().trim { it <= ' ' }
                    val password: String = findViewById<EditText>(R.id.e_txt_pass).text.toString().trim { it <= ' ' }

                    FirebaseFirestore.getInstance().collection("Kullanıcılar")
                            .whereEqualTo("kullaniciAdi", username)
                            .get()
                            .addOnSuccessListener { documents ->
                                if(documents.size() == 0) {
                                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    task.result!!.user!!

                                                    val userID = FirebaseAuth.getInstance().currentUser.uid
                                                    val user = hashMapOf(
                                                            "kullaniciAdi" to username,
                                                            "email" to email,
                                                            "puan" to 0
                                                    )
                                                    FirebaseFirestore.getInstance()
                                                            .collection("Kullanıcılar")
                                                            .document(userID)
                                                            .set(user)

                                                    Toast.makeText(this, "Kaydolma işlemi başarılı.", Toast.LENGTH_SHORT).show()

                                                    val intent = Intent(this, LoginActivity::class.java)
                                                    intent.putExtra("from", from)
                                                    startActivity(intent)
                                                    this.finish()
                                                } else {
                                                    Toast.makeText(this, "Kaydolma işlemi başarısız.", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                }
                                else
                                    Toast.makeText(this, "Bu kullanıcı adı kullanılıyor.", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Log.e("RegisterActivity", "Failure listener.")
                            }
                }
            }
        }

        val btnClose = findViewById<ImageButton>(R.id.btn_close)
        btnClose.setOnClickListener{
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
            this.finish()
        }

        val txtLogin = findViewById<TextView>(R.id.txt_login)
        txtLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("from", from)
            startActivity(intent)
            this.finish()

        }

        val right = 2
        var flag = false

        findViewById<EditText>(R.id.e_txt_pass).also {
            it.setOnTouchListener { v, event ->
                when (event?.action) {
                    MotionEvent.ACTION_UP -> //Do Something
                        if(event.rawX >= (it.right- it.compoundDrawables[right].bounds.width())){
                            if(!flag) {
                                findViewById<TextView>(R.id.e_txt_pass).transformationMethod = HideReturnsTransformationMethod.getInstance()
                                flag = true
                            } else{
                                findViewById<TextView>(R.id.e_txt_pass).transformationMethod = PasswordTransformationMethod.getInstance()
                                flag = false
                            }
                        }
                }

                v?.onTouchEvent(event) ?: true
            }
        }
    }
}