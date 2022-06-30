package com.example.pictsmanager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://51.77.150.250:8080")
            .build()

        val service = retrofit.create(RequestService::class.java)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val passwordConfirm = findViewById<EditText>(R.id.password_confirm)
        val email = findViewById<EditText>(R.id.email)

        val btnReset = findViewById<Button>(R.id.btn_reset)
        val btnRegister = findViewById<Button>(R.id.btn_register)
        val responseLogin = findViewById<TextView>(R.id.response)

        //username.setText("richard.la@epitech.eu")
        //password.setText("toto")

        btnReset.setOnClickListener {
            username.setText("")
            password.setText("")
            passwordConfirm.setText("")
            email.setText("")
        }

        btnRegister.setOnClickListener {
            val user = username.text
            val passwd = password.text
            val passwdconfirm = passwordConfirm.text
            val emailvar = email.text

            var redirect = false

            if (passwdconfirm.toString() != passwd.toString()) {
                Toast.makeText(this, "passwords don't match", Toast.LENGTH_SHORT).show()
            } else {
                val params: HashMap<String?, RequestBody?> = HashMap()
                params["username"] = user.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                params["password"] =
                    passwd.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                params["email"] =
                    emailvar.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                CoroutineScope(Dispatchers.IO).launch {
                    val response = service.register(params)

                    Log.d(response.toString(), response.toString())
                    if (response.isSuccessful) {
                        responseLogin.text = response.toString()
                        redirect = true
                        //CoroutineScope(Dispatchers.IO).launch {
                    } else {
                        responseLogin.text = response.errorBody().toString()
                    }
                }
                if (redirect) {
                    //Toast.makeText(this, "account created", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        /*
        btn_debug.setOnClickListener {
            val intent = Intent(this@RegisterActivity, ProfileActivity::class.java)
            startActivity(intent)
        }
         */
    }

    override fun onBackPressed() {

    }
}