package com.example.dicostory.ui.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.dicostory.R
import com.example.dicostory.data.Result
import com.example.dicostory.data.pref.RegisterRequest
import com.example.dicostory.databinding.ActivityRegisterBinding
import com.example.dicostory.ui.ViewModelFactory
import com.example.dicostory.ui.login.LoginActivity
import com.google.android.material.snackbar.Snackbar

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()

        binding.btnLogin.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            val verifyPassword = binding.edVerifyPassword.text.toString()

            if (password == verifyPassword) {
                val request = RegisterRequest(name, email, password)
                registerViewModel.register(request).observe(this) { result ->
                    when (result) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            true.disableInputFields()
                        }
                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE

                            startActivity(Intent(this, LoginActivity::class.java))
                            showToast(getString(R.string.registration_successful))
                            finish()
                            false.disableInputFields()
                        }
                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.errorMessage.visibility = View.VISIBLE
                            binding.errorMessage.text = result.error
                            Snackbar.make(
                                binding.root,
                                result.error,
                                Snackbar.LENGTH_SHORT
                            ).show()
                            false.disableInputFields()
                        }
                    }
                }
            } else {
                showToast(getString(R.string.password_mismatch))
            }
        }

        binding.textSignup.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

    }

    private fun playAnimation() {
        val textRegistration = ObjectAnimator.ofFloat(binding.textRegistration, View.ALPHA, 1f).setDuration(500)
        val name = ObjectAnimator.ofFloat(binding.textName, View.ALPHA, 1f).setDuration(500)
        val nameLayout = ObjectAnimator.ofFloat(binding.nameLayout, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.textEmail, View.ALPHA, 1f).setDuration(500)
        val emailLayout = ObjectAnimator.ofFloat(binding.emailLayout, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.textPassword, View.ALPHA, 1f).setDuration(500)
        val passwordLayout = ObjectAnimator.ofFloat(binding.passwordLayout, View.ALPHA, 1f).setDuration(500)
        val verifyPassword = ObjectAnimator.ofFloat(binding.verifyPasswordLayout, View.ALPHA, 1f).setDuration(500)
        val verifyPasswordLayout = ObjectAnimator.ofFloat(binding.verifyPasswordLayout, View.ALPHA, 1f).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val textLogin = ObjectAnimator.ofFloat(binding.textSignup, View.ALPHA, 1f).setDuration(500)

        val togetherName = AnimatorSet().apply {
            playTogether(name, nameLayout)
        }
        val togetherEmail = AnimatorSet().apply {
            playTogether(email, emailLayout)
        }
        val togetherPassword = AnimatorSet().apply {
            playTogether(password, passwordLayout,verifyPasswordLayout,verifyPassword)
        }
        AnimatorSet().apply {
            playSequentially(textRegistration, togetherName, togetherEmail, togetherPassword, btnLogin, textLogin)
            start()
        }

    }

    private fun Boolean.disableInputFields() {
        binding.btnLogin.isEnabled = !this
        binding.edRegisterName.isEnabled = !this
        binding.edRegisterEmail.isEnabled = !this
        binding.edRegisterPassword.isEnabled = !this
        binding.edVerifyPassword.isEnabled = !this
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
