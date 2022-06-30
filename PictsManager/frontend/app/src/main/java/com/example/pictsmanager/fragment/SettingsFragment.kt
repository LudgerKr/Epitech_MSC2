package com.example.pictsmanager.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NavUtils.navigateUpTo
import androidx.fragment.app.Fragment
import com.example.pictsmanager.LoginActivity
import com.example.pictsmanager.R
import com.example.pictsmanager.RequestService
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireContext().getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://51.77.150.250:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(RequestService::class.java)

        var apiUsername = ""
        var apiEmail = ""

        fun populateUserInfo() {
            var error = false
            CoroutineScope(Dispatchers.IO).launch {
                val response = service.getUserInfo(
                    sharedPreferences.getString("cookie", "").toString(),
                    sharedPreferences.getString("id", "").toString()
                )
                //Log.d("ok", response.body()?.asJsonObject?.get("id").toString())
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val u = response.body()?.asJsonObject?.get("username").toString()
                        val e = response.body()?.asJsonObject?.get("email").toString()
                        apiUsername = u.substring(1, u.length - 1)
                        apiEmail = e.substring(1, e.length - 1)
                        username.setText(apiUsername)
                        email.setText(apiEmail)
                    } else {
                        error = true
                    }
                }
            }; if (error) Toast.makeText(requireContext(), "error loading your info", Toast.LENGTH_SHORT).show()
        }
        populateUserInfo()

        btn_update.setOnClickListener {
            var changes = false
            var success = false

            val params: HashMap<String?, RequestBody?> = HashMap()

            if (username.text.toString() != apiUsername) {
                params["username"] =
                    username.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                changes = true
            }
            if (email.text.toString() != apiEmail) {
                params["email"] =
                    email.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                changes = true
            }
            if (password.text.toString() == password_confirm.text.toString() && "" != password.text.toString()) {
                params["password"] =
                    password.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                changes = true
            } else if (password.text.toString() != "" && password_confirm.text.toString() != "") {
                Toast.makeText(requireContext(), "passwords don't match", Toast.LENGTH_SHORT).show()
            }

            if (changes) {
                CoroutineScope(Dispatchers.IO).launch {
                    val response = service.update(
                        sharedPreferences.getString("cookie", "").toString(),
                        sharedPreferences.getString("id", "").toString(),
                        params
                    )
                    if (response.isSuccessful) {
                        Log.d("is success", response.isSuccessful.toString())
                        populateUserInfo()
                        success = true
                    }
                }
                // Log.d("b", b.toString())
                if (success) Toast.makeText(requireContext(), "user info updated", Toast.LENGTH_SHORT)
                    .show()
            } else Toast.makeText(requireContext(), "no changes", Toast.LENGTH_SHORT).show()
        }

        btn_logout.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val response = service.logout(
                    sharedPreferences.getString("cookie", "").toString()
                )
                if (response.isSuccessful) {
                    Log.d("ok", "logged out")
                    val editPref = sharedPreferences.edit()
                    editPref.putString("cookie", "")
                    editPref.putString("id", "")
                    editPref.apply()
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}