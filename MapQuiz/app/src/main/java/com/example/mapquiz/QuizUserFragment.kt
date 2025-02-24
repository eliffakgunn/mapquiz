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
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class QuizUserFragment:Fragment(), ConfirmPasswordDialog.OnConfirmPasswordListener, ChangePasswordDialog.OnChangePasswordListener {
    private var view1 : View? = null
    private var mAuth: FirebaseAuth? = null
    private var firestore: FirebaseFirestore? = null
    private var txt_username: TextView? = null
    private var txt_points: TextView? = null
    private var e_txt_username: EditText? = null
    private var e_txt_mail: EditText? = null
    private var txt_change_pass: TextView? = null
    private var btn_login: Button? = null
    private var btn_logout: Button? = null
    private var btn_save: Button? = null
    private var imgCoin: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user, container,false)

        initView(view)

        setProfile()

        return view
    }

    private fun initView(view: View){
        this.view1 = view
        this.mAuth = FirebaseAuth.getInstance()
        this.firestore = FirebaseFirestore.getInstance()
        this.txt_username = view.findViewById(R.id.txt_username)
        this.txt_points = view.findViewById(R.id.txt_points)
        this.e_txt_username = view.findViewById(R.id.e_txt_username)
        this.e_txt_mail = view.findViewById(R.id.e_txt_mail)
        this.txt_change_pass = view.findViewById(R.id.txt_change_pass)
        this.btn_login = view.findViewById(R.id.btn_login)
        this.btn_logout = view.findViewById(R.id.btn_logout)
        this.btn_save = view.findViewById(R.id.btn_save)
        this.btn_logout = view.findViewById(R.id.btn_logout)
        this.imgCoin = view.findViewById(R.id.img_coin)
    }

    @SuppressLint("SetTextI18n")
    private fun setProfile(){
        if(mAuth?.currentUser?.uid != null){
            btn_login?.visibility = View.INVISIBLE
            
            firestore?.collection("Kullanıcılar")?.document(mAuth?.currentUser?.uid!!)
                    ?.get()?.addOnSuccessListener { snapshot ->
                        if(snapshot != null){
                            txt_username?.text = snapshot.getString("kullaniciAdi")
                            e_txt_username?.hint = snapshot.getString("kullaniciAdi")
                            e_txt_mail?.hint = snapshot.getString("email")
                            val points = snapshot.getLong("puan")
                            txt_points?.text = points.toString() + " puan"
                        }
                        else{
                            Log.d("QuizUserFragment", "No such document")
                        }
                    }

            btn_save?.setOnClickListener {
                val dialog = ConfirmPasswordDialog()
                fragmentManager?.let { it1 -> dialog.show(it1, "ConfirmPasswordDialog") }
                dialog.setTargetFragment(this, 1)
            }

            txt_change_pass?.setOnClickListener {
                val dialog = ChangePasswordDialog()
                fragmentManager?.let { it1 -> dialog.show(it1, "ChangePasswordDialog") }
                dialog.setTargetFragment(this, 1)
            }

            btn_logout?.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                val i = Intent(activity, QuizActivity::class.java)
                startActivity(i)
                (activity as Activity?)!!.overridePendingTransition(0, 0)
            }
        }
        else{
            e_txt_username?.visibility = View.INVISIBLE
            e_txt_mail?.visibility = View.INVISIBLE
            txt_change_pass?.visibility = View.INVISIBLE
            btn_logout?.visibility = View.INVISIBLE
            btn_save?.visibility = View.INVISIBLE

            txt_username?.text = "Misafir"
            txt_points?.visibility = View.INVISIBLE
            imgCoin?.visibility = View.INVISIBLE

            btn_login?.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                val i = Intent(activity, LoginActivity::class.java)
                i.putExtra("from", "QuizUserFragment")
                startActivity(i)
                (activity as Activity?)!!.overridePendingTransition(0, 0)
            }
        }
    }

    override fun onConfirmPassword(password: String) {
        Log.d("QuizUserFragment", "onConfirmPassword: get the password: $password")

        val credential = EmailAuthProvider
                .getCredential(mAuth?.currentUser?.email, password)

        mAuth?.currentUser?.reauthenticate(credential)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("QuizUserFragment", "User re-authenticated.")

                        if(e_txt_username?.text?.length != 0){
                            FirebaseFirestore.getInstance().collection("Kullanıcılar")
                                    .whereEqualTo("kullaniciAdi", e_txt_username?.text.toString())
                                    .get()
                                    .addOnSuccessListener { documents ->
                                        if (documents.size() == 0) {
                                            println("doc 0")

                                            if (e_txt_mail?.text?.length != 0) {
                                                mAuth?.fetchSignInMethodsForEmail(e_txt_mail!!.text.toString().trim { it <= ' ' })
                                                        ?.addOnSuccessListener { result ->
                                                            val signInMethods = result.signInMethods!!
                                                            if (signInMethods.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)) {
                                                                // User can sign in with email/password
                                                                Log.d("QuizUserFragment", "onComplete: that email is already in use.")
                                                                Toast.makeText(activity, "Bu mail adresi zaten kullanılıyor.", Toast.LENGTH_SHORT).show()
                                                            } else{
                                                                Log.d("QuizUserFragment", "onComplete: That email is available.")
                                                                //////////////////////the email is available so update it
                                                                mAuth!!.currentUser.updateEmail(e_txt_mail!!.text.toString())
                                                                        .addOnCompleteListener { task ->
                                                                            if (task.isSuccessful) {
                                                                                Log.d("QuizUserFragment", "User email address updated.")
                                                                            }else{
                                                                                Log.d("QuizUserFragment", "updateEmail: User email address didn't update")
                                                                            }
                                                                        }

                                                                val _username = e_txt_username?.text.toString()
                                                                val _email = e_txt_mail?.text.toString()

                                                                firestore?.collection("Kullanıcılar")?.document(mAuth?.currentUser?.uid!!)
                                                                        ?.update("email", e_txt_mail!!.text.toString())
                                                                        ?.addOnSuccessListener { Log.d("QuizUserFragment", "Email successfully updated!") }
                                                                        ?.addOnFailureListener { e -> Log.w("QuizUserFragment", "Error updating email", e)}

                                                                firestore?.collection("Kullanıcılar")?.document(mAuth?.currentUser?.uid!!)
                                                                        ?.update("kullaniciAdi", e_txt_username?.text.toString())
                                                                        ?.addOnSuccessListener { Log.d("QuizUserFragment", "Name successfully updated!") }
                                                                        ?.addOnFailureListener { e -> Log.w("QuizUserFragment", "Error updating name", e)}

                                                                Toast.makeText(activity, "Değişiklikler başarıyla kaydedildi.", Toast.LENGTH_SHORT).show()
                                                                e_txt_username?.text?.clear()
                                                                e_txt_mail?.text?.clear()

                                                                e_txt_username?.hint =  _username
                                                                e_txt_mail?.hint =  _email
                                                                txt_username?.text = _username
                                                            }
                                                        }
                                                        ?.addOnFailureListener { exception ->
                                                            Log.e("QuizUserFragment", "Error getting sign in methods for user", exception)
                                                        }
                                            }
                                            else{
                                                val _username = e_txt_username?.text.toString()

                                                firestore?.collection("Kullanıcılar")?.document(mAuth?.currentUser?.uid!!)
                                                        ?.update("kullaniciAdi", e_txt_username?.text.toString())
                                                        ?.addOnSuccessListener { Log.d("QuizUserFragment", "Name successfully updated!") }
                                                        ?.addOnFailureListener { e -> Log.w("QuizUserFragment", "Error updating name", e)}

                                                Toast.makeText(activity, "Kullanıcı adınız başarıyla kaydedildi.", Toast.LENGTH_SHORT).show()

                                                e_txt_username?.text?.clear()

                                                e_txt_username?.hint =  _username
                                                txt_username?.text = _username

                                            }
                                        }
                                        else{
                                            Toast.makeText(activity, "Bu kullanıcı adı zaten kullanılıyor.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                        }else if (e_txt_mail?.text?.length != 0) {
                                mAuth?.fetchSignInMethodsForEmail(e_txt_mail!!.text.toString().trim { it <= ' ' })
                                        ?.addOnSuccessListener { result ->
                                            val signInMethods = result.signInMethods!!
                                            if (signInMethods.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)) {
                                                // User can sign in with email/password
                                                Log.d("QuizUserFragment", "onComplete: that email is already in use.")
                                                Toast.makeText(activity, "Bu mail adresi zaten kullanılıyor.", Toast.LENGTH_SHORT).show()
                                            } else{
                                                Log.d("QuizUserFragment", "onComplete: That email is available.")
                                                //////////////////////the email is available so update it
                                                mAuth!!.currentUser.updateEmail(e_txt_mail!!.text.toString())
                                                        .addOnCompleteListener { task ->
                                                            if (task.isSuccessful) {
                                                                val _email = e_txt_mail?.text.toString()

                                                                Log.d("QuizUserFragment", "User email address updated.")
                                                                Toast.makeText(activity, "Email adresiniz güncellendi.", Toast.LENGTH_SHORT).show()
                                                                e_txt_mail?.text?.clear()
                                                                e_txt_mail?.hint =  _email
                                                            }else{
                                                                Toast.makeText(activity, "Email adresiniz güncellenemedi.", Toast.LENGTH_SHORT).show()
                                                                Log.d("QuizUserFragment", "updateEmail: User email address didn't update")

                                                            }
                                                        }



                                                firestore?.collection("Kullanıcılar")?.document(mAuth?.currentUser?.uid!!)
                                                        ?.update("email", e_txt_mail!!.text.toString())
                                                        ?.addOnSuccessListener { Log.d("QuizUserFragment", "Email successfully updated!") }
                                                        ?.addOnFailureListener { e -> Log.w("QuizUserFragment", "Error updating email", e)
                                                            }
                                            }
                                        }
                                        ?.addOnFailureListener { exception ->
                                            Log.e("QuizUserFragment", "Error getting sign in methods for user", exception)
                                        }
                        }

                    }else{
                        Log.d("QuizUserFragment", "onComplete: re-authentication failed.")
                        Toast.makeText(activity, "Şifre doğrulanamadı.", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    override fun onChangePassword(curr_password: String, new_password1: String, new_password2: String) {
        if(new_password1 == new_password2){

            val credential = EmailAuthProvider
                    .getCredential(mAuth?.currentUser?.email, curr_password)

            ///////////////////// Prompt the user to re-provide their sign-in credentials
            mAuth?.currentUser?.reauthenticate(credential)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.e("QuizUserFragment", "onChangePassword: Re-authentication is successful")

                            mAuth?.currentUser?.updatePassword(new_password1)
                                    ?.addOnCompleteListener { task ->
                                        if(task.isSuccessful){
                                            Toast.makeText(activity, "Şifreniz değiştirildi.", Toast.LENGTH_SHORT).show()
                                            mAuth!!.signOut()
                                            val i = Intent(activity, LoginActivity::class.java)
                                            i.putExtra("from", "QuizUserFragment")
                                            startActivity(i)
                                            (activity as Activity?)!!.overridePendingTransition(0, 0)
                                            //this.activity?.finish()

                                        }
                                    }

                        } else{
                            Log.e("QuizUserFragment", "onChangePassword: Re-authentication is failed.")
                            Toast.makeText(activity, "Şifrenizi yanlış girdiniz.", Toast.LENGTH_SHORT).show()

                        }
                    }
        }else{
            Toast.makeText(activity, "Şifreler uyuşmuyor.", Toast.LENGTH_SHORT).show()
        }
    }
}