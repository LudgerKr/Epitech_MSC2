package com.example.pictsmanager

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.pictsmanager.fragment.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private val cameraFragment = CameraFragment()
    private val galleryFragment = GalleryFragment()
    private val socialFragment = SocialFragment()
    private val settingsFragment = SettingsFragment()
    private val permissions = arrayOf(  Manifest.permission.CAMERA,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.READ_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(hasNoPermissions()) {
            requestPermission()
        }

        setContentView(R.layout.activity_main)

        replaceFragment(cameraFragment)

        bottom_navigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.camera -> replaceFragment(cameraFragment)
                R.id.gallery -> replaceFragment(galleryFragment)
                R.id.social -> replaceFragment(socialFragment)
                R.id.settings -> replaceFragment(settingsFragment)
            }
            true
        }
    }

    override fun onBackPressed() {

    }

    fun replaceFragment(fragment: Fragment) {
        updateNbFriendRequest()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

    private fun hasNoPermissions(): Boolean{
        return ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(this, permissions,0)
    }

    private fun updateNbFriendRequest(){
        val retrofit = Retrofit.Builder()
            .baseUrl("http://51.77.150.250:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(RequestService::class.java)

        val sharedPreferences = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE)

        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getFriendsRequest(
                sharedPreferences.getString("cookie", "").toString(),
                sharedPreferences.getString("id", "").toString()
            )

            withContext(Dispatchers.Main) {
                if(response.isSuccessful) {
                    if (response.body() != null && response.body()!!.size() > 0) {
                        bottom_navigation.getOrCreateBadge(R.id.social).number = response.body()!!.size()
                        val editPref =  sharedPreferences.edit()
                        editPref.putInt("friendRequest",response.body()!!.size())
                        editPref.apply()
                    }
                    else {
                        val editPref =  sharedPreferences.edit()
                        editPref.putInt("friendRequest",0)
                        editPref.apply()
                        bottom_navigation.removeBadge(R.id.social)
                    }
                }
                else {
                    Toast.makeText(
                        applicationContext,
                        "Error Occurred: ${response.message()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}