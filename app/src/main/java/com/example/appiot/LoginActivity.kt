package com.example.appiot
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.appiot.R
import com.example.appiot.RegisterActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private lateinit var registerButton: Button
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        registerButton = findViewById(R.id.registerbt)
        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val loginButton = findViewById<Button>(R.id.loginbt)
        loginButton.setOnClickListener {
            val email = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val loginRequest = LoginRequest(email, password)
                sendLoginRequest(loginRequest)
            } else {
                Toast.makeText(this, "Por favor ingresa correo y contraseña", Toast.LENGTH_SHORT).show()
            }
        }
    }
    data class LoginRequest(val nombre: String, val contrasena: String)

    private fun sendLoginRequest(loginRequest: LoginRequest) {
        val url = "http://localhost:7030/api/authenticate_user"
        val gson = GsonBuilder().create()
        val json = gson.toJson(loginRequest)
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
                    val responseBody = response.body()?.string()
                    // Manejar la respuesta del servidor aquí según tus necesidades
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                        // Aquí puedes dirigir al usuario a la siguiente actividad
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
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
