package com.example.mapquiz

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class LoginActivity : AppCompatActivity(), ResetPasswordDialog.ResetPasswordDialogListener{
    private var from: String? = null
    private var flag: Boolean = false
    private var txtReg: TextView? = null
    private var txtForgetPassword: TextView? = null
    private var btnClose: ImageButton? = null
    private var btnPsw: ImageButton? = null
    private var btnLogin: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        from = intent.getStringExtra("from")

        initView()

        login()
    }

    private fun initView(){
        txtReg = findViewById(R.id.txt_reg)
        txtForgetPassword = findViewById(R.id.txt_forget_password)
        btnClose = findViewById(R.id.btn_close)
        btnPsw = findViewById(R.id.btn_psw)
        btnLogin = findViewById(R.id.btn_login)
    }

    private fun login(){
        txtReg?.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            intent.putExtra("from", from)
            startActivity(intent)
            this.finish()
        }

        txtForgetPassword?.setOnClickListener {
            val dialog = ResetPasswordDialog()
            dialog.show(supportFragmentManager, "ResetPasswordDialog")
        }

        btnClose?.setOnClickListener{
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
            this.finish()
        }

        btnPsw?.setOnClickListener{
            if(flag == false) {
                findViewById<TextView>(R.id.e_txt_pass).transformationMethod = HideReturnsTransformationMethod.getInstance()
                flag = true
            }
            else{
                findViewById<TextView>(R.id.e_txt_pass).transformationMethod = PasswordTransformationMethod.getInstance()
                flag = false
            }
        }

        btnLogin?.setOnClickListener{
            when {
                TextUtils.isEmpty(findViewById<TextView>(R.id.e_txt_username).text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(this, "Lütfen kullanıcı adınızı giriniz.", Toast.LENGTH_SHORT).show()
                }

                TextUtils.isEmpty(findViewById<TextView>(R.id.e_txt_pass).text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(this, "Lütfen şifrenizi giriniz.", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    val username: String =
                            findViewById<EditText>(R.id.e_txt_username).text.toString().trim { it <= ' ' }
                    val password: String =
                            findViewById<EditText>(R.id.e_txt_pass).text.toString().trim { it <= ' ' }

                    FirebaseFirestore.getInstance().collection("Kullanıcılar")
                            .whereEqualTo("kullaniciAdi", username)
                            .get()
                            .addOnSuccessListener { documents ->
                                if (documents.size() != 0){
                                    val email = documents.documents[0].get("email")
                                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email as String, password)
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    task.result!!.user!!
                                                    val intent = Intent(this, QuizActivity::class.java)
                                                    startActivity(intent)
                                                    this.finish()
                                                } else {
                                                    Toast.makeText(
                                                            this,
                                                            "Giriş işlemi başarısız.",
                                                            Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                            .addOnFailureListener {
                                                Log.e("LoginActivity", "Login failed.")
                                            }
                                }
                                else{
                                    Toast.makeText(
                                            this,
                                            "Kullanıcı bulunamadı.",
                                            Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            .addOnFailureListener {
                                Log.e("LoginActivity", "User name checking is failed.")
                            }
                }
            }
        }
    }

    override fun onResetPassword(email: String) {
        val auth = FirebaseAuth.getInstance()

        try {
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("LoginActivity", "Email sent.")
                            Toast.makeText(this, "Mail gönderildi.", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this, "Eksik veya hatalı bilgi girdiniz.", Toast.LENGTH_SHORT).show()
                        }
                    }
        }catch (e: Exception){
            println("Error sendPasswordResetEmail: ${e.toString()}")
        }
    }


}