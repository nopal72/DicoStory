package com.example.dicostory.ui.signup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.dicostory.R
import com.example.dicostory.data.pref.RegisterRequest
import com.example.dicostory.databinding.ActivityRegisterBinding
import com.example.dicostory.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerViewModel.registerResult.observe(this) { result ->
            if (result != null && !result.error) {
                startActivity(Intent(this, LoginActivity::class.java))
                showToast(getString(R.string.registration_successful))
                finish()
            } else {
                showToast(getString(R.string.registration_failed))
            }
        }

        binding.btnLogin.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            val verifyPassword = binding.edVerifyPassword.text.toString()

            if (password == verifyPassword) {
                val request = RegisterRequest(name, email, password)
                registerViewModel.registerUser(request)
            } else {
                showToast(getString(R.string.password_mismatch))
            }
        }

        binding.textSignup.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        registerViewModel.errorMessage.observe(this) { message ->
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
