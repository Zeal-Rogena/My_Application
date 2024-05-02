package com.example.myapplication

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.animation.AnticipateOvershootInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.databinding.ActivityMain2Binding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity3 : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMain2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        val signUpButton = binding.signup
        val signUpAnimator = ObjectAnimator.ofFloat(signUpButton, "translationY", 100f, 0f)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(signUpAnimator)
        animatorSet.duration = 2000
        animatorSet.interpolator = AnticipateOvershootInterpolator()
        animatorSet.start()

        auth = Firebase.auth

        binding.signup.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            if (checkAllFields()) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val jay = Intent(this@MainActivity3, MainActivity4::class.java)
                        startActivity(jay)
                        finish()
                    } else {
                        Log.e("error", it.exception.toString())
                    }
                }
            }
        }
    }
    private fun checkAllFields():Boolean{
        val email = binding.email.text.toString()
        if (binding.email.text.toString() == ""){
            binding.emailayout.error = "Please input your email"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailayout.error = "Please input  using the right email format"
            return false
        }
        if (binding.password.text.toString() != binding.confirmpassword.text.toString()){
            binding.passwordlayout.error = "Passwords do not match"
        }
        return true
    }
}

