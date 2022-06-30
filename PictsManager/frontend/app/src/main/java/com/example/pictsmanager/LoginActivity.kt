package com.example.pictsmanager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val sharedPreferences = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://51.77.150.250:8080")
            .build()

        val service = retrofit.create(RequestService::class.java)

        username.setText("zhigang.peng@epitech.eu")
        password.setText("toto")

        btn_reset.setOnClickListener {
            username.setText("")
            password.setText("")
        }

        btn_register.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        btn_login.setOnClickListener {
            val user = username.text
            val passwd = password.text
            val params: HashMap<String?, RequestBody?> = HashMap()
            params["username"] = user.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            params["password"] = passwd.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            CoroutineScope(Dispatchers.IO).launch {
                val response = service.login(params)

                withContext(Dispatchers.Main) {
                    if(response.isSuccessful) {
                        val editPref =  sharedPreferences.edit()
                        editPref.putString("cookie",response.headers()["Set-Cookie"])
                        editPref.putString("id", response.body()?.string())
                        editPref.apply()

                        Log.d("USERCOOKIE", sharedPreferences.getString("cookie", "cookie") as String)
                        Log.d("USERID", sharedPreferences.getString("id", "cookie") as String)

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                    else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Error Occurred: ${response.message()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {

    }
}