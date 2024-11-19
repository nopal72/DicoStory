package com.example.dicostory.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.dicostory.mainActivity.MainActivity
import com.example.dicostory.R
import com.example.dicostory.databinding.ActivityLoginBinding
import com.example.dicostory.ui.ViewModelFactory
import com.example.dicostory.ui.signup.RegisterActivity
import com.example.dicostory.data.Result
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()

        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            viewModel.login(email, password)
            viewModel.loginResult.observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        true.disableInputFields()
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        false.disableInputFields()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        false.disableInputFields()
                        binding.errorMessage.text = result.error
                        binding.errorMessage.visibility = View.VISIBLE
                        Snackbar.make(
                            binding.root,
                            result.error,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        binding.textSignup.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivBook, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val tagLine = ObjectAnimator.ofFloat(binding.tagLine, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.textEmail, View.ALPHA, 1f).setDuration(500)
        val emailLayout = ObjectAnimator.ofFloat(binding.emailLayout, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.textPassword, View.ALPHA, 1f).setDuration(500)
        val passwordLayout = ObjectAnimator.ofFloat(binding.passwordLayout, View.ALPHA, 1f).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val textSignup = ObjectAnimator.ofFloat(binding.textSignup, View.ALPHA, 1f).setDuration(500)
        val appName = ObjectAnimator.ofFloat(binding.appName, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(email, emailLayout, password, passwordLayout)
        }

        AnimatorSet().apply {
            playSequentially(tagLine,appName , together, btnLogin, textSignup)
            start()
        }
    }

    private fun Boolean.disableInputFields() {
        binding.btnLogin.isEnabled = !this
        binding.edLoginEmail.isEnabled = !this
        binding.edLoginPassword.isEnabled = !this
    }
}