package com.example.pictsmanager.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.pictsmanager.R
import com.example.pictsmanager.RequestService
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.fragment_camera.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream


class CameraFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listAlbums: HashMap<String, String> = HashMap()

        val noAlbum = arrayOf("You have no album")

        val retrofit = Retrofit.Builder()
            .baseUrl("http://51.77.150.250:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(RequestService::class.java)

        val sharedPreferences = requireContext().getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE)

        val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result : ActivityResult ->
                if(result.resultCode == Activity.RESULT_OK) {
                    val extras = result.data?.extras
                    val imgBitMap = extras?.get("data") as Bitmap
                    img_foto.setImageBitmap(imgBitMap)

                    val filename = getRandomString()

                    val file: File?

                    val bitmap = result.data?.extras?.get("data") as Bitmap

                    file = File(requireContext().cacheDir, "pictsmanagerphotos")
                    file.mkdirs()
                    val fos = FileOutputStream(file.path + "/$filename.png")
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                    fos.close()

                    val tmp = File(file, "$filename.png")

                    val requestBody: RequestBody = tmp.asRequestBody("*/*".toMediaTypeOrNull())

                    save_cam.setOnClickListener {

                        val albumName : String = if (new_album.isChecked) {
                            if(edit_album.text.toString().isBlank()) "default" else edit_album.text.toString()
                        } else {
                            if(spinner_album.selectedItem.toString().isBlank()) "default" else spinner_album.selectedItem.toString()
                        }

                        val fileName = if(edit_name.text.toString().isBlank()) getRandomString() else edit_name.text.toString()

                        val fileToUpload = MultipartBody.Part.createFormData("file", fileName, requestBody)

                        CoroutineScope(Dispatchers.IO).launch {
                            val response = service.uploadImage(
                                sharedPreferences.getString("cookie", "").toString(),
                                sharedPreferences.getString("id", "").toString(),
                                fileToUpload,
                                albumName.toRequestBody("text/plain".toMediaTypeOrNull())
                            )

                            withContext(Dispatchers.Main) {
                                if(!response.isSuccessful) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Error Occurred: ${response.message()}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                        save_panel_cam.visibility = View.INVISIBLE
                        btn_save_cam.visibility = View.VISIBLE
                        fab_camera.visibility = View.VISIBLE
                        edit_name.setText("")
                        edit_album.setText("")
                    }

                }
        }

        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getAlbums(
                sharedPreferences.getString("cookie", "").toString(),
                sharedPreferences.getString("id", "").toString()
            )

            withContext(Dispatchers.Main) {
                if(response.isSuccessful) {
                    if (response.body() != null && response.body()!!.size() > 0) {
                        val albums = ArrayList<String>()
                        for (album : JsonElement in response.body()!!) {
                            albums.add(album.asJsonObject.get("name").asString)
                            listAlbums[album.asJsonObject.get("name").asString] = album.asJsonObject.get("id").asString
                        }
                        val albumAdapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            albums
                        )
                        spinner_album.adapter = albumAdapter
                    }
                    else {
                        val albumAdapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            noAlbum
                        )
                        spinner_album.adapter = albumAdapter
                    }
                }
                else {
                    Toast.makeText(
                        requireContext(),
                        "Error Occurred: ${response.message()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        new_album.setOnCheckedChangeListener { _, isChecked ->
            when (isChecked) {
                true ->  {
                    spinner_album.visibility = View.INVISIBLE
                    edit_album.visibility = View.VISIBLE
                }
                false -> {
                    spinner_album.visibility = View.VISIBLE
                    edit_album.visibility = View.INVISIBLE
                }
            }
        }

        btn_save_cam.setOnClickListener {
            save_panel_cam.visibility = View.VISIBLE
            btn_save_cam.visibility = View.INVISIBLE
            fab_camera.visibility = View.INVISIBLE
            edit_name.setText("")
            edit_album.setText("")
        }

        cancel_cam.setOnClickListener {
            save_panel_cam.visibility = View.INVISIBLE
            btn_save_cam.visibility = View.VISIBLE
            fab_camera.visibility = View.VISIBLE
            edit_name.setText("")
            edit_album.setText("")
        }

        fab_camera.setOnClickListener {
            save_panel_cam.visibility = View.INVISIBLE
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(cameraIntent)
        }

    }

    private fun getRandomString() : String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz0123456789"
        return (1..6)
            .map { charset.random() }
            .joinToString("")
    }
}