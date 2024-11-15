package com.example.dicostory.ui.signup

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

        binding.btnLogin.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            val verifyPassword = binding.edVerifyPassword.text.toString()

            if (password == verifyPassword) {
                val request = RegisterRequest(name, email, password)
                registerViewModel.register(request)
            } else {
                showToast(getString(R.string.password_mismatch))
            }

            val request = RegisterRequest(name, email, password)

            registerViewModel.register(request).observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        startActivity(Intent(this, LoginActivity::class.java))
                        showToast(getString(R.string.registration_successful))
                        finish()
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Snackbar.make(
                            binding.root,
                            "Terjadi kesalahan" + result.error,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        binding.textSignup.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
