package com.example.appiot

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException

class RegisterActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicializar vistas
        usernameEditText = findViewById(R.id.username)
        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)

        // Configurar botón de registro
        val registerButton = findViewById<Button>(R.id.registerbt)
        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                val registerRequest = RegisterRequest(username, email, password)
                sendRegisterRequest(registerRequest)
            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    data class RegisterRequest(
        val name: String,
        val email: String,
        val password: String
    )

    private fun sendRegisterRequest(registerRequest: RegisterRequest) {
        val url = "https://backendiot.azurewebsites.net/api/register_user"
        val gson = GsonBuilder().create()
        val json = gson.toJson(registerRequest)
        val requestBody = RequestBody.create(MediaType.parse("application/json"), json)
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        // Redirigir al usuario a LoginActivity
                        startActivity(Intent(applicationContext, LoginActivity::class.java))
                        finish() // Finalizar RegisterActivity para que no pueda volver atrás con el botón de atrás
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Error en el registro", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Error de red", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
